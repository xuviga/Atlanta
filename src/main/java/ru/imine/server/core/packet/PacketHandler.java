package ru.imine.server.core.packet;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.shared.util.RobertoGarbagio;

import java.util.Objects;
import java.util.UUID;

public class PacketHandler
{
    private static Logger logger = LogManager.getLogger("PacketHandler");

    public static void readFromLoginPacket(GameProfile gameProfile, PacketBuffer buf) throws Exception
    {
        try
        {
            RobertoGarbagio.LOGGER.info("gameProfile=" + gameProfile);
            UUID seed = UUID.fromString(buf.readString(64));
            AiMinePlayerMP sender = AiMinePlayerMP.getOffline(gameProfile.getName());
            if (sender==null)
                throw new NullPointerException("sender not found for gameProfile="+gameProfile);
            RobertoGarbagio.LOGGER.info(">for " + sender + ": " + seed + " vs " + sender.getClientSeed());
            if (!Objects.equals(sender.getClientSeed(), seed))
            {
                logger.error("Неверный сид у игрока "+sender+": "+seed+" vs "+sender.getClientSeed());
                throw new IllegalAccessException("Неверный сид.");
                //throw new Exception("Не удалось подключиться. Ошибка #21.\nНе проходит? Обратись к нам: §6https://imine.ru/discord");
            }
        }
        catch (IllegalAccessException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Ошибка чтения логин-пакета",e);
            throw e;
        }
    }
}