package ru.imine.client.fancychat.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import ru.imine.client.fancychat.gui.IFancyGuiScreen;
import ru.imine.client.fancychat.image.IChatRenderable;
import ru.imine.client.fancychat.image.ImageLoader;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.core.player.PlayerRank;
import ru.imine.version.client.GuiMapper;

import java.util.ArrayList;
import java.util.List;

public class GuiColorOverlayButton extends GuiButton
{
    private static final IChatRenderable PALETTE_ICON = ImageLoader.loadImageNow(new ResourceLocation("imine", "textures/gui/fancychat/palette.png"));

    private static final List<String> TOOLTIP_DONATE_COLOR = new ArrayList<>();
    private static final List<String> TOOLTIP_DONATE_BCG = new ArrayList<>();

    private final IFancyGuiScreen gui;

    static
    {
        //todo тултипы со смайликами
        TOOLTIP_DONATE_COLOR.add("С ранга §9Diamond§r появляется возможность");
        TOOLTIP_DONATE_COLOR.add("раскрашивать текст своих сообщений в любой цвет,");
        TOOLTIP_DONATE_COLOR.add("а с ранга §aEmerald§r - выбирать цвет их фона");

        TOOLTIP_DONATE_BCG.add("С ранга §aEmerald§r появляется возможность");
        TOOLTIP_DONATE_BCG.add("выбирать цвет фона твоих сообщений");
    }


    public GuiColorOverlayButton(int id, int x, int y, int w, int h, IFancyGuiScreen gui)
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
        super.drawButton(mc, mouseX, mouseY, partialTicks);
        FontRenderer fontRenderer = GuiMapper.instance().getFontRenderer();
        if (!this.visible)
            return;
        boolean hovering = mouseX >= GuiMapper.instance().getX(this)
                && mouseY >= GuiMapper.instance().getY(this)
                && mouseX < GuiMapper.instance().getX(this) + this.width
                && mouseY < GuiMapper.instance().getY(this) + this.height;
        if (hovering)
        {
            if (!AiMineUser.getLocalPlayer().hasRank(PlayerRank.DIAMOND))
                gui.drawHoveringText(TOOLTIP_DONATE_COLOR, mouseX, mouseY + 15, fontRenderer);
            else if (!AiMineUser.getLocalPlayer().hasRank(PlayerRank.EMERALD))
                gui.drawHoveringText(TOOLTIP_DONATE_BCG, mouseX, mouseY + 15, fontRenderer);
        }
        int xPosition = GuiMapper.instance().getX(this);
        int yPosition = GuiMapper.instance().getY(this);
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFFFFFFFF);
        Gui.drawRect(xPosition + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, 0xAAAAAAAA);
        PALETTE_ICON.render(xPosition + 1, yPosition + 1, width - 2, height - 2, 0x99FFFFFF);
    }
}