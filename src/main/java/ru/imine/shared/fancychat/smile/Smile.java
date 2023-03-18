package ru.imine.shared.fancychat.smile;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.client.fancychat.gui.GuiFancyChat;
import ru.imine.client.fancychat.image.IChatRenderable;
import ru.imine.client.fancychat.image.ImageLoader;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.util.ObjectWrapper;
import ru.imine.shared.util.Rarity;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.Serializable;
import java.net.URL;

public class Smile implements Serializable
{
    private static final long serialVersionUID = 322L;
    public static final int SMILE_ROW_SIZE = 13;

    private static boolean isServer = FMLCommonHandler.instance().getSide()== Side.SERVER;

    public final String name;
    public transient Category category;
    public final byte rowSize;
    public final Rarity rarity;

    public final byte imageSourceType;
    public final String imageSource;
    public final byte soundSourceType;
    public final String soundSource;

    @SideOnly(Side.CLIENT) private transient ObjectWrapper<IChatRenderable> image;
    @SideOnly(Side.CLIENT) private transient Clip sound;
    @SideOnly(Side.CLIENT) public transient boolean unlocked;

    public Smile(Category category, String name, Rarity rarity, byte rowSize,
                    byte imageSourceType, String imageSource, byte soundSourceType, String soundSource)
    {
        this.category = category;
        this.name = name;
        this.rarity = rarity;
        this.rowSize = rowSize;
        this.imageSourceType = imageSourceType;
        this.imageSource = imageSource;
        this.soundSourceType = soundSourceType;
        this.soundSource = soundSource;
        category.smiles.put(name, this);
        if (!isServer)
            loadImage();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Smile)
        {
            Smile smile = (Smile) object;
            return category.equals(smile.category) && name.equals(smile.name);
        }
        return false;
    }

    @Override
    public String toString()
    {
        String cat = category==null ? "<null>" : category.name;
        return ":"+cat+"/"+name+":";
    }

    @Override
    public int hashCode()
    {
        return category.hashCode()+name.hashCode();
    }

    @SideOnly(Side.CLIENT)
    public void loadImage()
    {
        try
        {
            image = new ObjectWrapper<>(null);
            if (imageSourceType == 1)
                image = ImageLoader.loadImage(new URL(imageSource));
            else if (imageSourceType == 2)
            {
                String[] splits = imageSource.split(":");
                if (splits.length == 1)
                    image = ImageLoader.loadImage(new ResourceLocation("imine", imageSource));
                else
                    image = ImageLoader.loadImage(new ResourceLocation(splits[0], splits[1]));
            }

            if (soundSource != null)
            {
                if (soundSourceType == 1)
                {
                    sound = ImageLoader.loadSoundNow(new URL(soundSource));
                }
                else if (soundSourceType == 2)
                {
                    ResourceLocation res;
                    String[] splits = imageSource.split(":");
                    if (splits.length == 1)
                        res = new ResourceLocation("imine", soundSource);
                    else
                        res = new ResourceLocation(splits[0], splits[1]);

                    sound = ImageLoader.loadSoundNow(res);
                }
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to load image or sound for smile " + name, e);
        }
    }

    @SideOnly(Side.CLIENT)
    public IChatRenderable getImage()
    {
        return image.object == null ? ImageLoader.LOADING : image.object;
    }

    @SideOnly(Side.CLIENT)
    public Clip getSound()
    {
        if (sound != null)
        {
            sound.setFramePosition(0);
            FloatControl control = ((FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN));
            float volume = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) * 25;
            control.setValue(volume > 0.3 ? volume - 20 : -80);
        }
        return sound;
    }

    @SideOnly(Side.CLIENT)
    public int[] draw(int x, int y)
    {
        return draw(x, y, 0xFFFFFFFF);
    }

    @SideOnly(Side.CLIENT)
    public int[] draw(int x, int y, int color)
    {
        double yOffset = y - GuiFancyChat.LINE_SPACING / 2;
        x += getImage().render(x, (int) yOffset, getWidth(), getHeight(), color);
        return new int[]{x, y};
    }

    @SideOnly(Side.CLIENT)
    public int getHeight()
    {
        return SMILE_ROW_SIZE * rowSize;
    }

    @SideOnly(Side.CLIENT)
    public int getWidth()
    {
        return getImage().getWidth() * getHeight() / getImage().getHeight();
    }
}
