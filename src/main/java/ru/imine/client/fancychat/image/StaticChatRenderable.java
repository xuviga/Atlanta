package ru.imine.client.fancychat.image;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import ru.imine.version.client.GuiMapper;

import java.awt.image.BufferedImage;

public class StaticChatRenderable implements IChatRenderable
{
    private int textureId = -1;

    protected int width;
    protected int height;
    protected float scale = 1f;
    protected BufferedImage loadBuffer;
    protected int minWidthInSpaces = 4;
    protected String name;

    protected StaticChatRenderable()
    {
    }

    public StaticChatRenderable(BufferedImage image)
    {
        width = image.getWidth();
        height = image.getHeight();
        loadBuffer = image;
    }

    @Override
    public int getWidthInSpaces()
    {
        if (textureId == -1)
        {
            return minWidthInSpaces; // Texture is not loaded yet - most emotes fit just fine into four spaces though.
        }
        return Math.max(minWidthInSpaces, (int) Math.ceil((width * scale) / (float) GuiMapper.instance().getFontRenderer().getCharWidth(' ')));
    }

    @Override
    public int getTextureId()
    {
        if (loadBuffer != null)
        {
            textureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), loadBuffer);
            loadBuffer = null;
        }
        return textureId;
    }

    @Override
    public void disposeTexture()
    {
        if (textureId != -1)
        {
            TextureUtil.deleteTexture(textureId);
            textureId = -1;
        }
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getTexCoordX()
    {
        return 0;
    }

    @Override
    public int getTexCoordY()
    {
        return 0;
    }

    @Override
    public int render(int x, int y, int width, int height, int color)
    {
        GL11.glPushMatrix();

        GlStateManager.bindTexture(getTextureId());
        GL11.glEnable(GL11.GL_BLEND);
        byte r = (byte)(color & 0x000000FF);
        byte g = (byte)((color & 0x0000FF00) >> 8);
        byte b = (byte)((color & 0x00FF0000) >> 16);
        byte a = (byte)((color & 0xFF000000) >> 24);
        if (a==0)
            GL11.glColor3ub(r,g,b);
        else
            GL11.glColor4ub(r,g,b,a);
        GL11.glTranslated(x, y, 0);
        if (this instanceof AnimatedChatRenderable)
            ((AnimatedChatRenderable) this).updateAnimation();
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getTexCoordX(), getTexCoordY(), width, height, width, height);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        return width;
    }
}