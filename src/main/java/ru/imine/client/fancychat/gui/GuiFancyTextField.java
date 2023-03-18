package ru.imine.client.fancychat.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextComponentString;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.FancyChatComponentElement;
import ru.imine.shared.fancychat.chat.FancyChatElement;
import ru.imine.shared.fancychat.chat.FancyLine;
import ru.imine.version.client.GuiMapper;

public class GuiFancyTextField extends GuiTextField
{
    private static final int COLOR_ENABLE = 0xFFE0E0E0;

    public final int symLimit;
    public final int rowLimit;
    public FancyLine fancyLine;
    public final FontRenderer fontRenderer;

    protected int cursorPosition=0;
    public int selectionEndPos=0;
    protected boolean canLoseFocus=true;

    public GuiFancyTextField(int componentId, FontRenderer fontRenderer, int x, int y, int w, int h, int symLimit, int rowLimit)
    {
        super(componentId, fontRenderer, x, y, w, h);
        this.fontRenderer = fontRenderer;
        this.fancyLine = new FancyLine();
        this.symLimit = symLimit;
        this.rowLimit = rowLimit;
    }

    @Override
    public void setCanLoseFocus(boolean canLoseFocus)
    {
        this.canLoseFocus = canLoseFocus;
    }

    @Override
    public void drawTextBox()
    {
        int xPosition = GuiMapper.instance().getX(this);
        int yPosition = GuiMapper.instance().getY(this);

        if (this.getVisible())
        {
            if (this.getEnableBackgroundDrawing())
            {
                drawRect(xPosition - 1, yPosition - 1, xPosition + this.width + 1, yPosition + this.height + 1, -6250336);
                drawRect(xPosition, yPosition, xPosition + this.width, yPosition + this.height, -16777216);
            }

            if (selectionEndPos != cursorPosition)
            {
                processSelection();
            }
            else
            {
                int cursorPos = getCursorPosition();
                int pos = 0, x = xPosition, y = yPosition;
                for (FancyChatElement iterated : fancyLine)
                {
                    int len = iterated.getSymbolCount();
                    if (pos + len >= cursorPos)
                    {
                        x += iterated.getSymbolPosition(fontRenderer, cursorPos - pos);
                        break;
                    }
                    pos += len;
                    int[] coord = iterated.getSize(fontRenderer);
                    x += coord[0];
                }
                drawRect(x, y, x+1, y + fontRenderer.FONT_HEIGHT, 0xAAFFFFFF);
            }

            fancyLine.draw(fontRenderer, xPosition, yPosition, height, width, 255);
        }
    }

    private void processSelection()
    {
        int[] range = getSelectionRange();

        int pos = 0;
        int x=0;
        int startX=-1, endX=-1;
        for (FancyChatElement iterated : fancyLine)
        {
            int len = iterated.getSymbolCount();
            if (startX==-1 && pos + len > range[0])
            {
                startX = x + iterated.getSymbolPosition(fontRenderer, range[0] - pos);
            }
            if (pos + len > range[1])
            {
                endX = x + iterated.getSymbolPosition(fontRenderer, range[1] - pos + 1);
                break;
            }
            pos += len;
            int[] coord = iterated.getSize(fontRenderer);
            x += coord[0];
        }
        int xPosition = GuiMapper.instance().getX(this);
        int yPosition = GuiMapper.instance().getY(this);
        drawRect(startX+xPosition, yPosition, endX+xPosition, yPosition + fontRenderer.FONT_HEIGHT, 0xAAFFFFFF);
    }

    @Override
    public void setText(String str)
    {
        fancyLine.clear();
        FancyChatComponentElement element = new FancyChatComponentElement(str);
        element.setColor(AiMineUser.getLocalPlayer().chatColor);
        fancyLine.add(element);
    }

    @Override
    public void writeText(String str)
    {
        int[] range = getSelectionRange();
        if (range[0]!=range[1])
        {
            fancyLine.removeSymbolsInRange(range[0], range[1]);
            setCursorPosition(range[0]);
        }
        int newLen = 0;
        for (FancyChatElement element : fancyLine)
            newLen += element.getSize(fontRenderer)[0];
        FancyChatElement newElement = new FancyChatComponentElement(new TextComponentString(str));
        newElement.setColor(AiMineUser.getLocalPlayer().chatColor);
        newLen += newElement.getSize(fontRenderer)[0];
        if (newLen <= width)
            moveCursorBy(fancyLine.insertElement(getCursorPosition(), newElement, symLimit));
    }

    @Override
    public int getMaxStringLength()
    {
        return symLimit;
    }

    @Override
    @Deprecated
    public String getText()
    {
        return fancyLine.toPlainText();
    }

    @Override
    @Deprecated
    public String getSelectedText()
    {
        int[] range = getSelectionRange();
        return fancyLine.toPlainText().substring(range[0],range[1]);
    }

    @Override
    public int getCursorPosition()
    {
        return cursorPosition;
    }

    @Override
    public void setCursorPosition(int pos)
    {
        int end = fancyLine.getSymbolCount();
        if (pos < 0)
            pos = 0;
        else if (pos > end)
            pos = end;

        selectionEndPos = pos;
        cursorPosition = pos;
    }

    @Override
    @Deprecated
    public int getSelectionEnd()
    {
        return selectionEndPos;
    }

    public int[] getSelectionRange()
    {
        if (getCursorPosition()<getSelectionEnd())
            return new int[]{getCursorPosition(),getSelectionEnd()-1};
        else if (getCursorPosition()==getSelectionEnd())
            return new int[]{getCursorPosition()-1,getCursorPosition()-1};
        else
            return new int[]{getSelectionEnd(),getCursorPosition()-1};
    }


    @Override
    public void setSelectionPos(int pos)
    {
        int end = fancyLine.getSymbolCount();
        if (pos < 0)
            pos = 0;
        else if (pos > end)
            pos = end;

        selectionEndPos = pos;
    }

    @Override
    public void moveCursorBy(int p_146182_1_)
    {
        this.setCursorPosition(this.getCursorPosition() + p_146182_1_);
    }

    @Override
    public void setCursorPositionEnd()
    {
        setCursorPosition(fancyLine.getSymbolCount());
    }

    @Override
    public int getNthWordFromPosWS(int n, int pos, boolean skipWs)
    {
        FancyLine.ElementPosData data = fancyLine.findLastElementBySubString(" ", n, pos);
        return data==null ? 0 : data.offsetInLine+1;
    }

    @Override
    public void deleteFromCursor(int num)
    {
        fancyLine.removeSymbolsInRange(this.getCursorPosition(),this.getCursorPosition()+num);
    }

    @Override
    public boolean textboxKeyTyped(char key, int code)
    {
        int[] range = getSelectionRange();
        if (code==14)
        {
            if (GuiScreen.isCtrlKeyDown())
            {
                FancyLine.ElementPosData elementData = fancyLine.findLastElementBySubString(" ", 0, range[0]);
                if (elementData == null)
                {
                    fancyLine.removeSymbolsInRange(0, range[0]);
                    setCursorPosition(0);
                }
                else
                {
                    fancyLine.removeSymbolsInRange(elementData.offsetInLine,range[0]);
                    setCursorPosition(elementData.offsetInLine);
                }
            }
            else
            {
                int symbols = fancyLine.removeSymbolsInRange(range[0], range[1]);
                setCursorPosition(range[0]);
            }
            return true;
        }
        else if (code==211)
        {
            if (GuiScreen.isCtrlKeyDown())
            {
                int len = fancyLine.getSymbolCount();
                FancyLine.ElementPosData elementData = fancyLine.findLastElementBySubString(" ", range[0], len);
                if (elementData == null)
                    fancyLine.removeSymbolsInRange(range[0], len);
                else
                    fancyLine.removeSymbolsInRange(range[0],elementData.offsetInLine);
            }
            else if (range[0]==range[1])
                fancyLine.removeSymbolsInRange(range[0]+1, range[0]+1);
            else
            {
                int symbols = fancyLine.removeSymbolsInRange(range[0], range[1]);
                setCursorPosition(range[0]);
            }
            return true;
        }
        return super.textboxKeyTyped(key,code);
    }

    @Override
    public boolean mouseClicked(int x, int y, int butt)
    {
        int xPosition = GuiMapper.instance().getX(this);
        int yPosition = GuiMapper.instance().getY(this);

        boolean notMissed = x >= xPosition && x < xPosition + this.width && y >= yPosition && y < yPosition + this.height;

        if (canLoseFocus)
            this.setFocused(notMissed);

        if (isFocused() && butt==0)
        {
            int l = x - xPosition;

            if (getEnableBackgroundDrawing())
                l -= 4;

            String plainText = fancyLine.toPlainText();
            this.setCursorPosition(fontRenderer.trimStringToWidth(plainText, l).length()+1);
        }
        return true;
    }
    /**
     * Требуется вызывать этот метод из вашего GuiScreen, чтобы этот элемент поддерживал выделение текста мышкой
     */
    public void mouseClickMove(int x, int y, int butt, long pressureTime)
    {
        try
        {
            if (butt == 0)
            {
                int l = x - GuiMapper.instance().getX(this);

                if (getEnableBackgroundDrawing())
                    l -= 4;

                String plainText = fancyLine.toPlainText();
                setSelectionPos(fontRenderer.trimStringToWidth(plainText, l).length());
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to detect press-click", e);
        }
    }
}