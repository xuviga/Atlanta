package ru.imine.cases.gui;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import ru.imine.cases.Strings;
import ru.imine.cases.network.PacketsDecoder;
import ru.imine.cases.pojo.Case;

import java.io.IOException;

public class GuiCasesShop extends MPGui {
   int move = 0;
   int maxMove = 0;

   public void drawScreen(int x, int y, float ticks) {
      super.drawScreen(x, y, ticks);
      ScaledResolution scaled = new ScaledResolution(super.mc);
      int factor = scaled.getScaleFactor();
      int guiX = super.width / 2 - 127;
      int guiY = super.height / 2 - 113;
      super.mc.renderEngine.bindTexture(new ResourceLocation("cases:textures/gui/CaseShopTexture.png"));
      this.drawTexturedModalRect(guiX, guiY, 0, 0, 256, 255);
      int itemsCount = PacketsDecoder.getCases().size();
      byte colsCount = 3;
      int rowsCount = Math.round((float) itemsCount / (float) colsCount + 0.2F);
      GL11.glPushMatrix();
      GL11.glEnable(3089);
      GL11.glScissor(guiX * factor, super.height * factor - (guiY - 2) * factor - 220 * factor, 247 * factor, 209 * factor);
      this.maxMove = rowsCount * 80 - 208;
      if (this.move >= 0) {
         this.move = 0;
      }

      if (rowsCount > 3) {
         if (this.move <= -this.maxMove) {
            this.move = -this.maxMove;
         }
      } else {
         this.move = 0;
      }

      int counter = 0;

      for (int gnomik = 0; gnomik < rowsCount; ++gnomik) {
         for (int j = 0; j < colsCount; ++j) {
            if (counter < itemsCount) {
               super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/caseshoptexturel.png"));
               this.drawTexturedModalRect(guiX + 10 + j * 78, guiY + 10 + 80 * gnomik + this.move, 0, 0, 76, 78);
               if (!this.isHover(guiX + 11 + j * 78, guiY + 73 + 80 * gnomik + this.move, 74, 14)) {
                  this.drawTexturedModalRect(guiX + 11 + j * 78, guiY + 73 + 80 * gnomik + this.move, 0, 78, 74, 14);
               } else {
                  this.drawTexturedModalRect(guiX + 11 + j * 78, guiY + 73 + 80 * gnomik + this.move, 0, 92, 74, 14);
               }

               if (this.isClicked(guiX + 11 + j * 78, guiY + 73 + 80 * gnomik + this.move, 74, 14)) {
                  super.mc.player.sendChatMessage("/jcases view " + ((Case) PacketsDecoder.getCases().get(counter)).getName());
                  super.isClicked = false;
               }

               this.drawScaledString(((Case) PacketsDecoder.getCases().get(counter)).getName(), (float) (guiX + 12 + j * 78), (float) (guiY + 14 + 80 * gnomik + this.move), 0.85F, MPGui.TextPosition.LEFT);
               this.drawScaledString(Strings.available + ((Case) PacketsDecoder.getCases().get(counter)).getAvailable(), (float) (guiX + 12 + j * 78), (float) (guiY + 63 + 80 * gnomik + this.move), 0.85F, MPGui.TextPosition.LEFT);
               this.drawScaledString(Strings.view, (float) (guiX + 47 + j * 78), (float) (guiY + 76 + 80 * gnomik + this.move), 0.8F, MPGui.TextPosition.CENTER);
               this.draw3DCase(guiX + 21 + j * 78, guiY + 32 + 80 * gnomik + this.move, "case" + ((Case) PacketsDecoder.getCases().get(counter)).getName(), 160.0F);

               ++counter;
            }
         }
      }

      GL11.glDisable(3089);
      GL11.glPopMatrix();
      if (rowsCount > 3) {
         super.mc.renderEngine.bindTexture(new ResourceLocation("cases", "textures/gui/caseshoptexturel.png"));
         this.drawTexturedModalRect(357, 27, 79, 0, 3, 210);
         this.drawTexturedModalRect(357, (int) (27.0F - (float) this.move * (190.0F / (float) this.maxMove)), 76, 0, 3, 20);
         float var14 = (float) (190 / this.maxMove);
         if (this.isClicked(357, 27, 3, 210)) {
            super.isClicked = false;
            this.move = -((int) ((float) (y - 27) / var14));
         }
      }

   }

   protected void mouseClickMove(int x, int y, int b, long l) {
   }

   protected void mouseReleased(int x, int y, int b) {
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.move += Mouse.getEventDWheel() / 120 * 4;
   }

   public void initGui() {
      for (int i = 0; i < PacketsDecoder.getCases().size(); ++i) {
         Case aCase = (Case) PacketsDecoder.getCases().get(i);
         String id = "case" + aCase.getName();
         String texture = aCase.getTexture();
         this.addTex(id, texture);
      }

   }
}
