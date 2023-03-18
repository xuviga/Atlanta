package ru.imine.client.fancychat.gui.overlay;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;
import ru.imine.client.fancychat.gui.GuiFancyChatInput;
import ru.imine.client.fancychat.gui.GuiFancyTextField;
import ru.imine.shared.fancychat.chat.FancyChatElement;
import ru.imine.shared.fancychat.chat.FancyLine;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class GuiFancyColorOverlay
{
    public static final int WIDTH=85;
    public static final int HEIGHT=70;
    private static final DecimalFormat format = new DecimalFormat("0.00");

    private final GuiFancyChatInput gui;
    private final List<GuiFancyTextField> textFields;

    public int xPosition;
    public int yPosition;

    private float lasthue=1f;
    private float lastSaturation=1f;
    private float lastBrightness=1f;

    private float hue=1f;
    private float saturation=1f;
    private float brightness=1f;

    public boolean isOverlayEnabled;
    private int lockedBar=-1;

    private final int[] pallete = new int[14];

    public GuiFancyColorOverlay(GuiFancyChatInput gui, GuiFancyTextField textField, int xPosition, int yPosition)
    {
        this(gui, Collections.singletonList(textField), xPosition, yPosition);
    }

    public GuiFancyColorOverlay(GuiFancyChatInput gui, List<GuiFancyTextField> textFields, int xPosition, int yPosition)
    {
        this.gui=gui;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.textFields = textFields;
    }

    public void drawOverlay(FontRenderer fontRenderer, int mouseX, int mouseY, float f)
    {
        if (!isOverlayEnabled)
            return;
        Gui.drawRect(xPosition, yPosition, xPosition + WIDTH, yPosition + HEIGHT, 0xFFFFFFFF);
        int x = this.xPosition + 13;
        int y = this.yPosition + 3;
        int len = WIDTH - 16;
        GL11.glPushMatrix();
        GL11.glScaled(0.6,0.6,1);
        fontRenderer.drawString("H", (int)((xPosition + 5)/0.6), (int)((yPosition + 6)/0.6), 0xFF555555);
        fontRenderer.drawString("S", (int)((xPosition + 5)/0.6), (int)((yPosition + 16)/0.6), 0xFF555555);
        fontRenderer.drawString("V", (int)((xPosition + 5)/0.6), (int)((yPosition + 26)/0.6), 0xFF555555);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScaled(0.4,0.4,1);
        fontRenderer.drawString(format.format(hue), (int)((xPosition + 4)/0.4), (int)((yPosition + 11)/0.4), 0xFF555555);
        fontRenderer.drawString(format.format(saturation), (int)((xPosition + 4)/0.4), (int)((yPosition + 21)/0.4), 0xFF555555);
        fontRenderer.drawString(format.format(brightness), (int)((xPosition + 4)/0.4), (int)((yPosition + 31)/0.4), 0xFF555555);
        GL11.glPopMatrix();
        Gui.drawRect((int) (x + hue * len), y - 2, (int) (x + hue * len + 1), y + 13, 0xFF555555);
        for (int i = 0; i < len; i++)
            Gui.drawRect(x + i, y, x + i + 1, y + 11, 0xFF000000 | Color.getHSBColor((float) i / len, saturation, brightness).hashCode());

        Gui.drawRect((int) (x + saturation * len), y + 13, (int) (x + saturation * len + 1), y + 18, 0xFF555555);
        Gui.drawRect(x, y + 15, x + len, y + 16, 0xFF555555);

        Gui.drawRect((int) (x + brightness * len), y + 23, (int) (x + brightness * len + 1), y + 28, 0xFF555555);
        Gui.drawRect(x, y + 25, x + len, y + 26, 0xFF555555);

        x -= 10;
        len += 10;

        for (int i = 0; i < 8; i++) //todo сохранять палитру
        {
            Gui.drawRect(x + i * 10, y + 32, x + i * 10 + 9, y + 40, 0x22000000);
            Gui.drawRect(x + i * 10, y + 42, x + i * 10 + 9, y + 50, 0x22000000);
        }
        Gui.drawRect(x, y + 32, x + 19, y + 50, getColor());

        x += 5;
        Gui.drawRect(x, y + 52, x + 35, y + 65, 0xAAAAAAAA);
        Gui.drawRect(x + 1, y + 53, x + 34, y + 64, 0x77777777);

        Gui.drawRect(x + 37, y + 52, x + 70, y + 65, 0xAAAAAAAA);
        Gui.drawRect(x + 38, y + 53, x + 69, y + 64, 0x77777777);
        fontRenderer.drawString("OK", x + 12, y + 55, 0xFFFFFFFF);
        fontRenderer.drawString("Отмена", x + 40, y + 55, 0xFFFFFFFF);
    }

    public boolean mouseClicked(int x, int y, int button)
    {
        if (!isOverlayEnabled || !(x >= xPosition && x <= xPosition + WIDTH && y >= yPosition && y <= yPosition + HEIGHT))
            return false;

        if (x >= xPosition + 8 && x <= xPosition + 42 && y >= yPosition + 55 && y <= yPosition + 68) //OK
        {
            for (GuiFancyTextField textField : textFields)
            {
                if (!textField.isFocused())
                    continue;;
                FancyLine line = textField.fancyLine;
                int[] range =textField.getSelectionRange();
                line.splitElement(range[0]);
                line.splitElement(range[1] + 1);
                FancyLine.ElementRangeData elementData = line.findElementsBySymbolPosRange(range[0] + 1, range[1] + 1);
                for (FancyChatElement element : elementData.elements)
                {
                    element.setColor(getColor());
                }
            }
            return true;
        }
        else if (x >= xPosition + 45 && x <= xPosition + 77 && y <= yPosition + 55 && y >= yPosition + 68) //Cancel
        {
            hue = lasthue;
            saturation = lastSaturation;
            brightness = lastBrightness;
            return true;
        }
        int barStart = xPosition + 13;
        int barLen = WIDTH - 16;

        if (x >= barStart && x <= barStart + barLen)
        {
            if (y >= yPosition + 3 && y <= yPosition + 14)
                lockedBar = 0;
            else if (y >= yPosition + 15 && y <= yPosition + 21)
                lockedBar = 1;
            else if (y >= yPosition + 24 && y <= yPosition + 30)
                lockedBar = 2;
        }
        return mouseClickMove(x, y, button, 0);
    }

    public boolean mouseClickMove(int x, int y, int button, long pressureTime)
    {
        if (!isOverlayEnabled || lockedBar==-1)
            return false;
        int barStart = xPosition+13;
        int barLen = WIDTH-16;
        if (lockedBar==0)
            hue = Math.max(0,Math.min(1,(x - barStart) / (float) barLen));
        else if (lockedBar==1)
            saturation = Math.max(0,Math.min(1,(x - barStart) / (float) barLen));
        else if (lockedBar==2)
            brightness = Math.max(0,Math.min(1,(x - barStart) / (float) barLen));
        return true;
    }

    public void mouseReleased(int x, int y, int which)
    {
        lockedBar=-1;
    }

    public int getColor()
    {
        return Color.getHSBColor(hue, saturation, brightness).hashCode();
    }

}