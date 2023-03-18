package ru.imine.server.core.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class AiMinePlayerEvent extends Event
{
    public final AiMinePlayerMP player;

    public AiMinePlayerEvent(AiMinePlayerMP player)
    {
        this.player = player;
    }

    public static class JoinEvent extends AiMinePlayerEvent
    {
        public JoinEvent(AiMinePlayerMP player)
        {
            super(player);
        }
    }

    public static class LeaveEvent extends AiMinePlayerEvent
    {
        public LeaveEvent(AiMinePlayerMP player)
        {
            super(player);
        }
    }
}
