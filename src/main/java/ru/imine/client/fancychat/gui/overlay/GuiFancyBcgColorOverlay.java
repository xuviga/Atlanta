package ru.imine.client.fancychat.gui.overlay;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import ru.imine.client.fancychat.gui.GuiFancyChatInput;
import ru.imine.client.fancychat.gui.GuiFancyTextField;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;

import java.util.Collections;
import java.util.List;

public class GuiFancyBcgColorOverlay
{
    public static final int ELEMENT_WIDTH=40;
    public static final int ELEMENT_HEIGHT=10;

    private final GuiFancyChatInput gui;
    private final List<GuiFancyTextField> textFields;

    public int xPosition;
    public int yPosition;

    public boolean isOverlayEnabled;

    public GuiFancyBcgColorOverlay(GuiFancyChatInput gui, GuiFancyTextField textField, int xPosition, int yPosition)
    {
        this(gui, Collections.singletonList(textField), xPosition, yPosition);
    }

    public GuiFancyBcgColorOverlay(GuiFancyChatInput gui, List<GuiFancyTextField> textFields, int xPosition, int yPosition)
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
        ChatBcgColor[] colors = ChatBcgColor.values();
        Gui.drawRect(xPosition, yPosition,
                xPosition - ELEMENT_WIDTH * 2, yPosition - ELEMENT_HEIGHT * colors.length / 2,
                0xFF000000);

        int index = 0;
        for (ChatBcgColor chatBcgColor : colors)
        {
            if (chatBcgColor != ChatBcgColor.SERVER && chatBcgColor != ChatBcgColor.RED)
            {
                int i = index % 2;
                int j = index / 2;
                Gui.drawRect(xPosition - ELEMENT_WIDTH * i, yPosition - ELEMENT_HEIGHT * j,
                        xPosition - ELEMENT_WIDTH * (i + 1) + 1, yPosition - ELEMENT_HEIGHT * (j + 1) + 1,
                        0x7F000000 | chatBcgColor.value);
                index++;
            }
        }
    }

    public boolean mouseClicked(int x, int y, int button)
    {
        ChatBcgColor[] colors = ChatBcgColor.values();
        if (!isOverlayEnabled)
            return false;
        if (!(x <= xPosition && x >= xPosition - ELEMENT_WIDTH * 2 && y <= yPosition && y >= yPosition - ELEMENT_HEIGHT * colors.length / 2))
        {
            isOverlayEnabled=false;
            return false;
        }

        int i = (xPosition - x) / ELEMENT_WIDTH;
        int j = (yPosition - y) / ELEMENT_HEIGHT;

        int index = Math.min(colors.length-1,Math.max(0,i + j * 2));

        for (GuiFancyTextField textField : textFields)
            textField.fancyLine.bcgColor=colors[index+2];

        return true;
    }
}