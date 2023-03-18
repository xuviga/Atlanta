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
import ru.will0376.cases.ColorHelper;
import ru.will0376.cases.Strings;
import ru.will0376.cases.model.ModelGuiCase;
import ru.will0376.cases.network.PacketsDecoder;
import ru.will0376.cases.network.Recieve;
import ru.will0376.cases.pojo.CaseItem;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class GuiCaseView extends MPGui {
   float rollMove = 0.0F;
   int counter = 0;
   float gridAnim = 0.0F;
   float animation = 0.0F;
   float mainAnimation = 25.0F;
   float fenceAnim = 0.0F;
   float numAnim = 0.0F;
   ModelGuiCase model = new ModelGuiCase();
   RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
   private final String caseName;
   private boolean isWon = false;
   private List ROLLING_ITEMS = null;
   private boolean isCaseLoading = false;
   private float caseLoadingRotation = 160.0F;
   private float littleWaiting = 0.0F;
   private float slow = randFloat(9.38F, 9.42F);
   private int randStop = 0;
   private final boolean isRolling = false;
   private final boolean useful1 = true;
   private int current = 0;
   private int lastInt = 0;
   private int itemsCount;
   private ItemStack itemStack;
   private int quality;

   public GuiCaseView(String caseName) {
      this.caseName = caseName;
   }

   public static float randFloat(float min, float max) {
      return min + (new Random()).nextFloat() * (max - min);
   }

   public static int randInt(int min, int max) {
      int randomNum = (new Random()).nextInt(max - min + 1) + min;
      return randomNum;
   }

   public void drawScreen(int x, int y, float ticks) {
      super.drawScreen(x, y, ticks);
      this.drawDefaultBackground();
      int guiX = super.width / 2 - 127;
      int guiY = super.height / 2 - 113;
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureCW.png"));
      this.drawTexturedModalRect(guiX, guiY, 0, 0, 256, 72);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
      this.drawTexturedModalRect(guiX, guiY, 0, 0, 256, 255);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
      this.drawScaledString(Strings.itemsCanDrop, (float) (guiX + 12), (float) (guiY + 63), 0.85F, MPGui.TextPosition.LEFT);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
      if (!Recieve.isRolling) {
         if (!this.isCaseLoading) {
            if (!this.isHover(guiX + 65, guiY + 39, 125, 14)) {
               this.drawTexturedModalRect(guiX + 65, guiY + 39, 0, 214, 125, 14);
            } else {
               this.drawTexturedModalRect(guiX + 65, guiY + 39, 0, 228, 125, 14);
            }

            if (this.isClicked(guiX + 65, guiY + 39, 125, 14)) {
               this.isCaseLoading = true;
               super.mc.player.sendChatMessage("/jcases roll " + this.caseName);
               this.randStop = randInt(12, 21);
               super.isClicked = false;
            }

            this.draw3DCase(guiX + 101, guiY + 13, "case" + this.caseName, 160.0F);
            this.drawScaledString(Strings.openPrice(), (float) (guiX + 128), (float) (guiY + 42), 0.92F, MPGui.TextPosition.CENTER);
         } else {
            this.draw3DCase(guiX + 101, guiY + 13, "case" + this.caseName, this.caseLoadingRotation);
            this.caseLoadingRotation += super.delta * 2.0F;
            super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
            this.drawTexturedModalRect(guiX + 65, guiY + 39, 0, 242, 125, 14);
            this.drawScaledString(Strings.opening, (float) (guiX + 81), (float) (guiY + 42), 0.92F, MPGui.TextPosition.LEFT);
         }
      } else {
         if (this.ROLLING_ITEMS == null) {
            this.ROLLING_ITEMS = PacketsDecoder.getRandomItemsForRoll();
         }

         super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
         this.drawTexturedModalRect(guiX + 65, guiY + 39, 0, 242, 125, 14);
         this.drawScaledString(Strings.opening, (float) (guiX + 81), (float) (guiY + 42), 0.92F, MPGui.TextPosition.LEFT);
         this.drawRollingItems();
      }

      this.drawItemsGrid(x, y, guiX + 8, guiY + 73);
      if (this.isWon) {
         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, 0.0F, 400.0F);
         this.drawWonScreen(x, y, ticks);
         GL11.glPopMatrix();
      }

   }

   public void onGuiClosed() {
      Recieve.isRolling = false;
   }

   private void drawRollingItems() {
      ScaledResolution scaled = new ScaledResolution(super.mc);
      int factor = scaled.getScaleFactor();
      int guiX = super.width / 2 - 127;
      int guiY = super.height / 2 - 113;
      GL11.glPushMatrix();
      GL11.glEnable(3089);
      GL11.glScissor((guiX + 66) * factor, super.height * factor - (guiY + 7) * factor - 30 * factor, 123 * factor, 30 * factor);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));

      for (int i = 0; i < this.ROLLING_ITEMS.size(); ++i) {
         Color cl = ColorHelper.getColorByRare(((CaseItem) this.ROLLING_ITEMS.get(i)).getRarity());
         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glTranslatef((float) guiX + this.rollMove + 66.0F + 40.0F * (float) i, (float) (guiY + 8), 0.0F);
         this.drawTexturedModalRect(0, 0, 127, 0, 38, 20);
         int poop = (int) ((float) guiX + this.rollMove + 66.0F + 40.0F * (float) i);
         int contact = guiX + 127;
         if (contact >= poop && contact < 40 + poop) {
            this.current = i;
         }

         if (this.lastInt != this.current) {
            this.playSound("rolling");
         }

         this.lastInt = this.current;
         GL11.glColor4f((float) cl.getRed() / 255.0F, (float) cl.getGreen() / 255.0F, (float) cl.getBlue() / 255.0F, 1.0F);
         this.drawTexturedModalRect(0, 20, 127, 20, 38, 8);
         GL11.glDisable(3042);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
         ItemStack is = ((CaseItem) this.ROLLING_ITEMS.get(i)).getItemStack();
         this.draw3DGuiItem(is, (float) guiX + this.rollMove + 85.0F + 40.0F * (float) i, (float) (guiY + 26), 30.0F);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         if (is.getDisplayName().length() > 12) {
            this.drawScaledString(is.getDisplayName().substring(0, 11) + "...", (float) guiX + this.rollMove + 67.0F + 40.0F * (float) i, (float) (guiY + 29), 0.48F, MPGui.TextPosition.LEFT);
         } else {
            this.drawScaledString(is.getDisplayName(), (float) guiX + this.rollMove + 67.0F + 40.0F * (float) i, (float) (guiY + 29), 0.48F, MPGui.TextPosition.LEFT);
         }

         super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
      }

      GL11.glDisable(3089);
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glTranslatef(0.0F, 0.0F, 400.0F);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
      this.drawTexturedModalRect(guiX + 102, guiY + 7, 165, 0, 51, 30);
      GL11.glPopMatrix();
      if (this.rollMove >= (float) (-((this.ROLLING_ITEMS.size() - 4) * 40) + this.randStop)) {
         this.rollMove -= super.delta * this.slow;
         if (this.slow >= 0.05F) {
            this.slow -= super.delta / 50.0F;
         }
      } else if (this.littleWaiting < 6.0F) {
         this.littleWaiting += super.delta * this.slow;
      } else {
         this.itemStack = ((CaseItem) this.ROLLING_ITEMS.get(57)).getItemStack().copy();
         this.itemsCount = this.itemStack.getCount();
         this.quality = ((CaseItem) this.ROLLING_ITEMS.get(57)).getRarity();
         this.isWon = true;
      }

   }

   private void drawItemsGrid(int mouseX, int mouseY, int x, int y) {
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));

      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 5; ++j) {
            if (this.counter < (int) this.gridAnim) {
               super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/CaseShopTextureL.png"));
               this.drawTexturedModalRect(x + 3 + 47 * j, y + 2 + 48 * i, 82, 0, 45, 33);
               Color cl = ColorHelper.getColorByRare(((CaseItem) PacketsDecoder.getCaseItemsList().get(this.counter)).getRarity());
               GL11.glPushMatrix();
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               this.drawTexturedModalRect(x + 3 + 47 * j, y + 2 + 48 * i + 33, 82, 33, 45, 12);
               GL11.glColor4f((float) cl.getRed() / 255.0F, (float) cl.getGreen() / 255.0F, (float) cl.getBlue() / 255.0F, 0.9F);
               this.drawTexturedModalRect(x + 3 + 47 * j, y + 2 + 48 * i + 33, 82, 33, 45, 12);
               GL11.glDisable(3042);
               GL11.glColor3f(1.0F, 1.0F, 1.0F);
               GL11.glPopMatrix();
               ItemStack is = ((CaseItem) PacketsDecoder.getCaseItemsList().get(this.counter)).getItemStack();
               this.draw3DGuiItem(is, (float) (x + 25 + 47 * j), (float) (y + 29 + 48 * i), 38.0F);
               String name = is.getDisplayName();
               if (((CaseItem) PacketsDecoder.getCaseItemsList().get(this.counter)).getRarity() == 5) {
                  name = "\u2726 " + name + " \u2726";
               }

               if (name.length() > 14) {
                  this.drawScaledString(name.substring(0, 13) + "-", (float) (x + 5 + 47 * j), (float) (y + 36 + 48 * i), 0.5F, MPGui.TextPosition.LEFT);
                  if (name.length() > 27) {
                     this.drawScaledString(name.substring(13, 26) + "...", (float) (x + 5 + 47 * j), (float) (y + 41 + 48 * i), 0.5F, MPGui.TextPosition.LEFT);
                  } else {
                     this.drawScaledString(name.substring(13), (float) (x + 5 + 47 * j), (float) (y + 41 + 48 * i), 0.5F, MPGui.TextPosition.LEFT);
                  }
               } else {
                  this.drawScaledString(name, (float) (x + 5 + 47 * j), (float) (y + 38 + 48 * i), 0.5F, MPGui.TextPosition.LEFT);
               }

               ++this.counter;
            }
         }
      }

      if (this.gridAnim < (float) PacketsDecoder.getCaseItemsList().size()) {
         this.gridAnim += super.delta / 2.0F;
      }

      if (this.gridAnim > (float) PacketsDecoder.getCaseItemsList().size()) {
         this.gridAnim = (float) PacketsDecoder.getCaseItemsList().size();
      }

      this.counter = 0;
   }

   private void drawWonScreen(int x, int y, float ticks) {
      this.drawDefaultBackground();
      ScaledResolution scaled = new ScaledResolution(super.mc);
      int factor = scaled.getScaleFactor();
      short panX = 205;
      byte panY = 105;
      int guiX = super.width / 2 - panX / 2;
      int guiY = super.height / 2 - panY / 2;
      Color color = ColorHelper.getColorByRare(this.quality);
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/OpcTexture.png"));
      this.drawTexturedModalRect(guiX - 7, guiY - 16, 0, 0, 219, 146);
      GL11.glColor4f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, 0.7F);
      this.drawTexturedModalRect(guiX - 7, guiY - 16, 0, 0, 219, 16);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      //super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/OpcTextureU.png"));
      GL11.glPopMatrix();
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
            this.isWon = false;
            super.isClicked = false;
         }

         this.drawScaledString(Strings.continueOK, (float) (guiX - 5 + 67 + 42), (float) (guiY - 11 + 124), 0.76F, MPGui.TextPosition.CENTER);
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
      ItemStack is = this.itemStack.copy();
      is.setItemDamage(this.itemStack.getMetadata());
      is.setCount(1);
      EntityItem entityItem = new EntityItem(super.mc.world, 0.0D, 0.0D, 0.0D, is);
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
         fontRenderer.drawString(TextFormatting.DARK_GRAY + "" + itemsCount, 0, 0, -872415232);
         GL11.glPopMatrix();
      }

      if (this.mainAnimation <= 8.0F) {
         this.animation += super.delta;
      } else {
         this.mainAnimation += -super.delta / 6.0F;
      }

   }
}
