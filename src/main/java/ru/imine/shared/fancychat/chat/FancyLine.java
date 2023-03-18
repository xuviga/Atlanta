package ru.imine.shared.fancychat.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.fancychat.chat.util.ChatBcgColor;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.ObjectWrapper;

import java.util.ArrayList;
import java.util.List;

public class FancyLine extends ArrayList<FancyChatElement>
{
    private static final long serialVersionUID = 322L;

    public ChatBcgColor bcgColor = ChatBcgColor.DEFAULT;

    public FancyLine()
    {
    }

    public FancyLine(ChatBcgColor bcgColor)
    {
        this.bcgColor = bcgColor;
    }

    public FancyLine with(FancyChatElement element)
    {
        add(element);
        return this;
    }

    public FancyLine with(String text)
    {
        return with(new TextComponentString(text));
    }

    public FancyLine with(ITextComponent component)
    {
        add(new FancyChatComponentElement(component));
        return this;
    }

    public int getSymbolCount()
    {
        return stream().mapToInt(FancyChatElement::getSymbolCount).sum();
    }

    /**
     *
     * @param pos
     * @return Кол-во добавленных символьных позиций (неделимый элемент в теории может занимать несколько позиций)
     */
    public int insertElement(int pos, FancyChatElement newElement, int symLimit)
    {
        try
        {
            ElementPosData elementData = findElementBySymbolPos(pos);
            if (elementData==null)
            {
                add(newElement);
                return newElement.getSymbolCount();
            }
            int elementLength = elementData.element.getSymbolCount();

            if (symLimit>0 && elementLength+newElement.getSymbolCount()>symLimit)
                return 0;

            List<FancyChatElement> reprocess = elementData.element.mergeWithElement(elementData.offsetInElement,newElement);
            remove(elementData.elementIndex);
            for (int i = reprocess.size() - 1; i >= 0; i--)
                add(elementData.elementIndex,reprocess.get(i));
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to insert chat element: " + newElement + " at " + pos, e);
            Discord.instance.sendErrorLog("Fancy Chat","Failed to insert chat element: " + newElement + " at " + pos, e);
        }
        return newElement.getSymbolCount();
    }

    public void splitElement(int pos)
    {
        int elementPos = 0;
        int elementIndex = 0;
        for (; ; )
        {
            int len = get(elementIndex).getSymbolCount();
            if (elementPos + get(elementIndex).getSymbolCount() >= pos && pos - elementPos > 0)
            {
                List<FancyChatElement> reprocess = get(elementIndex).splitElement(pos - elementPos);
                remove(elementIndex);
                for (int i = reprocess.size() - 1; i >= 0; i--)
                    add(elementIndex, reprocess.get(i));
                len = get(elementIndex).getSymbolCount();
            }
            elementPos += len;
            elementIndex++;
            if (elementIndex >= this.size())
                break;
        }
    }

    /**
     *
     * @return Кол-во удаленных символьных позиций (неделимый элемент в теории может занимать несколько позиций)
     */
    public int removeSymbolsInRange(int from, int to)
    {
        boolean reversed = from > to;
        if (reversed)
        {
            int tmp = from;
            from = to;
            to = tmp;
        }
        int fuckFrom = from;
        int fuckTo = to;

        ObjectWrapper<Integer> elementPos = new ObjectWrapper<>(0);
        this.removeIf(element -> {
            int len = element.getSymbolCount();
            boolean toRemove = element.removeSymbols(fuckFrom - elementPos.object, fuckTo - elementPos.object);
            elementPos.object+=len;
            return toRemove;
        });
        return fuckTo-fuckFrom;
    }

    public ElementRangeData findElementsBySymbolPosRange(int from, int to)
    {
        List<FancyChatElement> elements = new ArrayList<>();
        int elementIndex = -1;
        int startElementPosOffset = -1;

        int posCounter = 0;
        int currentElementIndex = 0;
        for (FancyChatElement element : this)
        {
            int posCount = element.getSymbolCount();
            if (posCounter + posCount >= from)
            {
                elements.add(element);
                if (elementIndex == -1)
                {
                    elementIndex = currentElementIndex;
                    startElementPosOffset = from - posCounter;
                }
            }
            if (posCounter + posCount >= to)
                break;
            posCounter += posCount;
            currentElementIndex++;
        }
        return new ElementRangeData(elements, elementIndex, startElementPosOffset, to - posCounter);
    }

    /**
     * Находит элемент, которому пренадлежит энный символ всего {@link FancyLine}'а
     * @param pos Позиция символа, начиная с 0. К слову, смайлики не смотря на свой размер занимают 1 символ.
     * @return {@link ElementPosData}, содержащий строку, элемент и позицию внутри элемента
     */
    public ElementPosData findElementBySymbolPos(int pos)
    {
        int posCounter = 0;
        int elementIndex = 0;
        for (FancyChatElement element : this)
        {
            int posCount = element.getSymbolCount();
            if (posCounter + posCount >= pos)
                return new ElementPosData(element, elementIndex, pos - posCounter, pos);
            posCounter += posCount;
            elementIndex++;
        }
        return null;
    }

    public ElementPosData findLastElementBySubString(String subString, int start, int end)
    {
        int posCounter = 0;
        int elementIndex = 0;
        ElementPosData result = null;
        for (FancyChatElement element : this)
        {
            int len = element.getSymbolCount();
            if (posCounter + len >= start)
            {
                if (element instanceof FancyChatComponentElement)
                {
                    String rawText = ((FancyChatComponentElement) element).chatComponent.getUnformattedText();
                    int pos = rawText.lastIndexOf(subString);
                    if (posCounter + pos <= end)
                    {
                        if (pos != -1)
                            result = new ElementPosData(element, elementIndex, pos, posCounter + pos);
                    }
                    else
                        break;
                }
            }
            posCounter += len;
            elementIndex++;
        }
        return result;
    }

    @Deprecated
    public String toPlainText()
    {
        StringBuilder bldr = new StringBuilder();
        for (FancyChatElement element : this)
        {
            bldr.append(element.toPlainText());
        }
        return bldr.toString();
    }

    @Override
    public Object clone()
    {
        FancyLine clone = new FancyLine();
        for (FancyChatElement element : this)
            clone.add(element.clone());
        return clone;
    }

    /*@SideOnly(Side.CLIENT)
    public ElementPosData findElementByCoords(FontRenderer fontRenderer, int x, int y)
    {
        int[] pos = new int[2];
        int totalSymbols = 0;
        int elementIndex = 0;
        for (FancyChatElement element : this)
        {
            int[] size = element.getSize(fontRenderer);
            if (pos[0] + size[0] >= x)
            {
                int symbolOffset;
                if (element instanceof FancyChatComponentElement)
                {
                    String rawText = ((FancyChatComponentElement) element).chatComponent.getUnformattedText();
                    symbolOffset = fontRenderer.trimStringToWidth(rawText, x - pos[0]).length();
                }
                else
                    symbolOffset = element.getSymbolCount();
                return new ElementPosData(row, element, elementIndex, symbolOffset, totalSymbols + symbolOffset);
            }
            totalSymbols += element.getSymbolCount();
            elementIndex++;
            pos[0] += size[0];
        }
        elementIndex = 0;
        return null;
    }*/

    @SideOnly(Side.CLIENT)
    public int getTotalRowSize(FontRenderer fontRenderer, int rowWidth)
    {
        return Math.max(1,process(fontRenderer,rowWidth).stream().mapToInt(it->it.rowSize).sum());
        //return stream().mapToInt(FancyChatElement::getRowSize).max().orElse(1);
        /*int currentX = xPosition;
        int currentY = yPosition;
        int rowHeight = 0;
        int currentRowCounter = 0;
        int lineRowCounter = 0;
        for (FancyChatElement element : this)
        {
            currentRowCounter = Math.max(currentRowCounter,element.getRowSize());
            int[] size = element.getSize(fontRenderer);
            if (currentX+size[0]>rowWidth)
            {
                if (element instanceof FancyChatComponentElement)
                {
                    final IChatComponent component = ((FancyChatComponentElement) element).chatComponent;
                    String part1 = fontRenderer.trimStringToWidth(component.getUnformattedText(), rowWidth - currentX);
                    String part2 = component.getUnformattedText().substring(part1.length());
                    FancyChatComponentElement newElement = new FancyChatComponentElement(part1);
                    newElement.chatComponent.setChatStyle(component.getChatStyle());
                    newElement.draw(fontRenderer,currentX,currentY);

                    currentX = xPosition;
                    currentY += rowHeight;
                    rowHeight = 0;

                    newElement = new FancyChatComponentElement(part2);
                    newElement.chatComponent.setChatStyle(component.getChatStyle());
                    newElement.draw(fontRenderer,currentX,currentY);

                    size = newElement.getSize(fontRenderer);
                    currentX = size[0];
                    rowHeight = size[1];
                }
                else
                {
                    currentX = xPosition;
                    currentY += rowHeight;
                    rowHeight = 0;
                }
            }
            else
            {
                element.draw(fontRenderer, currentX, currentY);
                currentX += size[0];
                rowHeight = Math.max(rowHeight, size[1]);
            }
        }
        return lineRowCounter+currentRowCounter;*/
    }

    /*@SideOnly(Side.CLIENT)
    public int[] getSize(FontRenderer fontRenderer, int rowWidth)
    {
        int[] maxSize = new int[2];
        int[] rowSize = new int[2];
        for (FancyChatElement element : this)
        {
            int[] elementSize = element.getSize(fontRenderer);
            if (rowSize[0]+elementSize[0]>rowWidth)
            {
                if (element instanceof FancyChatComponentElement)
                {
                    final IChatComponent component = ((FancyChatComponentElement) element).chatComponent;
                    String part1 = fontRenderer.trimStringToWidth(component.getUnformattedText(), rowWidth - rowSize[0]);
                    String part2 = component.getUnformattedText().substring(part1.length());
                    FancyChatComponentElement newElement = new FancyChatComponentElement(part1);
                    newElement.getSize(fontRenderer);

                    int[] newElementSize = element.getSize(fontRenderer);
                    rowSize[0] += newElementSize[0];
                    rowSize[1] = Math.max(rowSize[1], newElementSize[1]);

                    maxSize[0]=Math.max(maxSize[0],rowSize[0]);
                    maxSize[1]=maxSize[1]+rowSize[0];

                    newElement = new FancyChatComponentElement(part2);
                    newElement.chatComponent.setChatStyle(component.getChatStyle());

                    elementSize = newElement.getSize(fontRenderer);
                    rowSize[0] = elementSize[0];
                    rowSize[1] = elementSize[1];
                }
                else
                {
                    maxSize[0]=Math.max(maxSize[0],rowSize[0]);
                    maxSize[1]=maxSize[1]+rowSize[0];
                    rowSize = new int[2];
                }
            }
            else
            {
                rowSize[0] += elementSize[0];
                rowSize[1] = Math.max(rowSize[1], elementSize[1]);
            }
        }
        maxSize[0]=Math.max(maxSize[0],rowSize[0]);
        maxSize[1]=maxSize[1]+rowSize[0];
        return maxSize;
    }*/

    @SideOnly(Side.CLIENT)
    public void draw(FontRenderer fontRenderer, int xPosition, int yPosition, int yStep, int rowWidth, int alpha)
    {
        int i=0;
        for (FancyRow row : process(fontRenderer, rowWidth))
        {
            int currentX = xPosition;
            int currentY = yPosition + i * yStep;
            for (FancyChatElement element : row.elements)
            {
                element.draw(fontRenderer, currentX, currentY, alpha);
                int width = element.getSize(fontRenderer)[0];
                currentX += width;
            }
            i++;
        }
    }

    @SideOnly(Side.CLIENT)
    public FancyChatElement getElement(FontRenderer fontRenderer, int yStep, int rowWidth, int xOffset, int yOffset)
    {
        int i=0;
        for (FancyRow row : process(fontRenderer, rowWidth))
        {
            int currentX = 0;
            int currentY = i * yStep;
            for (FancyChatElement element : row.elements)
            {
                int width = element.getSize(fontRenderer)[0];

                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                int see=yOffset-scaledresolution.getScaledHeight();
                if (xOffset>=currentX && xOffset<=currentX+width && see>=currentY && see<=currentY+yStep)
                {
                    return element;
                    //drawHover(element, fontRenderer, xOffset, see, yStep, rowWidth);
                }
                currentX += width;
            }
            i++;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public List<FancyRow> process(FontRenderer fontRenderer, int rowWidth)
    {
        List<FancyRow> result = new ArrayList<>();
        List<FancyChatElement> currentRow = new ArrayList<>();

        int currentX = 0;
        int rowSize = 1;

        for (FancyChatElement element : this)
        {
            int[] size = element.getSize(fontRenderer);
            if (currentX + size[0] > rowWidth)
            {
                if (element instanceof FancyChatComponentElement)
                {
                    final ITextComponent component = ((FancyChatComponentElement) element).chatComponent;
                    String part1 = fontRenderer.trimStringToWidth(component.getUnformattedText(), rowWidth - currentX);
                    String part2 = component.getUnformattedText().substring(part1.length());
                    FancyChatComponentElement newElement = new FancyChatComponentElement(part1);
                    newElement.setColor(element.getColor());
                    rowSize = Math.max(rowSize, newElement.getRowSize());

                    currentRow.add(newElement);
                    result.add(new FancyRow(currentRow, rowSize));
                    currentRow = new ArrayList<>();

                    newElement = new FancyChatComponentElement(part2);
                    newElement.chatComponent.setStyle(component.getStyle());
                    newElement.setColor(element.getColor());
                    currentRow.add(newElement);

                    size = newElement.getSize(fontRenderer);
                    currentX = size[0];
                }
                else
                {
                    result.add(new FancyRow(currentRow, rowSize));
                    currentRow = new ArrayList<>();
                    currentX = 0;
                }
            }
            else
            {
                currentRow.add(element);
                currentX += size[0];
                rowSize = Math.max(rowSize, element.getRowSize());
            }
        }
        if (!currentRow.isEmpty())
            result.add(new FancyRow(currentRow, rowSize));
        return result;
    }

    public static class ElementPosData
    {
        public final FancyChatElement element;
        public final int elementIndex;
        public final int offsetInElement;
        public final int offsetInLine;

        public ElementPosData(FancyChatElement element, int elementIndex, int offsetInElement, int offsetInLine)
        {
            this.element = element;
            this.elementIndex = elementIndex;
            this.offsetInElement = offsetInElement;
            this.offsetInLine = offsetInLine;
        }
    }

    public static class ElementRangeData
    {
        public final List<FancyChatElement> elements;
        public final int startElementIndex;
        public final int startElementPosOffset;
        public final int endElementPosOffset;

        public ElementRangeData(List<FancyChatElement> elements, int startElementIndex, int startElementPosOffset, int endElementPosOffset)
        {
            this.elements = elements;
            this.startElementIndex = startElementIndex;
            this.startElementPosOffset = startElementPosOffset;
            this.endElementPosOffset = endElementPosOffset;
        }
    }

    public static class FancyRow
    {
        public final List<FancyChatElement> elements;
        public final int rowSize;

        public FancyRow(List<FancyChatElement> elements, int rowSize)
        {
            this.elements = elements;
            this.rowSize = rowSize;
        }
    }
}