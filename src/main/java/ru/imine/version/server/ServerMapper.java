package ru.imine.version.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.version.server.v1_12_2.ServerMapper1_12_2;

public abstract class ServerMapper
{
    protected static Logger logger = LogManager.getLogger("GuiMapper");
    protected static final MinecraftServer SERVER = FMLCommonHandler.instance().getMinecraftServerInstance();

    protected static ServerMapper instance;

    public static ServerMapper instance()
    {
        if (instance==null)
            instance = new ServerMapper1_12_2();
        return instance;
    }

    public abstract WorldServer[] getWorlds();
}