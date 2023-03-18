package ru.imine.server.fancychat.packet;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.core.player.PlayerRank;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.*;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.shared.fancychat.smile.Smile;
import ru.imine.shared.util.Discord;
import ru.imine.version.server.PlayerMapper;

public class FCPacketHandler
{
    private static final ITextComponent MESSAGE_DELIMITER = new TextComponentString(" §l»§r ");

    public static IMessage handleChatMessage(FCPacket0ChatMessage message, MessageContext ctx)
    {
        FancyChatLine line = message.line;
        AiMinePlayerMP sender = AiMinePlayerMP.get(PlayerMapper.instance().get(ctx.getServerHandler()));
        line.sender = sender.asUser(sender);

        /*String plainText = line.toPlainText();
        if (plainText.startsWith("/"))
        {
            String[] splits = plainText.substring(1).split(" ");
            for (AiCommand command : AiCommand.COMMANDS)
            {
                List<String> aliases = CommandMapper.instance().getAliases(command);
                if (splits[0].equalsIgnoreCase(CommandMapper.instance().getName(command))
                        || (aliases != null && aliases.stream().anyMatch(it -> it.equalsIgnoreCase(splits[0]))))
                {
                    try
                    {
                        if (command.checkPermission(FMLCommonHandler.instance().getMinecraftServerInstance(),sender.getEntity()))
                        {
                            String[] args = splits.length == 1 ? new String[0] : Arrays.copyOfRange(splits, 1, splits.length);
                            command.execute(FMLCommonHandler.instance().getMinecraftServerInstance(),sender.getEntity(), args);
                        }
                        else
                            sender.sendMessage("§7Нет доступа!");
                    }
                    catch (Exception e)
                    {
                        sender.sendMessage("§cЧто-то пошло не так при выполнении команды");
                        FancyChat.LOGGER.error("Ошибка выполнения команды '{}':", plainText, e);
                    }
                    return null;
                }
            }

            ctx.getServerHandler().processChatMessage(new CPacketChatMessage(plainText));
            return null;
        }*/

        for (FancyChatElement element : line)
        {
            if (element instanceof FancyChatSmileElement && !(element instanceof FancyChatSmilatarElement))
            {
                FancyChatSmileElement smileElement = (FancyChatSmileElement) element;
                Smile smile = smileElement.getSmile();
                if (smile == null)
                {
                    Discord.instance.sendErrorLog("FancyChat", "Smile=null for " + element + "!\nLine=" + line);
                }
                else if (!SmileManager.getPlayerSmileData(sender).hasSmile(smile)) //todo
                {
                    Discord.instance.sendErrorLog("FancyChat [HAX?]", "Looks like " + sender + " is cheating!\n" +
                            "He tried to use smile " + smile + ", but he doesn't have it!\nLine=" + line);
                    return null;
                }
            }
            if (element.getColor() != 0xFFFFFFFF && !sender.hasRank(PlayerRank.DIAMOND))
            {
                element.setColor(0xFFFFFFFF);
                String haxMessage = "Looks like " + sender + " is cheating!\n" +
                        "He tried to color chat text " + element + ", but he doesn't have diamond group!\nLine=" + line;
                FancyChat.LOGGER.error(haxMessage);
                Discord.instance.sendErrorLog("FancyChat [HAX?]", haxMessage);
            }
        }

        if (line.bcgColor != ChatBcgColor.DEFAULT && !sender.hasRank(PlayerRank.EMERALD))
        {
            String haxMessage = "Looks like " + sender + " is cheating!\n" +
                    "He tried to use bcg color " + line.bcgColor + ", but he doesn't have emerald group!\nLine=" + line;
            FancyChat.LOGGER.error(haxMessage);
            Discord.instance.sendErrorLog("FancyChat [HAX?]", haxMessage);
            line.bcgColor = ChatBcgColor.DEFAULT;
        }

        FancyChat.CHAT_LOGGER.info(sender.getName() + "»" + line.toPlainText());

        line.add(0, new FancyChatComponentElement(MESSAGE_DELIMITER));
        FancyChatElement nickElement = new FancyChatComponentElement(PlayerMapper.instance().get(ctx.getServerHandler()).getDisplayName());
        nickElement.setColor(sender.getNickColor());
        line.add(0, nickElement);
        Smile icon = SmileManager.getSmilatar(sender.getName());
        if (icon != null)
            line.add(0, new FancyChatSmilatarElement(icon));
        else
        {
            icon = sender.getPlayerRole().icon;
            if (icon!=null)
            {
                FancyChatSmileElement element = new FancyChatSmileElement(icon.category.name, icon.name);
                element.setHover(FancyChat.stringToChatLine(sender.getPlayerRole().displayName));
                line.add(0, element);
            }
            if (icon == null)
            {
                icon = sender.getPlayerRank().icon;
                if (icon!=null)
                {
                    FancyChatSmileElement element = new FancyChatSmileElement(icon.category.name, icon.name);
                    element.setHover(FancyChat.stringToChatLine(sender.getPlayerRank().displayName));
                    line.add(0, element);
                }
            }
        }
        line.timeStamp = System.currentTimeMillis();
        FancyChat.network.sendToAll(new FCPacket0ChatMessage(line));
        return null;
    }
}