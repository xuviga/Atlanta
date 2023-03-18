package ru.imine.shared.fancychat.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.imine.client.fancychat.smile.SmileDictionary;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.smile.Category;
import ru.imine.shared.fancychat.smile.Smile;
import ru.imine.shared.util.Discord;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FCPacket1SmileDictionary implements IMessage
{
    public List<Category> categories;
    public List<Smile> unlockedSmiles;

    public FCPacket1SmileDictionary()
    {
    }

    public FCPacket1SmileDictionary(Collection<Category> categories, Collection<Smile> unlockedNames)
    {
        this.categories=new ArrayList<>(categories);
        this.unlockedSmiles=new ArrayList<>(unlockedNames);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteArrayOutputStream byteSteam = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteSteam))
        {
            out.writeInt(categories.size());
            for (Category category : categories)
                out.writeObject(category);
            out.writeInt(unlockedSmiles.size());
            for (Smile smile : unlockedSmiles)
            {
                out.writeUTF(smile.category == null ? "<null>" : smile.category.name);
                out.writeUTF(smile.name);
            }
            out.writeObject(categories);
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
            int size = in.readInt();
            categories = new ArrayList<>(size);
            for (int i = 0; i < size; i++)
            {
                Category category = (Category) in.readObject();
                categories.add(category);
                for (Smile smile : category.getSmiles())
                {
                    smile.category = category;
                    smile.loadImage();
                }
            }
            SmileDictionary.updateInfo(categories);
            size = in.readInt();
            for (int i = 0; i < size; i++)
            {
                String a = in.readUTF();
                String b = in.readUTF();
                Smile smile = SmileDictionary.getSmile(a,b);
                if (smile!=null)
                    smile.unlocked = true;
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("", e);
        }
    }

    public static class Handler implements IMessageHandler<FCPacket1SmileDictionary, IMessage>
    {
        @Override
        public IMessage onMessage(FCPacket1SmileDictionary message, MessageContext ctx)
        {
            for (Category category : message.categories)
            {
                category.loadImage();
                for (Smile smile : category.getSmiles())
                {
                    smile.category = category;
                    smile.loadImage();
                }
            }

            SmileDictionary.updateInfo(message.categories);
            return null;
        }
    }
}
