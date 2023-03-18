package ru.imine.server.fancychat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.imine.server.core.player.AiMinePlayerEvent;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.version.server.CommandMapper;

public class FancyChat
{
    public static void preInit(FMLPreInitializationEvent e)
    {
        SmileManager.init();
        MinecraftForge.EVENT_BUS.register(FancyChat.class);
    }

    public static void init(FMLServerStartingEvent e)
    {
        CommandMapper.instance().registerCommands(e);
    }

    @SubscribeEvent
    public static void onEvent(AiMinePlayerEvent.JoinEvent event)
    {
        SmileManager.refreshPlayerSmiles(event.player);
    }
}
