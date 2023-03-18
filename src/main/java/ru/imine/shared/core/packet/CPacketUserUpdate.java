package ru.imine.shared.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.imine.client.core.packet.PacketHandler;
import ru.imine.shared.AiMine;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.annotations.KeepClass;

import java.io.*;

@KeepClass
public class CPacketUserUpdate implements IMessage
{
    public AiMineUser user;
    public int reason;

    public CPacketUserUpdate()
    {
    }

    public CPacketUserUpdate(AiMineUser user, int reason)
    {
        this.user = user;
        this.reason = reason;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteArrayOutputStream byteSteam = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteSteam))
        {
            out.writeObject(user);
            out.writeInt(reason+1);
            out.flush();
            buf.writeBytes(byteSteam.toByteArray());
        }
        catch (IOException e)
        {
            AiMine.LOGGER.error("Failed to write packet", e);
            Discord.instance.sendErrorLog("iMineCore", "Failed to write packet", e);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        int readable = buf.readableBytes();
        byte[] bytes = new byte[readable];
        buf.readBytes(bytes);
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes)))
        {
            user = (AiMineUser) in.readObject();
            reason = in.readInt()-1;
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Failed to read packet", e);
        }
    }

    public static class Handler implements IMessageHandler<CPacketUserUpdate, IMessage>
    {
        public IMessage onMessage(CPacketUserUpdate message, MessageContext ctx)
        {
            return PacketHandler.handleUserUpdate(message, ctx);
        }
    }
}

