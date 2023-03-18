package ru.imine.version.shared;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import ru.imine.version.shared.v1_12_2.WorldMapper1_12_2;

public abstract class WorldMapper
{
    protected static WorldMapper instance;

    public static WorldMapper instance()
    {
        if (instance==null)
            instance = new WorldMapper1_12_2();
        return instance;
    }

    public abstract int getChunkX(ChunkPos pos);
    public abstract int getChunkZ(ChunkPos pos);
    public abstract boolean spawnEntity(World world, Entity entity);
}
