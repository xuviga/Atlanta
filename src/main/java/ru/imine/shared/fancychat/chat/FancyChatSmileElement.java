package ru.imine.shared.fancychat.chat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.client.fancychat.smile.SmileDictionary;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.smile.Smile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FancyChatSmileElement extends FancyChatElement
{
    private static final long serialVersionUID = 322L;
    protected transient Smile smile;
    protected String category;
    protected String name;

    public FancyChatSmileElement(String category, String name)
    {
        this.category = category;
        this.name = name;
    }

    public Smile getSmile()
    {
        if (smile==null)
        {
            if (FMLCommonHandler.instance().getSide() == Side.SERVER)
                smile = SmileManager.getSmile(category, name);
            else
                smile = SmileDictionary.getSmile(category, name);
        }
        return smile;
    }

    @Override
    public List<FancyChatElement> mergeWithElement(int pos, FancyChatElement newElement)
    {
        List<FancyChatElement> result = new ArrayList<>();
        if (pos <= Math.ceil(getSymbolCount()/2))
        {
            result.add(newElement);
            result.add(this);
        }
        else
        {
            result.add(this);
            result.add(newElement);
        }
        return result;
    }

    @Override
    public FancyLine getHover()
    {
        FancyLine hover = super.getHover();
        if (hover!=null)
            return hover;
        return FancyChat.stringToChatLine(toPlainText());
    }

    @Override
    public List<FancyChatElement> splitElement(int pos)
    {
        return Collections.singletonList(this);
    }

    @Override
    public boolean removeSymbols(int from, int to)
    {
        return (to>=0 && from<=getSymbolCount());
    }

    @Override
    public String toPlainText()
    {
        return ":"+category+"/"+name+":";
    }

    @Override
    public int getRowSize()
    {
        return getSmile().rowSize;
    }

    @Override
    public int getSymbolCount()
    {
        return 1;
    }

    @Override
    public String toString()
    {
        return "FancyChatSmileElement={:"+category+"/"+name+":}";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(FontRenderer fontRenderer, int x, int y, int alpha)
    {
        int rAlpha = (color >> 24 + alpha) / 2;
        int rColor = color | 0x00FFFFFF;
        getSmile().draw(x + 1, y, rAlpha << 24 | rColor);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int[] getSize(FontRenderer fontRenderer)
    {
        return new int[]{getSmile().getWidth() + 2, getSmile().getHeight()};
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSymbolPosition(FontRenderer fontRenderer, int index)
    {
        return getSmile().getWidth();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean mouseClicked(GuiScreen screen)
    {
        return false;
    }
}