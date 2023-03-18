package ru.imine.version.server.v1_12_2;

import net.minecraft.world.WorldServer;
import ru.imine.version.server.ServerMapper;

public class ServerMapper1_12_2 extends ServerMapper
{
    @Override
    public WorldServer[] getWorlds()
    {
        return SERVER.worlds;
    }
}