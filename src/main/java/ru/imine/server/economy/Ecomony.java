package ru.imine.server.economy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.imine.server.core.player.AiMinePlayerEvent;
import ru.imine.server.core.player.AiMinePlayerMP;

public class Ecomony
{
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(Ecomony.class);
    }

    @SubscribeEvent
    public static void onEvent(AiMinePlayerEvent.LeaveEvent event)
    {
        AiMinePlayerMP player = event.player;
    }

    @SubscribeEvent
    public static void onEvent(AiMinePlayerEvent.JoinEvent event)
    {
        AiMinePlayerMP player = event.player;
    }

    private Ecomony() { }
}