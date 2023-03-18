package ru.imine.version.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.ITextComponent;
import ru.imine.version.server.v1_12_2.PlayerMapper1_12_2;

public abstract class PlayerMapper
{
    protected static PlayerMapper instance;

    public static PlayerMapper instance()
    {
        if (instance==null)
            instance = new PlayerMapper1_12_2();
        return instance;
    }

    public abstract void kick(EntityPlayerMP player, ITextComponent message);
    public abstract EntityPlayerMP get(NetHandlerPlayServer netHandler);
}