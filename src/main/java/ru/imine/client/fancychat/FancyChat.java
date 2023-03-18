package ru.imine.client.fancychat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.Locale;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.client.fancychat.gui.GuiFancyChat;
import ru.imine.client.fancychat.gui.GuiFancyChatInput;
import ru.imine.client.fancychat.gui.GuiFancySleepMP;
import ru.imine.shared.fancychat.item.ItemSmileCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FancyChat
{
    private static GuiFancyChat persistentChatGUI;
    private static Locale english;

    public static void preInit(FMLPreInitializationEvent e)
    {
        FancyChat instance = new FancyChat();
        MinecraftForge.EVENT_BUS.register(instance);
        persistentChatGUI = new GuiFancyChat(Minecraft.getMinecraft());
        english = new Locale();
        List<String> list = new ArrayList<>();
        list.add("en_US");
        english.loadLocaleDataFiles(Minecraft.getMinecraft().getResourceManager(), list);
        ru.imine.shared.fancychat.FancyChat.LOGGER.info("",e);
    }

    public static void postInit(FMLPostInitializationEvent e)
    {
        english = new Locale();
        List<String> list = new ArrayList<>();
        list.add("en_US");
        english.loadLocaleDataFiles(Minecraft.getMinecraft().getResourceManager(), list);
    }

    public static void init(FMLInitializationEvent event)
    {
        for (ItemSmileCase item : ItemSmileCase.getItems())
        {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                    .register(item, 0, new ModelResourceLocation(item.getRegistryName(),"inventory"));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(ItemTooltipEvent event)
    {
        if (Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("en_"))
            return;
        String engName = english.formatMessage(event.getItemStack().getTranslationKey() + ".name", new String[0]);
        engName = "§7" + engName.replace("§.", "");
        event.getToolTip().add(1, engName);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEvent(GuiOpenEvent event)
    {
        if (event.getGui() != null)
        {
            if (event.getGui().getClass() == GuiChat.class)
            {
                event.setGui(new GuiFancyChatInput(((GuiChat)event.getGui()).defaultInputFieldText));
            }
            else if (event.getGui().getClass() == GuiSleepMP.class)
            {
                event.setGui(new GuiFancySleepMP());
            }
        }
    }

    @SubscribeEvent
    public void onEvent(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        try
        {
            Field f = GuiIngame.class.getDeclaredFields()[6];
            Field fc = Field.class.getDeclaredField("modifiers");
            fc.setAccessible(true);
            f.setAccessible(true);
            fc.set(f,f.getModifiers() &~ Modifier.FINAL);
            f.set(Minecraft.getMinecraft().ingameGUI, persistentChatGUI);
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            ru.imine.shared.fancychat.FancyChat.LOGGER.info("",e);
        }
    }


}
