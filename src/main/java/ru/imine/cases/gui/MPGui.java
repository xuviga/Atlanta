package ru.imine.cases.gui;

import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import ru.imine.cases.Rolling;
import ru.imine.cases.model.ModelItemCase;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MPGui extends GuiScreen {
   private static Map images = Maps.newHashMap();
   final double tc = 60.0D;
   protected boolean isClicked = false;
   ModelItemCase modelItemCase = new ModelItemCase();
   float delta = 0.0F;
   long lastTime = System.nanoTime();
   private int MX = 0;
   private int MY = 0;

   public static void bindTexture(String name) {
      if (images.get(name) != null) {
         GL11.glBindTexture(3553, ((DynamicTexture) images.get(name)).getGlTextureId());
      }

   }

   protected void drawScaledString(String text, float x, float y, float scale, TextPosition textPosition) {
      GL11.glPushMatrix();
      GL11.glTranslatef(x, y, 0.0F);
      GL11.glScalef(scale, scale, 0.0F);
      if (textPosition == TextPosition.CENTER) {
         this.drawCenteredString(super.mc.fontRenderer, text, 0, 0, 16777215);
      } else if (textPosition == TextPosition.LEFT) {
         this.drawString(super.mc.fontRenderer, text, 0, 0, 16777215);
      } else if (textPosition == TextPosition.RIGHT) {
         this.drawString(super.mc.fontRenderer, text, -super.fontRenderer.getStringWidth(text), 0, 16777215);
      }

      GL11.glPopMatrix();
   }

   protected void drawCompleteImage(int posX, int posY, int width, int height) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float) posX, (float) posY, 0.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.0F, (float) height, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f((float) width, (float) height, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f((float) width, 0.0F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }

   public boolean isHover(int xx, int yy, int xx1, int yy1) {
      int mouseX = this.MX;
      int mouseY = this.MY;
      return mouseX >= xx && mouseX < xx1 + xx && mouseY >= yy && mouseY < yy1 + yy;
   }

   public boolean isClicked(int xx, int yy, int xx1, int yy1) {
      int mouseX = this.MX;
      int mouseY = this.MY;
      return mouseX >= xx && mouseX < xx1 + xx && mouseY >= yy && mouseY < yy1 + yy && this.isClicked;
   }

   protected void draw3DCase(int x, int y, String texture, float rotation) {
      GL11.glPushMatrix();
      bindTexture(texture);
      GL11.glTranslatef((float) (x + 27), (float) (y - 12), 44.0F);
      GL11.glScalef(20.0F, 20.0F, 20.0F);
      GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
      GL11.glDisable(2884);
      this.modelItemCase.renderModel(0.0625F);
      GL11.glPopMatrix();
   }

   protected void mouseClicked(int x, int y, int b) throws IOException {
      super.mouseClicked(x, y, b);
      this.isClicked = true;
      Timing timing = new Timing(100);
      timing.start();
   }

   public void drawScreen(int x, int y, float ticks) {
      this.MX = x;
      this.MY = y;
      double ns = 1.6666666666666666E7D;
      long now = System.nanoTime();
      this.delta = (float) ((double) (now - this.lastTime) / ns);
      this.lastTime = now;
   }

   protected void draw3DGuiItem(ItemStack itemStack, float x, float y, float scale) {
      ItemStack is = itemStack.copy();
      is.setCount(1);
      itemStack.setItemDamage(itemStack.getMetadata());
      EntityItem entityItem = new EntityItem(super.mc.world, 0.0D, 0.0D, 0.0D, is);
      entityItem.hoverStart = 0.0F;
      GL11.glPushMatrix();
      GL11.glDisable(2884);
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glTranslatef(x, y, 4.0F);
      GL11.glRotatef(-11.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(160.0F, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
      GL11.glScalef(scale, scale, scale);
      mc.getRenderManager().renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.2F, 0.2F, false);
      RenderHelper.disableStandardItemLighting();
      GL11.glPopMatrix();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void playSound(String name) {
      super.mc.player.playSound(Rolling.test, 1.0F, 1.0F);
   }

   public void addTex(String name, String image) {
      try {
         images.put(name, new DynamicTexture(ImageIO.read(new URL(image))));
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   enum TextPosition {
      LEFT,
      CENTER,
      RIGHT
   }

   class Timing extends Thread {
      private int timer;

      public Timing(int timer) {
         this.timer = timer;
      }

      public void run() {
         try {
            Thread.sleep(this.timer);
         } catch (InterruptedException var2) {
            var2.printStackTrace();
         }

         MPGui.this.isClicked = false;
         this.stop();
      }
   }
}
