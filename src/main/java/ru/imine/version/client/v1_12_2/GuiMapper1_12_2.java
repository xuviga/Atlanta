package ru.imine.version.client.v1_12_2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Slot;
import ru.imine.version.client.GuiMapper;

public class GuiMapper1_12_2 extends GuiMapper
{
    /*static
    {
        try
        {
            fieldFontRenderer = GuiScreen.class.getDeclaredField("fontRenderer");
            fieldFontRenderer.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            logger.error(e);
        }
    }*/

    @Override
    public FontRenderer getFontRenderer()
    {
        return Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public int getX(GuiTextField element)
    {
        return element.x;
    }

    @Override
    public int getY(GuiTextField element)
    {
        return element.y;
    }

    @Override
    public void setY(GuiTextField element, int value)
    {
        element.y = value;
    }

    @Override
    public int getX(GuiButton element)
    {
        return element.x;
    }

    @Override
    public int getY(GuiButton element)
    {
        return element.y;
    }

    @Override
    public void changeY(Slot slot, int value)
    {
        slot.yPos = value;
    }
}