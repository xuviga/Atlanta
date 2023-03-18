package ru.imine.version.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import ru.imine.version.client.v1_12_2.MinecraftMapper1_12_2;

public abstract class MinecraftMapper
{
    protected static MinecraftMapper instance;

    public static MinecraftMapper instance()
    {
        if (instance==null)
            instance = new MinecraftMapper1_12_2();
        return instance;
    }

    public abstract EntityPlayerSP getPlayer();
    public abstract World getWorld();
}
