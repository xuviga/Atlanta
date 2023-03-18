package ru.imine.shared.fancychat.smile;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.client.fancychat.image.IChatRenderable;
import ru.imine.client.fancychat.image.ImageLoader;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.util.ObjectWrapper;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Category implements Serializable
{
    private static final long serialVersionUID = 322L;

    public final String name;
    public final String displayName;
    protected final LinkedHashMap<String,Smile> smiles = new LinkedHashMap<>();

    protected final byte imageSourceType;
    protected final String imageSource;

    @SideOnly(Side.CLIENT) private transient ObjectWrapper<IChatRenderable> image;

    public Category(String name, String displayName, byte imageSourceType, String imageSource)
    {
        this.name = name;
        this.displayName = displayName;
        this.imageSourceType = imageSourceType;
        this.imageSource=imageSource;
    }

    public List<Smile> getSmiles()
    {
        return new ArrayList<>(smiles.values());
    }

    public Smile getSmileByName(String name)
    {
        return smiles.get(name);
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof Category && ((Category) object).name.equalsIgnoreCase(name);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(name).append(":{");
        for (Smile smile : smiles.values())
            builder.append(smile.name).append(",");
        return builder.append("}").toString();
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
                    image = ImageLoader.loadImage(new ResourceLocation("imine", "textures/gui/fancychat"+imageSource));
                else
                    image = ImageLoader.loadImage(new ResourceLocation(splits[0], "textures/"+splits[1]));
            }
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to load image for category "+name,e);
        }
    }

    @SideOnly(Side.CLIENT)
    public IChatRenderable getImage()
    {
        return image.object == null ? ImageLoader.LOADING : image.object;
    }
}
