package ru.imine.version.server.v1_12_2.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.database.MySQL;
import ru.imine.server.core.database.Row;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.util.AiCommand;
import ru.imine.server.util.DiscordAPI;
import ru.imine.server.webapi.WebAPI;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.version.shared.ItemMapper;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonateCommand extends AiCommand
{
    private static final Pattern NUMBERIC_PATTERN = Pattern.compile("([0-9]+):([0-9]+)");
    @Override
    public String getName()
    {
        return "donate";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "Выдает купленные на сайте предметы, пока есть место в инвентаре";
    }

    @Override
    public List<String> getAliases()
    {
        return Collections.singletonList("donat");
    }


    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] args)
    {
        AiMinePlayerMP player = AiMinePlayerMP.get(sender);
        long playerId = player.getPlayerId();
        String server = AiMineCore.getServerName();
        String request = "SELECT `Id`,`ItemStack` FROM `bought_items` WHERE `Player`=? AND `Server` =?";
        List<Row> rows;
        try
        {
            MySQL.getGameDataSQL().getConnection().setAutoCommit(false);
            rows = MySQL.getGameDataSQL().querySQL(request,playerId,server);
            if (rows.isEmpty())
            {
                player.sendMessage(FancyChat.stringToChatLine(
                        "Донатные привелегии, цветные сообщения, и смайлики на http://imine.ru/cabinet/"));
                return;
            }
            for (Row row : rows)
            {
                String nbtStr = row.getString("ItemStack");
                BigInteger itemId = row.getBigInteger("Id");
                ItemStack itemStack = ItemMapper.instance().getEmptyStack();
                try
                {
                    JsonObject nbt = new JsonParser().parse(nbtStr).getAsJsonObject();

                    String stringId="air";
                    int numberId=0;
                    int count=1;
                    short damage=0;
                    NBTTagCompound tag=null;
                    for (Map.Entry<String, JsonElement> entry : nbt.entrySet())
                    {
                        if (entry.getKey().equalsIgnoreCase("id"))
                        {
                            String value = entry.getValue().getAsString();
                            Matcher matcher = NUMBERIC_PATTERN.matcher(value);
                            if (matcher.matches())
                            {
                                numberId = Integer.parseInt(matcher.group(1));
                                damage = (short)Integer.parseInt(matcher.group(2));
                            }
                            else
                            {
                                try
                                {
                                    numberId = Integer.parseInt(value);
                                }
                                catch (Exception ignored)
                                {
                                    stringId = value;
                                }
                            }
                        }
                        if (entry.getKey().equalsIgnoreCase("count"))
                            count = entry.getValue().getAsInt();
                        if (entry.getKey().equalsIgnoreCase("damage"))
                            damage = entry.getValue().getAsShort();
                        if (entry.getKey().equalsIgnoreCase("tag"))
                            tag = JsonToNBT.getTagFromJson(entry.getValue().getAsString());
                    }
                    if (numberId!=0)
                    {
                        Item item = Item.getItemById(numberId);
                        Block block = Block.getBlockById(numberId);
                        for (; count > 0; count -= 64)
                        {
                            if (item != null)
                                itemStack = new ItemStack(item, Math.min(64, count), damage < 0 ? 0 : damage);
                            else if (block != null)
                                itemStack = new ItemStack(block, Math.min(64, count), damage < 0 ? 0 : damage);
                            else
                                throw new Exception("Не найден предмет или блок: "+numberId);
                            if (tag != null)
                                itemStack.setTagCompound(tag);
                        }
                    }
                    else if (!stringId.equalsIgnoreCase("air") && !stringId.equals("0"))
                    {
                        ResourceLocation resourceLocation = new ResourceLocation(stringId);
                        Item item = Item.REGISTRY.getObject(resourceLocation);
                        Block block = item==null ? Block.REGISTRY.getObject(resourceLocation) : null;
                        for (; count > 0; count -= 64)
                        {
                            if (item != null)
                                itemStack = new ItemStack(item, Math.min(64, count), damage < 0 ? 0 : damage);
                            else if (block!=null)
                                itemStack = new ItemStack(block, Math.min(64, count), damage < 0 ? 0 : damage);
                            else
                                throw new Exception("Не найден предмет или блок: "+stringId);
                            if (tag != null)
                                itemStack.setTagCompound(tag);
                        }
                    }
                }
                catch (Exception e)
                {
                    if (ItemMapper.instance().isEmpty(itemStack))
                    {
                        WebAPI.LOGGER.error("Не найдет предмен с nbt={} для игрока {}", nbtStr, player.getName(), e);
                        DiscordAPI.getInstance().sendDonateErrorLog(String.format("Не найдет предмет с nbt=%s для игрока %s", nbtStr, player), e);
                        player.sendMessage("§cОдин из предметов не удалось выдать! Обратись к Администрации! Код ошибки #dc1");
                    }
                    else
                    {
                        WebAPI.LOGGER.error("Failed to parse item from nbt. Player=" + player + " Id=" + itemId + "; nbt=" + nbtStr, e);
                        DiscordAPI.getInstance().sendDonateErrorLog("Failed to parse item from nbt. Player=" + player + "; Id=" + itemId + "; nbt=" + nbtStr, e);
                        player.sendMessage("§cОдин из предметов не удалось выдать! Обратись к Администрации! Код ошибки #dc2");
                    }
                    continue;
                }
                if (player.getEntity().inventory.addItemStackToInventory(itemStack))
                {
                    try
                    {
                        if (ItemMapper.instance().isEmpty(itemStack))
                            throw new Exception();
                        MySQL.getGameDataSQL().updateSQL("DELETE FROM `bought_items` WHERE `Id`=?", itemId.toString());
                        ITextComponent component = new TextComponentString("Выдан предмет: ");
                        component.appendSibling(new TextComponentTranslation(itemStack.getTranslationKey()));
                        player.getEntity().sendMessage(component);
                        DiscordAPI.getInstance().sendDonateLog("Игрок "+ player.getName() +" забрал покупку: "+nbtStr);
                    }
                    catch (Exception e)
                    {
                        WebAPI.LOGGER.error("Failed to delete donate item from SQL: Player="+player+"; id=" + itemId + "; nbt=" + nbtStr, e);
                        DiscordAPI.getInstance().sendDonateErrorLog("Failed to delete donate item from SQL: id=" + itemId + "; player=" + player + "; nbt=" + nbtStr, e);
                        player.sendMessage(FancyChat.stringToChatLine("Что-то пошло не так при добавлении предмета! Обратись к Администрации! Код ошибки #dc3"));
                    }
                }
                else
                {
                    ITextComponent component = new TextComponentString("Инвентарь полон! Не удалось выдать предмет: ");
                    component.appendSibling(new TextComponentTranslation(itemStack.getTranslationKey()));
                    player.getEntity().sendMessage(component);
                }
            }
        }
        catch (SQLException e)
        {
            WebAPI.LOGGER.error("Failed to make SQL request about donate items for "+player, e);
            DiscordAPI.getInstance().sendDonateErrorLog("Failed to make SQL request about donate items for "+player, e);
        }
        finally
        {
            try
            {
                MySQL.getGameDataSQL().getConnection().setAutoCommit(true);
            }
            catch (SQLException e)
            {
                WebAPI.LOGGER.error("Failed to setAutoCommit for SQL request about donate items for "+player, e);
                DiscordAPI.getInstance().sendDonateErrorLog("Failed to setAutoCommit for SQL request about donate items for "+player, e);
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender p_71519_1_)
    {
        return p_71519_1_ instanceof EntityPlayerMP;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_)
    {
        return false;
    }

}
