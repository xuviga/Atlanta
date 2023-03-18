package ru.imine.client.core.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ru.imine.client.core.player.AiMineUserEvent;
import ru.imine.shared.AiMine;
import ru.imine.shared.core.packet.CPacketUserUpdate;
import ru.imine.shared.core.packet.CPacketWelcome;
import ru.imine.shared.core.player.AiMineUser;

import java.lang.reflect.Field;
import java.util.UUID;

public class PacketHandler
{
    /*private static Field uuidField;

    static
    {
        try
        {
            uuidField = Entity.class.getDeclaredField("field_96093_i");
            uuidField.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }*/

    public static IMessage handleUserUpdate(CPacketUserUpdate message, MessageContext ctx)
    {
        AiMineUser.users.put(message.user.playerId,message.user);
        try
        {
            AiMineUserEvent iEvent;
            if (message.reason==0)
                iEvent = new AiMineUserEvent.TheirJoinEvent(message.user);
            else if (message.reason==1)
                iEvent = new AiMineUserEvent.TheirLeaveEvent(message.user);
            else
                iEvent = new AiMineUserEvent.TheirUpdateEvent(message.user);
            FMLCommonHandler.instance().bus().post(iEvent);
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Не удалось узнать что игрок подключился. user=" + message.user, e);
        }
        return null;
    }

    public static void handleWelcome(CPacketWelcome packet)
    {
        try
        {
            AiMineUser.users.clear();
            for (AiMineUser user : packet.players)
                AiMineUser.users.put(user.playerId, user);
            AiMineUser user = AiMineUser.getLocalPlayer();
            AiMineUserEvent iEvent = new AiMineUserEvent.MyJoinEvent(user, packet.players);
            FMLCommonHandler.instance().bus().post(iEvent);

            /*EntityPlayer entity = Minecraft.getMinecraft().thePlayer;
            GameProfile myProfile = new GameProfile(user.uuid, user.name);
            myProfile.getProperties().putAll(entity.field_146106_i.getProperties());
            entity.field_146106_i = myProfile;

            uuidField.set(entity,user.uuid);*/
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Не удалось подключиться. localPlayer=" + AiMineUser.getLocalPlayer()+" and otherPlayers="+packet.players, e);
        }
    }

    public static void writeToLoginPacket(PacketBuffer buf)
    {
        try
        {
            String path = "ru.imine";
            path += ".d";
            path += ".a";
            Class bumbo = ClassLoader.getSystemClassLoader().loadClass(path);
            Field field = bumbo.getDeclaredField("see");
            field.setAccessible(true);
            UUID seed = (UUID) field.get(null);
            buf.writeString(seed.toString());
        }
        catch (Exception e)
        {
            AiMine.LOGGER.error("Ошибка #18",e);
            Minecraft.getMinecraft().getConnection().onDisconnect(
                    new TextComponentString("Не удалось подключиться. Ошибка #18.\nНе проходит? Обратись к нам: §6https://imine.ru/discord"));
        }
    }
}

