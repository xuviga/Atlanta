package ru.imine.shared.fancychat;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.shared.fancychat.smile.SmilePack;
import ru.imine.shared.fancychat.chat.FancyChatComponentElement;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.shared.fancychat.item.ItemSmileCase;
import ru.imine.shared.fancychat.packet.FCPacket0ChatMessage;
import ru.imine.shared.fancychat.packet.FCPacket1SmileDictionary;

public class FancyChat
{
    public static final Logger LOGGER = LogManager.getLogger("iFancyChat");
    public static final Logger CHAT_LOGGER = LogManager.getLogger("CHAT");

    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper("ifancychat");

    public static void preInit(FMLPreInitializationEvent e)
    {
        network.registerMessage(FCPacket0ChatMessage.Handler.class, FCPacket0ChatMessage.class, 0, Side.CLIENT);
        network.registerMessage(FCPacket0ChatMessage.Handler.class, FCPacket0ChatMessage.class, 0, Side.SERVER);
        network.registerMessage(FCPacket1SmileDictionary.Handler.class, FCPacket1SmileDictionary.class, 1, Side.CLIENT);

        for (SmilePack smilePack : SmilePack.values())
            ItemSmileCase.generate(smilePack);
    }

    public static FancyChatLine lineToChatLine(FancyLine line)
    {
        return new FancyChatLine(line, null, System.currentTimeMillis(), ChatBcgColor.SERVER, ChatTag.SERVER);
    }

    public static FancyChatLine stringToChatLine(String message)
    {
        return componentToChatLine(new TextComponentString(message));
    }

    public static FancyChatLine componentToChatLine(ITextComponent component)
    {
        FancyChatLine line = new FancyChatLine(null, System.currentTimeMillis(), ChatBcgColor.SERVER, ChatTag.SERVER);
        line.add(new FancyChatComponentElement(component));
        return line;
    }
}
