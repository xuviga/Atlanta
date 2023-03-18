package ru.imine.shared.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public abstract class IMineMessage implements IMessage
{
    private static final Logger LOGGER = LogManager.getLogger("IMineMessage");

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            int readable = buf.readableBytes();
            byte[] bytes = new byte[readable];
            buf.readBytes(bytes);
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes)))
            {
                fromBytes(input);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bos))
        {
            toBytes(output);
            output.flush();
            buf.writeBytes(bos.toByteArray());
            bos.close();
        }
        catch (Exception e)
        {
            LOGGER.error(e);
        }
    }

    public abstract void fromBytes(ObjectInputStream input) throws IOException, ClassNotFoundException;
    public abstract void toBytes(ObjectOutputStream output) throws IOException;
}
