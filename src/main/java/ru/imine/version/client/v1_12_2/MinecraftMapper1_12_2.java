package ru.imine.version.client.v1_12_2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import ru.imine.version.client.MinecraftMapper;

public class MinecraftMapper1_12_2 extends MinecraftMapper
{
    @Override
    public EntityPlayerSP getPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getWorld()
    {
        return Minecraft.getMinecraft().world;
    }
}
