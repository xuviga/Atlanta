package ru.will0376.cases.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import ru.will0376.cases.model.ModelGuiCase;

import java.awt.*;

public class GuiCaseWon extends MPGui {
   float animation = 0.0F;
   float mainAnimation = 25.0F;
   float fenceAnim = 0.0F;
   float numAnim = 0.0F;
   ModelGuiCase model = new ModelGuiCase();
   RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
   private int itemsCount;
   private ItemStack itemStack;
   private int quality;

   public GuiCaseWon(ItemStack itemStack, int quality) {
      this.quality = quality;
      this.itemsCount = itemStack.getCount();
      this.itemStack = new ItemStack(itemStack.getItem(), 1);
   }

   public void drawScreen(int x, int y, float ticks) {
      super.drawScreen(x, y, ticks);
      this.drawDefaultBackground();
      ScaledResolution scaled = new ScaledResolution(super.mc);
      int factor = scaled.getScaleFactor();
      short panX = 205;
      byte panY = 105;
      int guiX = super.width / 2 - panX / 2;
      int guiY = super.height / 2 - panY / 2;
      Color color = Color.white;
      switch (this.quality) {
         case 1:
            color = Color.WHITE;
            break;
         case 2:
            color = Color.GREEN;
            break;
         case 3:
            color = Color.BLUE;
            break;
         case 4:
            color = Color.MAGENTA;
            break;
         case 5:
            color = Color.ORANGE;
            break;
         case 6:
            color = Color.RED;
      }

      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/OpcTexture.png"));
      this.drawTexturedModalRect(guiX - 7, guiY - 16, 0, 0, 219, 146);
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glPopMatrix();
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      this.drawCenteredString(super.fontRenderer, this.itemStack.getDisplayName(), super.width / 2, guiY - 12, 16777215);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/OpcTexture.png"));
      if (this.fenceAnim == -0.9F) {
         if (!this.isHover(guiX - 7 + 67, guiY - 16 + 124, 85, 17)) {
            this.drawTexturedModalRect(guiX - 7 + 67, guiY - 16 + 124, 0, 146, 85, 17);
         } else {
            this.drawTexturedModalRect(guiX - 7 + 67, guiY - 16 + 124, 0, 164, 85, 17);
         }

         if (this.isClicked(guiX - 7 + 67, guiY - 16 + 124, 85, 17)) {
            super.mc.displayGuiScreen(null);
         }

         this.drawScaledString("\u0420\u045f\u0421\u0402\u0420\u0455\u0420\u0491\u0420\u0455\u0420\u00bb\u0420\u00b6\u0420\u0451\u0421\u201a\u0421\u040a", (float) (guiX - 5 + 67 + 42), (float) (guiY - 11 + 124), 0.76F, MPGui.TextPosition.CENTER);
      }

      if (this.animation >= 360.0F) {
         this.animation = 0.0F;
      }

      if (this.mainAnimation <= 8.0F) {
         this.mainAnimation = 8.0F;
      }

      if (this.mainAnimation <= 15.0F) {
         this.fenceAnim -= super.delta / 90.0F;
      }

      if (this.fenceAnim <= -0.9F) {
         this.fenceAnim = -0.9F;
      }

      if ((double) this.fenceAnim <= -0.8D) {
         this.numAnim += super.delta / 60.0F;
      }

      if (this.numAnim >= 0.3F) {
         this.numAnim = 0.3F;
      }

      GL11.glPushMatrix();
      EntityItem entityItem = new EntityItem(super.mc.world, 0.0D, 0.0D, 0.0D, this.itemStack);
      entityItem.hoverStart = 0.0F;
      GL11.glEnable(3089);
      GL11.glScissor(guiX * factor, super.height * factor - guiY * factor - panY * factor, panX * factor, panY * factor);
      GL11.glEnable(2929);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(2884);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CasesOpened.png"));
      GL11.glTranslatef((float) (super.width / 2 + 7), (float) (super.height / 2 - 113), 360.0F);
      GL11.glScalef(118.0F, 118.0F, 1.0F);
      GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(this.mainAnimation, 0.0F, 1.0F, 1.0F);
      this.model.renderModel(0.0625F, this.fenceAnim, this.numAnim);
      RenderHelper.disableStandardItemLighting();
      GL11.glTranslatef(-0.5F, 1.35F, 0.0F);
      GL11.glScalef(1.2F, 1.2F, 1.2F);
      GL11.glRotatef(8.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(this.animation + 90.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      mc.getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.2F, 0.2F, false);
      GL11.glDisable(3089);
      GL11.glPopMatrix();
      if ((double) this.numAnim >= 0.22D) {
         GL11.glPushMatrix();
         if (this.itemsCount >= 10) {
            GL11.glTranslatef((float) (super.width / 2 + 77), (float) (super.height / 2 - 37), 400.0F);
         } else {
            GL11.glTranslatef((float) (super.width / 2 + 77) + 4.6F, (float) (super.height / 2 - 37), 400.0F);
         }

         GL11.glScalef(1.5F, 1.5F, 1.5F);
         super.fontRenderer.drawString(TextFormatting.DARK_GRAY + "" + this.itemsCount, 0, 0, -872415232);
         GL11.glPopMatrix();
      }

      if (this.mainAnimation <= 8.0F) {
         this.animation += super.delta;
      } else {
         this.mainAnimation += -super.delta / 6.0F;
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
