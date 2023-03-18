package ru.imine.client.core.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import ru.imine.shared.core.player.AiMineUser;

import java.util.List;

@Cancelable
public class AiMineUserEvent extends Event
{
    public final AiMineUser player;

    public AiMineUserEvent(AiMineUser player)
    {
        this.player = player;
    }

    public static class MyJoinEvent extends AiMineUserEvent
    {
        public final List<AiMineUser> otherPlayers;

        public MyJoinEvent(AiMineUser me, List<AiMineUser> otherPlayers)
        {
            super(me);
            this.otherPlayers = otherPlayers;
        }
    }

    public static class TheirJoinEvent extends AiMineUserEvent
    {
        public TheirJoinEvent(AiMineUser player)
        {
            super(player);
        }
    }

    public static class TheirLeaveEvent extends AiMineUserEvent
    {
        public TheirLeaveEvent(AiMineUser player)
        {
            super(player);
        }
    }

    public static class TheirUpdateEvent extends AiMineUserEvent
    {
        public TheirUpdateEvent(AiMineUser player)
        {
            super(player);
        }
    }
}
