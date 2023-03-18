package ru.imine.server.core;

import com.alibaba.fastjson.JSONObject;
import net.luckperms.api.node.types.DisplayNameNode;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import ru.imine.server.core.player.AiMinePlayerEvent;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.shared.AiMine;
import ru.imine.shared.core.packet.CPacketUserUpdate;
import ru.imine.shared.core.packet.CPacketWelcome;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.JsonUtil;
import ru.imine.shared.util.RobertoGarbagio;
import ru.imine.version.server.PlayerMapper;
import ru.imine.version.server.v1_12_2.command.MoneyCommand;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class AiMineCore
{
    public static DisplayNameNode.Builder getPlayerRole;
    protected static JSONObject config;

    static
    {
        try
        {
            config = JsonUtil.fromFile(new File("config/iMine/core.cfg").toPath());
        }
        catch (IOException e)
        {
            AiMine.LOGGER.error("Failed to read rules config. Shutdown!", e);
            Discord.instance.sendErrorLog("iMineCore", "Failed to read rules config. Shutdown!", e);
            FMLCommonHandler.instance().exitJava(1, false);
        }
    }

    public static void preInit(FMLPreInitializationEvent e)
    {
        AiMineCore instance = new AiMineCore();
        FMLCommonHandler.instance().bus().register(instance);
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static void starting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new MoneyCommand());
    }

    public static boolean isTest()
    {
        return !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer();
    }

    public static String getServerName()
    {
        return config.getJSONObject("settings").getString("servername");
    }

    public static JSONObject getConfig()
    {
        return config;
    }


    public static void updatePlayerInfo(AiMinePlayerMP player)
    {
        ru.imine.shared.core.AiMineCore.network.sendTo(new CPacketUserUpdate(player.asUser(player),2), player.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        AiMinePlayerMP player = AiMinePlayerMP.addFromHere(event);
        if (player==null)
        {
            PlayerMapper.instance().kick((EntityPlayerMP) event.player,
                    new TextComponentString("§cПроблемы авторизации. Обратись к Администрации.\n      §bhttp://iMine.ru"));
            RobertoGarbagio.LOGGER.info("asd");
            event.setResult(Event.Result.DENY);
        }
        else
        {
            ru.imine.shared.core.AiMineCore.network.sendTo(new CPacketWelcome(AiMinePlayerMP.getUsers(player)), player.getEntity());
            MinecraftForge.EVENT_BUS.post(new AiMinePlayerEvent.JoinEvent(player));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(PlayerEvent.PlayerLoggedOutEvent event)
    {
        AiMinePlayerMP player = AiMinePlayerMP.get(event.player);
        if (player!=null)
        {
            AiMinePlayerEvent iEvent = new AiMinePlayerEvent.LeaveEvent(player);
            FMLCommonHandler.instance().bus().post(iEvent);
            AiMinePlayerMP.removeFromHere(event);
        }
    }
}