package ru.imine.shared.fancychat.chat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FancyChatComponentElement extends FancyChatElement
{
    private static final long serialVersionUID = 322L;
    public ITextComponent chatComponent;

    public FancyChatComponentElement(ITextComponent chatComponent)
    {
        this.chatComponent = chatComponent;
    }

    public FancyChatComponentElement(ITextComponent chatComponent, int color)
    {
        this.chatComponent = chatComponent;
        this.color = color;
    }

    public FancyChatComponentElement(String message)
    {
        this.chatComponent = new TextComponentString(message);
    }

    public FancyChatComponentElement(String message, int color)
    {
        this.chatComponent = new TextComponentString(message);
        this.color = color;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        oos.writeUTF(ITextComponent.Serializer.componentToJson(chatComponent));
        oos.writeInt(color);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        chatComponent = ITextComponent.Serializer.fromJsonLenient(ois.readUTF());
        color = ois.readInt();
    }

    @Override
    public List<FancyChatElement> mergeWithElement(int pos, FancyChatElement newElement)
    {
        List<FancyChatElement> result = new ArrayList<>();

        Style style = chatComponent.getStyle();
        if (newElement instanceof FancyChatComponentElement)
        {
            FancyChatComponentElement newComponentElement = (FancyChatComponentElement) newElement;
            if (style.equals(newComponentElement.chatComponent.getStyle()) && color == newComponentElement.color)
            {
                String rawText = chatComponent.getUnformattedText();
                chatComponent = new TextComponentString(rawText.substring(0, pos)
                        + newComponentElement.chatComponent.getUnformattedText()
                        + rawText.substring(pos));
                chatComponent.setStyle(style);
                result.add(this);
                return result;
            }
        }
        String rawText = chatComponent.getUnformattedText();
        chatComponent = new TextComponentString(rawText.substring(0, pos));
        chatComponent.setStyle(style);
        ITextComponent thirdChatComponent = new TextComponentString(rawText.substring(pos));
        thirdChatComponent.setStyle(style);
        FancyChatComponentElement thirdComponent = new FancyChatComponentElement(thirdChatComponent);
        thirdComponent.setColor(getColor());

        result.add(this);
        result.add(newElement);
        result.add(thirdComponent);
        return result;
    }

    @Override
    public List<FancyChatElement> splitElement(int pos)
    {
        if (pos<=0 || pos>=getSymbolCount())
            return Collections.singletonList(this);
        List<FancyChatElement> result = new ArrayList<>();
        Style style = chatComponent.getStyle();
        String rawText = chatComponent.getUnformattedText();
        chatComponent = new TextComponentString(rawText.substring(0, pos));
        chatComponent.setStyle(style);
        ITextComponent thirdChatComponent = new TextComponentString(rawText.substring(pos));
        thirdChatComponent.setStyle(style);
        FancyChatComponentElement thirdComponent = new FancyChatComponentElement(thirdChatComponent);
        thirdComponent.setColor(getColor());

        result.add(this);
        result.add(thirdComponent);
        return result;
    }

    @Override
    public boolean removeSymbols(int from, int to)
    {
        if (from > to)
        {
            int tmp = from;
            from = to;
            to = tmp;
        }
        int length = getSymbolCount();
        if ((to > length && from < 0) || from > length || to<0)
            return false;
        if (to > length)
            to = length;
        else if (from < 0)
            from = 0;

        Style style = chatComponent.getStyle();
        String rawText = chatComponent.getUnformattedText();
        String newText = "";
        if (from != 0)
            newText += rawText.substring(0, from);
        if (to != length)
            newText += rawText.substring(to + 1);
        chatComponent = new TextComponentString(newText);
        chatComponent.setStyle(style);
        return newText.isEmpty();
    }

    @Override
    @Deprecated
    public String toPlainText()
    {
        return chatComponent.getUnformattedText();
    }

    @Override
    public int getRowSize()
    {
        return 1;
    }

    @Override
    public int getSymbolCount()
    {
        return chatComponent.getUnformattedText().length();
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    public void setColor(int color)
    {
        this.color = 0xFF000000 | (color & 0xFFFFFF);
    }

    @Override
    public String toString()
    {
        return "FancyChatComponentElement={"+chatComponent+" #0x"+Integer.toHexString(color)+"}";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(FontRenderer fontRenderer, int x, int y, int alpha)
    {
        int rAlpha = (color >> 24 + alpha) / 2;
        int rColor = color | 0x00FFFFFF;
        fontRenderer.drawStringWithShadow(chatComponent.getFormattedText(), x, y, rAlpha << 24 | rColor);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int[] getSize(FontRenderer fontRenderer)
    {
        return new int[]{fontRenderer.getStringWidth(chatComponent.getFormattedText()), fontRenderer.FONT_HEIGHT};
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getSymbolPosition(FontRenderer fontRenderer, int index)
    {
        String rawText = chatComponent.getUnformattedText();
        return fontRenderer.getStringWidth(rawText.substring(0, Math.min(index,rawText.length())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean mouseClicked(GuiScreen screen)
    {
        return screen.handleComponentClick(chatComponent);
    }
}
