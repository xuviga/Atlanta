package ru.imine.shared;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.client.util.DiscordClient;
import ru.imine.server.util.DiscordAPI;
import ru.imine.server.webapi.WebAPI;
import ru.imine.shared.economy.Economy;
import ru.imine.shared.util.Discord;

import java.util.Map;

@Mod(modid = "imine", version = "0.92", name = "iMine")
public class AiMine implements IFMLLoadingPlugin
{
    public static final Logger LOGGER = LogManager.getLogger("iMine");

    public static AiMine instance;

    @Mod.EventHandler
    public void onEventShared(FMLPreInitializationEvent e)
    {
        instance = this;
        ru.imine.shared.core.AiMineCore.preInit(e);
        ru.imine.shared.fancychat.FancyChat.preInit(e);
        Economy.preInit(e);
        NetworkRegistry.INSTANCE.registerGuiHandler(AiMine.instance, new GuiMetaHandler());
        if (e.getSide()==Side.SERVER)
            onEventServer(e);
        else
            onEventClient(e);
    }

    @SideOnly(Side.SERVER)
    public void onEventServer(FMLPreInitializationEvent e)
    {
        if (e.getSide()==Side.SERVER)
        {
            Discord.instance = new DiscordAPI();
            ru.imine.server.core.AiMineCore.preInit(e);
            ru.imine.server.fancychat.FancyChat.preInit(e);
            ru.imine.server.economy.Ecomony.preInit(e);
            WebAPI.preInit(e);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onEventClient(FMLPreInitializationEvent e)
    {
        if (e.getSide()==Side.CLIENT)
        {
            Discord.instance = new DiscordClient();
            ru.imine.client.fancychat.FancyChat.preInit(e);
            ru.imine.client.economy.Economy.preInit(e);
        }
    }

    @Mod.EventHandler
    public void onEvent(FMLInitializationEvent e)
    {
        Economy.init(e);
        if (e.getSide()==Side.CLIENT)
            onEventClient(e);
    }

    @SideOnly(Side.CLIENT)
    public void onEventClient(FMLInitializationEvent e)
    {
        if (e.getSide()==Side.CLIENT)
        {
            ru.imine.client.fancychat.FancyChat.init(e);
            ru.imine.client.economy.Economy.init(e);
        }
    }


    @Mod.EventHandler
    public void onEvent(FMLPostInitializationEvent e)
    {
        ru.imine.shared.core.AiMineCore.postInit(e);
        if (e.getSide()==Side.CLIENT)
            onEventClient(e);
    }

    public void onEventClient(FMLPostInitializationEvent e)
    {
        if (e.getSide()==Side.CLIENT)
        {
            ru.imine.client.fancychat.FancyChat.postInit(e);
        }
    }
    @Mod.EventHandler
    @SideOnly(Side.SERVER)
    public void onEvent(FMLServerStartingEvent e)
    {
        ru.imine.server.core.AiMineCore.starting(e);
        ru.imine.server.fancychat.FancyChat.init(e);
        WebAPI.starting(e);
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
        //return new String[]{ChunkTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}