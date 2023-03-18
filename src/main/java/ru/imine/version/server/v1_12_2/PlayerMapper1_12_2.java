package ru.imine.version.server.v1_12_2;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.ITextComponent;
import ru.imine.version.server.PlayerMapper;

public class PlayerMapper1_12_2 extends PlayerMapper
{
    @Override
    public void kick(EntityPlayerMP player, ITextComponent message)
    {
        player.connection.disconnect(message);
    }

    @Override
    public EntityPlayerMP get(NetHandlerPlayServer netHandler)
    {
        return netHandler.player;
    }
}