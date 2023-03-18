package ru.imine.version.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.version.client.v1_12_2.GuiMapper1_12_2;

public abstract class GuiMapper
{
    protected static Logger logger = LogManager.getLogger("GuiMapper");
    //protected static Field fieldFontRenderer;

    protected static GuiMapper instance;

    public static GuiMapper instance()
    {
        if (instance==null)
            instance = new GuiMapper1_12_2();
        return instance;
    }

    public FontRenderer getFontRenderer(GuiScreen gui)
    {
        /*try
        {
            return (FontRenderer) fieldFontRenderer.get(gui);
        }
        catch (IllegalAccessException e)
        {
            logger.error(e);
        }*/
        return Minecraft.getMinecraft().fontRenderer;
    }

    public abstract FontRenderer getFontRenderer();

    public abstract int getX(GuiTextField element);
    public abstract int getY(GuiTextField element);
    public abstract void setY(GuiTextField element, int i);
    public abstract int getX(GuiButton element);
    public abstract int getY(GuiButton element);
    public abstract void changeY(Slot slot, int i);
}