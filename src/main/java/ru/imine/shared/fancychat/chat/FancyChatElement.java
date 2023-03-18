package ru.imine.shared.fancychat.chat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.Serializable;
import java.util.List;

public abstract class FancyChatElement implements Serializable, Cloneable
{
    private static final long serialVersionUID = 322L;
    @Override
    public FancyChatElement clone()
    {
        try
        {
            return (FancyChatElement)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError(e);
        }
    }

    protected FancyLine hover;
    protected int color = 0xFFFFFFFF;

    public FancyLine getHover()
    {
        return hover;
    }

    public void setHover(FancyLine hover)
    {
        this.hover = hover;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
        if ((this.color & 0xFF000000) == 0)
            this.color |= 0xFF000000;
    }

    public abstract int getRowSize();
    public abstract int getSymbolCount();
    public abstract List<FancyChatElement> mergeWithElement(int pos, FancyChatElement newElement);
    public abstract List<FancyChatElement> splitElement(int pos);
    public abstract boolean removeSymbols(int from, int to);

    @Deprecated
    public abstract String toPlainText();

    @SideOnly(Side.CLIENT)
    public void draw(FontRenderer fontRenderer, int x, int y)
    {
        draw(fontRenderer, x, y, 255);
    }

    @SideOnly(Side.CLIENT)
    public abstract void draw(FontRenderer fontRenderer, int x, int y, int alpha);
    @SideOnly(Side.CLIENT)
    public abstract int[] getSize(FontRenderer fontRenderer);
    @SideOnly(Side.CLIENT)
    public abstract int getSymbolPosition(FontRenderer fontRenderer, int index);
    @SideOnly(Side.CLIENT)
    public abstract boolean mouseClicked(GuiScreen screen);
}
