package ru.imine.server.fancychat.smile;

import com.alibaba.fastjson.JSONObject;
import net.minecraft.world.World;
import ru.imine.server.core.AiMineCore;
import ru.imine.server.core.database.MySQL;
import ru.imine.server.core.database.Row;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.util.DiscordAPI;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatComponentElement;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyChatSmileElement;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.shared.fancychat.packet.FCPacket1SmileDictionary;
import ru.imine.shared.fancychat.smile.Category;
import ru.imine.shared.fancychat.smile.Smile;
import ru.imine.shared.fancychat.smile.SmilePack;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.JsonUtil;
import ru.imine.shared.util.Rarity;
import ru.imine.shared.util.collection.AutoCleanPlayerMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class SmileManager
{
    protected final static AutoCleanPlayerMap<PlayerSmileData> playerDatas = new AutoCleanPlayerMap<>();
    protected final static Map<String, Category> categories = new LinkedHashMap<>();

    private static MySQL mySQL;
    private static File smilatarRootFolder;
    private static URL smilatarRootUrl;

    public static void init()
    {
        mySQL = MySQL.getGameDataSQL();
        readSmiles();
    }

    public static void readSmiles()
    {
        categories.clear();
        try
        {
            JSONObject wholeData = JsonUtil.fromFile(new File("config/iMine/smiles.cfg").toPath());
            for (Map.Entry<String, Object> categoryEntry : wholeData.entrySet())
            {
                if (categoryEntry.getKey().equalsIgnoreCase("smilatarRootFolder"))
                {
                    smilatarRootFolder = new File(categoryEntry.getValue().toString());
                    continue;
                }
                if (categoryEntry.getKey().equalsIgnoreCase("smilatarRootUrl"))
                {
                    smilatarRootUrl = new URL(categoryEntry.getValue().toString());
                    continue;
                }
                JSONObject categoryData = (JSONObject) categoryEntry.getValue();
                byte sourceType = 0;
                String source = null;
                String displayName = null;
                if (categoryData.containsKey("data"))
                {
                    JSONObject data = categoryData.getJSONObject("data");
                    if (data.containsKey("url"))
                    {
                        sourceType = 1;
                        source = data.getString("url");
                    }
                    else if (data.containsKey("resource"))
                    {
                        sourceType = 2;
                        source = data.getString("resource");
                    }
                    if (data.containsKey("displayName"))
                        displayName = data.getString("displayName");
                }
                Category category = new Category(categoryEntry.getKey(), displayName != null ? displayName : categoryEntry.getKey(), sourceType, source);
                categories.put(categoryEntry.getKey(), category);
                for (Map.Entry<String, Object> smileEntry : categoryData.entrySet())
                {
                    if (smileEntry.getKey().equalsIgnoreCase("data"))
                        continue;
                    JSONObject smileData = (JSONObject) smileEntry.getValue();
                    sourceType = 0;
                    source = null;
                    if (smileData.containsKey("url"))
                    {
                        sourceType = 1;
                        source = smileData.getString("url");
                    }
                    else if (smileData.containsKey("resource"))
                    {
                        sourceType = 2;
                        source = smileData.getString("resource");
                    }
                    byte row_size = smileData.containsKey("rowsize") ? smileData.getByte("rowsize") : 1;
                    byte rarity = smileData.containsKey("rarity") ? smileData.getByte("rarity") : 1;
                    double scale = smileData.containsKey("scale") ? smileData.getByte("scale") : 1;
                    byte soundType = 0;
                    String sound = null;
                    if (smileData.containsKey("sound_url"))
                    {
                        soundType = 1;
                        sound = smileData.getString("sound_url");
                    }
                    else if (smileData.containsKey("sound_resource"))
                    {
                        soundType = 2;
                        sound = smileData.getString("sound_resource");
                    }
                    Smile smile = new Smile(category, smileEntry.getKey(), Rarity.getByValue(rarity), row_size,
                            sourceType, source, soundType, sound);
                }
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to read smiles config", e);
            Discord.instance.sendErrorLog("iFancyChat", "Failed to read smiles config", e);
        }
    }

    public static void openSmileCase(SmilePack smilePack, World world, AiMinePlayerMP player)
    {
        if (player==null)
            throw new NullPointerException("Попытка открыть смайлик без AiMinePlayerMP");
        smilePack.openCase(player);
        SmileManager.refreshPlayerSmiles(player);
    }

    public static void provideSmile(AiMinePlayerMP player, Rarity rarity)
    {
        try
        {
            FancyChat.LOGGER.info("Providing new smile for "+player+"!");
            FancyChat.LOGGER.info("  rarity: "+rarity.getDisplayName());
            List<Smile> smiles = SmileManager.getAllSmilesByRatity(rarity);
            PlayerSmileData data = SmileManager.getPlayerSmileData(player);
            FancyChat.LOGGER.info("  he already has "+data.getUnlockedSmiles());
            smiles.removeAll(data.getUnlockedSmiles());
            FancyChat.LOGGER.info("  and he don't have "+smiles);
            if (smiles.isEmpty())
            {
                int moneyReward = rarity.value * 322;
                player.changeMoney(moneyReward);
                FancyChat.LOGGER.info("  provided with money!");
                FancyChatLine line = FancyChat.stringToChatLine(
                        "Ты должен был получить "+rarity.getDisplayName()+" смайлик, но у тебя они уже все есть! Выдаем "+moneyReward);
                line.add(new FancyChatSmileElement("imine","coin"));
                player.sendMessage(line);
            }
            else
            {
                int random = new Random().nextInt(smiles.size());
                Smile reward = smiles.get(random);
                FancyChat.LOGGER.info("  providing him "+reward);
                if (SmileManager.provideInnerSmile(player.getPlayerId(),reward))
                {
                    FancyChat.LOGGER.info("  " + reward + " is provided!");
                    FancyLine line = new FancyChatLine(null, System.currentTimeMillis(), ChatBcgColor.SERVER, ChatTag.SERVER)
                            .with(player.getDisplayName())
                            .with(" открыл новый " + reward.rarity.getDisplayName() + " смайлик: ")
                            .with(new FancyChatSmileElement(reward.category.name, reward.name))
                            .with(new FancyChatComponentElement(" из категории "))
                            .with(new FancyChatComponentElement(reward.category.displayName));
                    for (AiMinePlayerMP otherPlayer : AiMinePlayerMP.getPlayers())
                    {
                        otherPlayer.sendMessage(line);
                    }
                }
                else
                {
                    FancyChat.stringToChatLine("§cЧто-то пошло серьезно не так! Обратись к Администрации. Код ошибки #sp1");
                }
            }
        }
        catch (Exception e)
        {
            FancyChat.stringToChatLine("§cЧто-то пошло серьезно не так! Обратись к Администрации. Код ошибки #sp2");
            FancyChat.LOGGER.error("FATAL ERROR! Failed to proceed providing smiles!", e);
            DiscordAPI.getInstance().sendDonateErrorLog("Failed to proceed providing smiles!", e);
        }
    }

    private static boolean provideInnerSmile(long uniqueID, Smile smile)
    {
        try
        {
            int rows = mySQL.updateSQL("INSERT INTO `FancyChatSmiles` (`Player`, `Server`, `Category`,`Smile`) VALUES (?,?,?,?)",
                    uniqueID, AiMineCore.getServerName(), smile.category.name, smile.name);
            if (rows==0)
                throw new Exception("rows==0 for "+uniqueID+" and "+smile);
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("FATAL ERROR! Failed to provide smile '"+smile+"' for "+uniqueID, e);
            DiscordAPI.getInstance().sendDonateErrorLog("Failed to provide smile '"+smile+"' for "+uniqueID, e);
            return false;
        }
        return true;
    }

    public static void refreshPlayerSmiles(AiMinePlayerMP player)
    {
        PlayerSmileData data = new PlayerSmileData(player.getPlayerId());
        try
        {
            for (Row row : mySQL.querySQL("SELECT * FROM `FancyChatSmiles` WHERE `Player`=? AND Server=?",
                    player.getPlayerId(), AiMineCore.getServerName()))
            {
                String category = row.getString("Category");
                String name = row.getString("Smile");
                Smile smile = getSmile(category,name);
                if (smile!=null)
                    data.unlockedSmiles.add(smile);
                else
                    DiscordAPI.instance.sendErrorLog("iFancyChat",String.format("У %s анлокнут несуществующий смайлик %s:%s", player.getName(),category,name));
            }
        }
        catch (SQLException e)
        {
            FancyChat.LOGGER.error("Failed to get smile data from MySQL for player " + player.getEntity().getDisplayName(), e);
            Discord.instance.sendWarningLog("iFancyChat", "Failed to get smile data from MySQL for player " + player.getEntity().getDisplayName(), e);
        }
        playerDatas.put(player, data);
        sendSmilesToPlayer(player, data);
    }

    private static void sendSmilesToPlayer(AiMinePlayerMP player, PlayerSmileData data)
    {
        FancyChat.network.sendTo(new FCPacket1SmileDictionary(categories.values(), data.getUnlockedSmiles()), player.getEntity());
    }

    public static PlayerSmileData getPlayerSmileData(AiMinePlayerMP playerId)
    {
        return playerDatas.get(playerId);
    }

    public static Category getCategoryByName(String name)
    {
        return categories.get(name);
    }

    public static List<Category> getAllCategories()
    {
        return new ArrayList<>(categories.values());
    }

    public static Smile getSmile(String catetory, String name)
    {
        if (catetory==null || name==null)
            return null;
        Category category = categories.get(catetory);
        if (category == null)
            return null;
        return category.getSmileByName(name);
    }

    public static List<Smile> getAllSmiles()
    {
        List<Smile> result = new ArrayList<>();
        for (Category category : categories.values())
            result.addAll(category.getSmiles());
        return result;
    }

    public static List<Smile> getAllSmilesByRatity(Rarity rarity)
    {
        List<Smile> result = new ArrayList<>();
        for (Category category : categories.values())
            for (Smile smile  : category.getSmiles())
            {
                if (rarity.equals(smile.rarity))
                    result.add(smile);
            }
        return result;
    }

    public static Smile getSmilatar(String username)
    {
        String addPath = AiMineCore.getServerName().toLowerCase() + "/" + username + ".png";
        try
        {
            if (AiMineCore.getServerName().equalsIgnoreCase("iLocalPingas"))
                return null;
            if (username.equalsIgnoreCase("DrEggman") || username.equalsIgnoreCase("DrRobotnik"))
            {
                Smile pingas = getSmile("memes_big", "pingas");
                return new Smile(getCategoryByName("memes"), "pingas", Rarity.SPECIAL, (byte) 1,
                        pingas.imageSourceType, pingas.imageSource, pingas.soundSourceType, pingas.soundSource);
            }
            if (new File(smilatarRootFolder, AiMineCore.getServerName().toLowerCase() + "/" + username + ".png").exists())
                return new Smile(getCategoryByName("smilatar"), username, Rarity.SPECIAL, (byte) 1,
                        (byte) 1, new URL(smilatarRootUrl, addPath).toExternalForm(), (byte) 0, null);
        }
        catch (MalformedURLException e)
        {
            FancyChat.LOGGER.error("Failed to generate smilatar for " + username + ": smilatarRootUrl=" + smilatarRootUrl + " and addPath=" + addPath, e);
        }
        return null;
    }
}