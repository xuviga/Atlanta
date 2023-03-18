package ru.imine.client.fancychat.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import ru.imine.client.fancychat.gui.IFancyGuiScreen;
import ru.imine.client.fancychat.image.IChatRenderable;
import ru.imine.client.fancychat.image.ImageLoader;
import ru.imine.version.client.GuiMapper;

public class GuiSmileOverlayButton extends GuiButton
{
    private static final IChatRenderable SMILE_ICON = ImageLoader.loadImageNow(new ResourceLocation("imine", "textures/gui/fancychat/smile.png"));

    private final IFancyGuiScreen gui;

    public GuiSmileOverlayButton(int id, int x, int y, int w, int h, IFancyGuiScreen gui)
    {
        super(id, x, y, w, h, "");
        this.gui = gui;
    }

    //1.10.2
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        drawButton(mc,mouseX,mouseY,0);
    }

    //1.12.2
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (!this.visible)
            return;
        int xPosition = GuiMapper.instance().getX(this);
        int yPosition = GuiMapper.instance().getY(this);
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFFFFFFFF);
        Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, 0xAAAAAAAA);
        SMILE_ICON.render(xPosition + 1, yPosition + 1, width - 2, height - 2, 0x99FFFFFF);
    }
}
