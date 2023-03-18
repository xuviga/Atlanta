package ru.imine.client.fancychat.gui.overlay;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import ru.imine.client.fancychat.gui.GuiFancyChatInput;
import ru.imine.client.fancychat.gui.GuiFancyTextField;
import ru.imine.client.fancychat.image.IChatRenderable;
import ru.imine.client.fancychat.image.ImageLoader;
import ru.imine.client.fancychat.smile.SmileDictionary;
import ru.imine.shared.fancychat.chat.FancyChatSmileElement;
import ru.imine.shared.fancychat.smile.Category;
import ru.imine.shared.fancychat.smile.Smile;

import java.util.*;

public class GuiFancySmileOverlay
{
    private static final IChatRenderable NO_ENTRY = ImageLoader.loadImageNow(new ResourceLocation("imine", "textures/gui/no_entry.png"));

    private static final List<String> TOOL_TIP_LOCKED = new ArrayList<>();

    private final GuiFancyChatInput gui;
    private final List<GuiFancyTextField> textFields;
    private final int xPosition;
    private final int yPosition;
    private int smilePanelOffset;

    private final List<Category> categories;
    public boolean isOverlayEnabled;
    private Category selectedCategory;
    private final Map<Integer, Smile> smileGrid = new HashMap<>();
    private int skipCategoryRows=0;
    private int skipSmileRows=0;

    private final int[] lockedLabelPos = new int[2];
    private long lockedLabelStart;

    static
    {
        TOOL_TIP_LOCKED.add("Данный смайлик заблокирован");
        TOOL_TIP_LOCKED.add("Получить смайлики можно из контейнеров,");
        TOOL_TIP_LOCKED.add("которые можно купить на сайте, или у других игроков");
    }

    public GuiFancySmileOverlay(GuiFancyChatInput gui, GuiFancyTextField textField, int x, int y)
    {
        this(gui, Collections.singletonList(textField), x, y);
    }

    public GuiFancySmileOverlay(GuiFancyChatInput gui, List<GuiFancyTextField> textFields, int x, int y)
    {
        this.gui=gui;
        this.textFields=textFields;
        this.xPosition=x;
        this.yPosition=y;

        categories = SmileDictionary.getAllCategories();
        if (!categories.isEmpty())
            selectedCategory = SmileDictionary.getAllCategories().get(0);
        else
            selectedCategory = null;
    }

    public void drawOverlay(FontRenderer fontRenderer, int mouseX, int mouseY)
    {
        if (!isOverlayEnabled)
            return;

        int smilePanelX = xPosition + 19;
        int offset = Smile.SMILE_ROW_SIZE + 2;

        //Category panel
        Gui.drawRect(xPosition - 2, yPosition - 3, xPosition + 17, yPosition + 85, 0xFFFFFFFF);
        Gui.drawRect(xPosition, yPosition + 4, xPosition + 15, yPosition + 79, 0xAAAAAAAA);
        int i = 0;
        int co = 0;
        for (Category category : categories)
        {
            if (i>=5)
                break;
            if (co++<skipCategoryRows)
                continue;
            int height = Smile.SMILE_ROW_SIZE;
            int width = (category.getImage().getWidth() * height) / category.getImage().getHeight();
            category.getImage().render(xPosition + 1 + (height-width)/2, yPosition + 5 + (Smile.SMILE_ROW_SIZE + 2) * i, width, height);
            i++;
        }

        if (i+skipCategoryRows>=5)
        {
            GL11.glPushMatrix();
            fontRenderer.drawString("∧", xPosition+3, yPosition-4, 0xCC999999, false);
            fontRenderer.drawString("∨", xPosition+3, yPosition+77, 0xCC999999, false);
            GL11.glPopMatrix();
        }
        if (i<=4)
            skipCategoryRows=Math.max(0,skipCategoryRows-1);

        //Smile panel
        if (selectedCategory==null)
            return;

        Gui.drawRect(smilePanelX - 3, yPosition - 3, smilePanelX + 92, yPosition + 85, 0xFFFFFFFF);
        Gui.drawRect(smilePanelX - 1, yPosition - 1, smilePanelX + 90, yPosition + 75, 0xAAAAAAAA);

        i = 0;
        int j = -smilePanelOffset;
        int j_inc = 1;
        int rowCounter=0;
        smileGrid.clear();
        boolean noEntry = false;
        for (Smile smile : selectedCategory.getSmiles())
        {
            if (j >= 5)
                break;
            if (rowCounter >= skipSmileRows)
            {
                int height = Smile.SMILE_ROW_SIZE * smile.rowSize;
                int width = (smile.getImage().getWidth() * height) / smile.getImage().getHeight();
                if (j >= 0)
                {
                    int x = smilePanelX + i * (Smile.SMILE_ROW_SIZE + 2) + (height - width) / 2;
                    int y = yPosition + j * (Smile.SMILE_ROW_SIZE + 2);
                    if (!smile.unlocked && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height)
                        noEntry = true;
                    smile.getImage().render(x, y, width, height, smile.unlocked ? 0xFFFFFFFF : 0x55FFFFFF);
                    for (int k = 0; k < smile.rowSize; k++)
                    {
                        smileGrid.put(i + j * 6 + k, smile);
                        for (int l = 0; l <= smile.rowSize; l++)
                            smileGrid.put(i + (j + l) * 6 + k, smile);
                    }
                }
            }
            i += smile.rowSize;
            j_inc = Math.max(j_inc, smile.rowSize);
            if (i >= 6)
            {
                if (rowCounter >= skipSmileRows)
                {
                    i = 0;
                    j += j_inc;
                    j_inc = 1;
                }
                else
                {
                    rowCounter += j_inc;
                    j_inc = 1;
                    i = 0;
                }
            }
        }

        if (j+rowCounter>=5)
            fontRenderer.drawString("↑§f.§r↓", smilePanelX + 75, yPosition + 76, 0xCC999999, false);
        if (j<4)
            skipSmileRows=Math.max(0,skipSmileRows-1);

        if (System.currentTimeMillis()-lockedLabelStart<10000)
        {
            gui.drawHoveringText(TOOL_TIP_LOCKED, lockedLabelPos[0], lockedLabelPos[1], fontRenderer);
        }

        if (mouseX >= smilePanelX && mouseX <= smilePanelX + offset * 6 && mouseY>=yPosition)
        {
            if (mouseX <= offset)
            {
                int index = (mouseY-5) / offset + skipCategoryRows;
                if (index >= 0 && index < categories.size())
                {
                    Category category = categories.get(index);
                    if (category!=null)
                    {
                        GL11.glPushMatrix();
                        GL11.glScaled(0.7142, 0.7142, 1);
                        fontRenderer.drawString(category.displayName,
                                (xPosition + 20) * 14 / 10, (yPosition + 77) * 14 / 10, 0xFF777777);
                        GL11.glPopMatrix();
                    }
                }
            }
            else if (mouseY<yPosition+55)
            {
                int indexX = (mouseX - smilePanelX) / offset;
                int indexY = (mouseY - yPosition) / offset;
                int index = indexX + indexY * 6;
                if (index >= 0 && index < smileGrid.size())
                {
                    Smile smile = smileGrid.get(index);
                    if (smile != null)
                    {
                        GL11.glPushMatrix();
                        GL11.glScaled(0.7142, 0.7142, 1);
                        fontRenderer.drawString(smile.rarity.textFormatting + smile.name,
                                (xPosition + 20) * 14 / 10, (yPosition + 77) * 14 / 10, 0xFF777777);
                        GL11.glPopMatrix();
                    }
                }
            }
        }

        if (noEntry) {
            assert NO_ENTRY != null;
            NO_ENTRY.render(mouseX + 3, mouseY + 3, 8, 8);
        }
    }

    public boolean mouseClicked(int realX, int realY)
    {
        if (!isOverlayEnabled)
            return false;
        int mouseX = realX-xPosition;
        int mouseY = realY-yPosition;
        boolean inRange = mouseX>=-2 && mouseX<=110 && mouseY>=-3 && mouseY<=85;
        lockedLabelStart=0;
        if (mouseX<14)
        {
            if (mouseY<=3)
            {
                skipCategoryRows = Math.max(0, skipCategoryRows - 1);
                return true;
            }
            else if (mouseY>=79)
            {
                skipCategoryRows++;
                return true;
            }
        }
        if (selectedCategory!=null)
        {
            int offset = Smile.SMILE_ROW_SIZE + 2;
            if (mouseY >= 76 && mouseY <= 82)
            {
                if (mouseX >= 92 && mouseX <= 100)
                    skipSmileRows = Math.max(0, skipSmileRows - 1);
                if (mouseX >= 100 && mouseX <= 107)
                    skipSmileRows += 1;
                return true;
            }
            if (inRange)
            {
                if (mouseX <= offset)
                {
                    int index = (mouseY - 5) / offset + skipCategoryRows;
                    if (index >= 0 && index < categories.size())
                        selectedCategory = categories.get(index);
                }
                else if (mouseX >= 19 && mouseX <= 19 + offset * 6)
                {
                    int index = (mouseX - 19) / offset;
                    index += mouseY / offset * 6;
                    if (index < smileGrid.size())
                    {
                        Smile smile = smileGrid.get(index);
                        if (smile != null)
                        {
                            if (smile.unlocked)
                            {
                                FancyChatSmileElement element = new FancyChatSmileElement(smile.category.name, smile.name);
                                for (GuiFancyTextField textField : textFields)
                                {
                                    if (textField.isFocused())
                                        textField.moveCursorBy(textField.fancyLine.insertElement(textField.getCursorPosition(), element, textField.symLimit));
                                }
                            }
                            else
                            {
                                lockedLabelPos[0] = realX;
                                lockedLabelPos[1] = realY;
                                lockedLabelStart = System.currentTimeMillis();
                            }
                        }
                    }
                }
            }
        }
        return inRange;
    }
}