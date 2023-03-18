package ru.imine.shared.fancychat.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.server.fancychat.packet.FCPacketHandler;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatLine;
import ru.imine.shared.fancychat.chat.FancyChatSmileElement;
import ru.imine.shared.fancychat.chat.FancyChatElement;
import ru.imine.shared.fancychat.chat.util.ChatTag;
import ru.imine.shared.fancychat.smile.Smile;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.Location;
import ru.imine.shared.util.annotations.KeepClass;
import ru.imine.version.client.MinecraftMapper;

import javax.sound.sampled.Clip;
import java.io.*;

@KeepClass
public class FCPacket0ChatMessage implements IMessage
{
    public FancyChatLine line;

    public FCPacket0ChatMessage()
    {
    }

    public FCPacket0ChatMessage(FancyChatLine line)
    {
        this.line = line;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteArrayOutputStream byteSteam = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteSteam))
        {
            out.writeObject(line);
            buf.writeBytes(byteSteam.toByteArray());
        }
        catch (IOException e)
        {
            FancyChat.LOGGER.error("Faield to write packet!", e);
            Discord.instance.sendWarningLog("iFancyChat", "Faield to write packet!", e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int readable = buf.readableBytes();
        byte[] bytes = new byte[readable];
        buf.readBytes(bytes);
        try (ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bytes)))
        {
            line = (FancyChatLine) in.readObject();
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("", e);
        }
    }

    @SideOnly(Side.CLIENT)
    public static IMessage onMessageClient(FCPacket0ChatMessage message)
    {
        FancyChatLine line = message.line;
        if (line.tag.equals(ChatTag.GENERAL))
        {
            EntityPlayer me = MinecraftMapper.instance().getPlayer();
            if (line.sender.location.distance(new Location(me)) < 500 && !me.getUniqueID().equals(line.sender.uuid))
                line.tag = ChatTag.LOCAL;
        }
        ru.imine.client.fancychat.gui.GuiFancyChat.instance.addChatLine(line);

        for (FancyChatElement element : line)
        {
            if (element instanceof FancyChatSmileElement)
            {
                Smile smile = ((FancyChatSmileElement) element).getSmile();
                Clip sound = smile.getSound();
                if (sound != null)
                {
                    sound.start();
                    return null;
                }
            }
        }
        return null;
    }

    public static class Handler implements IMessageHandler<FCPacket0ChatMessage, IMessage>
    {
        @Override
        public IMessage onMessage(FCPacket0ChatMessage message, MessageContext ctx)
        {
            return ctx != null && ctx.side == Side.SERVER ? FCPacketHandler.handleChatMessage(message, ctx) : onMessageClient(message);
        }
    }
}
