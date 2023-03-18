package ru.imine.server.webapi;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.server.core.AiMineCore;
import ru.imine.version.server.v1_12_2.command.DonateCommand;

public class WebAPI
{
    public static final Logger LOGGER = LogManager.getLogger("WebAPI");

    private static Configuration config;
    private static HttpListener httpListener;
    private static MySQLListener sqlListener;

    public static HttpListener getHttpListener()
    {
        return httpListener;
    }
    public static MySQLListener getSQLListener()
    {
        return sqlListener;
    }

    public static void preInit(FMLPreInitializationEvent e)
    {
        httpListener = new HttpListener(AiMineCore.getConfig().getJSONObject("settings").getInteger("web_api_port"));
        sqlListener = new MySQLListener();
    }

    public static void starting(FMLServerStartingEvent e)
    {
        e.registerServerCommand(new DonateCommand());
    }
}
