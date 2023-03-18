package ru.imine.version.shared.v1_12_2;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import ru.imine.version.shared.WorldMapper;

public class WorldMapper1_12_2 extends WorldMapper
{
    @Override
    public int getChunkX(ChunkPos pos)
    {
        return pos.x;
    }

    @Override
    public int getChunkZ(ChunkPos pos)
    {
        return pos.z;
    }

    @Override
    public boolean spawnEntity(World world, Entity entity)
    {
        return world.spawnEntity(entity);
    }
}
