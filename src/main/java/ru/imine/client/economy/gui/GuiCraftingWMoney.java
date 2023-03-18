package ru.imine.client.economy.gui;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.TabInfo;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.shared.core.player.AiMineUser;
import ru.imine.shared.economy.container.ContainerWorkbenchWMoney;
import ru.imine.version.client.GuiMapper;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiCraftingWMoney extends GuiContainerCore
{
    public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("imine","textures/gui/economy/craft_with_money.png");

    public static boolean isMoneyDeposited;

    public GuiCraftingWMoney(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition)
    {
        super(new ContainerWorkbenchWMoney(playerInv, worldIn, blockPosition), GUI_TEXTURE);
        isMoneyDeposited = false;
        xSize = 176;
        ySize = 195;
        generateInfo("gui.craft_with_money.info_tab");
    }

    @Override
    public void initGui()
    {
        super.initGui();

        addTab(new TabInfo(this, myInfo));
        addTab(new TabMoney(this));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int butt) throws IOException
    {
        int guiX = mouseX - guiLeft;
        int guiY = mouseY - guiTop;

        if (!isMoneyDeposited && guiX>=120 && guiX<=145 && guiY>=31 && guiY<=55)
        {
            mc.world.playSound(mc.player.posX, mc.player.posY, mc.player.posZ,
                    new SoundEvent(new ResourceLocation("imine","deny")), SoundCategory.MASTER, 1, 1, false);
            return;
        }
        if (guiX>=21 && guiX<=87 && guiY>=75 && guiY<=86)
        {
            long balance = AiMineUser.getLocalPlayer().money;
            long cost = ((ContainerWorkbenchWMoney)this.inventorySlots).cost;
            if (balance>=cost)
            {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                isMoneyDeposited = !isMoneyDeposited;
            }
            else
            {
                mc.world.playSound(mc.player.posX, mc.player.posY, mc.player.posZ,
                        new SoundEvent(new ResourceLocation("imine","deny")), SoundCategory.MASTER, 1, 1, false);
            }
        }
        super.mouseClicked(mouseX,mouseY,butt);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        if (!isMoneyDeposited)
            drawRect(120,31,145,55,0xCCFFFFFF);
        super.drawGuiContainerForegroundLayer(mouseX,mouseY);
        FontRenderer fontRenderer = GuiMapper.instance().getFontRenderer(this);
        long balance = AiMineUser.getLocalPlayer().money;
        long cost = ((ContainerWorkbenchWMoney)this.inventorySlots).cost;
        fontRenderer.drawStringWithShadow(Long.toString(isMoneyDeposited ? balance-cost : balance), 116, 98, 14737632);
        fontRenderer.drawStringWithShadow((isMoneyDeposited ? cost : 0) + "/" + cost, 36, 76, 14737632);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.func_146976_a(partialTicks,mouseX,mouseY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}