package ru.imine.client.fancychat.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.NodeList;
import ru.imine.shared.fancychat.FancyChat;
import ru.imine.shared.util.ObjectWrapper;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ImageLoader
{
    public static final IChatRenderable NOT_FOUND = loadImageNow(new ResourceLocation("imine", "textures/404.png"));
    public static final IChatRenderable LOADING = loadImageNow(new ResourceLocation("imine", "textures/gui/loading.gif"));
    private static final ConcurrentMap<Object, ObjectWrapper<IChatRenderable>> imageCache = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Object, Object> soundCache = new ConcurrentHashMap<>();
    private static final Random RND = new Random();

    public static ObjectWrapper<IChatRenderable> loadImage(URL url) throws IOException
    {
        ObjectWrapper<IChatRenderable> wrapper = imageCache.get(url);
        if (wrapper!=null)
            return wrapper;
        ObjectWrapper<IChatRenderable> image = new ObjectWrapper<>(null);
        imageCache.put(url,image);
        new Thread(()-> {
            try
            {
                URLConnection con = url.openConnection();
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
                try (InputStream in = con.getInputStream())
                {
                    image.object = loadImageInternal(in);
                }
            }
            catch (SocketTimeoutException ignored)
            {
                image.object = NOT_FOUND;
            }
            catch (IOException e)
            {
                FancyChat.LOGGER.error("Failed to load image!", e);
                image.object = NOT_FOUND;
            }
        }).start();
        return image;
    }

    public static ObjectWrapper<IChatRenderable> loadImage(ResourceLocation resourceLocation)
    {
        ObjectWrapper<IChatRenderable> wrapper = imageCache.get(resourceLocation);
        if (wrapper!=null)
            return wrapper;
        ObjectWrapper<IChatRenderable> image = new ObjectWrapper<>(null);
        imageCache.put(resourceLocation, image);
        new Thread(() -> {
            try
            {
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
                image.object = loadImageInternal(resource.getInputStream());
            }
            catch (IOException e)
            {
                FancyChat.LOGGER.error("Failed to load inbuilt image {}: ", resourceLocation, e);
            }
        }).start();
        return image;
    }

    public static IChatRenderable loadImageNow(ResourceLocation resourceLocation)
    {
        IResource resource;
        try
        {
            resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
            return loadImageInternal(resource.getInputStream());
        }
        catch (IOException e)
        {
            FancyChat.LOGGER.error("Failed to load inbuilt image {}: ", resourceLocation, e);
            return null;
        }
    }

    private static IChatRenderable loadImageInternal(Object obj) throws IOException
    {
        try (ImageInputStream in = ImageIO.createImageInputStream(obj))
        {
            if (in == null)
            {
                throw new IOException("Failed to load image: input stream is null");
            }
            Iterator<ImageReader> it = ImageIO.getImageReaders(in);
            if (it.hasNext())
            {
                ImageReader reader = it.next();
                reader.setInput(in);
                int numImages = reader.getNumImages(true);
                if (numImages > 1)
                {
                    ImageWriter writer = null;
                    ImageOutputStream out = null;
                    int[] frameTime = new int[numImages];
                    int[] offsetX = new int[numImages];
                    int[] offsetY = new int[numImages];
                    BufferedImage[] images = new BufferedImage[numImages];
                    for (int i = 0; i < images.length; i++)
                    {
                        images[i] = reader.read(reader.getMinIndex() + i);
                        IIOMetadata metadata = reader.getImageMetadata(i);
                        String metaFormatName = metadata.getNativeMetadataFormatName();
                        if (metaFormatName == null)
                        {
                            throw new IOException("Failed to load image: meta format name is null");
                        }
                        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                        NodeList childNodes = root.getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++)
                        {
                            if (childNodes.item(j).getNodeName().equalsIgnoreCase("GraphicControlExtension"))
                            {
                                frameTime[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("delayTime")) * 10;
                            }
                            if (childNodes.item(j).getNodeName().equalsIgnoreCase("ImageDescriptor"))
                            {
                                try
                                {
                                    offsetX[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageLeftPosition"));
                                }
                                catch (NumberFormatException ignored)
                                {
                                }
                                try
                                {
                                    offsetY[i] = Integer.parseInt(((IIOMetadataNode) childNodes.item(j)).getAttribute("imageTopPosition"));
                                }
                                catch (NumberFormatException ignored)
                                {
                                }
                            }
                        }
                    }
                    IIOMetadata metadata = reader.getImageMetadata(0);
                    String metaFormatName = metadata.getNativeMetadataFormatName();
                    IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
                    NodeList childNodes = root.getChildNodes();
                    boolean cumulativeRendering = true;
                    for (int i = 0; i < childNodes.getLength(); i++)
                    {
                        if (childNodes.item(i).getNodeName().equalsIgnoreCase("GraphicControlExtension"))
                        {
                            cumulativeRendering = ((IIOMetadataNode) childNodes.item(i)).getAttribute("disposalMethod").equals("doNotDispose");
                            break;
                        }
                    }
                    AnimatedChatRenderable image = new AnimatedChatRenderable(images, frameTime, offsetX, offsetY);
                    image.setCumulativeRendering(cumulativeRendering);
                    return image;
                }
                else
                {
                    BufferedImage buffer = reader.read(0);
                    StaticChatRenderable image = new StaticChatRenderable(buffer);
                    return image;
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throw new IOException(e);
        }
        return null;
    }

    public static Clip loadSoundNow(URL url) throws IOException
    {
        Object sound = soundCache.get(url);
        if (sound!=null)
        {
            if (sound instanceof Clip)
                return (Clip) sound;
            return null;
        }
        try
        {
            //URLConnection con = url.openConnection();
            //con.setConnectTimeout(120000);
            //con.setReadTimeout(120000);
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(url))
            {
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                sound = clip;
            }
        }
        catch (SocketTimeoutException ignored)
        {
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to load sound!", e);
        }
        soundCache.put(url, sound != null ? sound : new Object());
        return (Clip)sound;
    }

    public static Clip loadSoundNow(ResourceLocation resourceLocation) throws IOException
    {
        Object sound = soundCache.get(resourceLocation);
        if (sound!=null)
        {
            if (sound instanceof Clip)
                return (Clip) sound;
            return null;
        }
        try
        {
            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
            if (resource!=null)
            {
                try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource.getInputStream()))
                {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    sound = clip;
                }
            }
        }
        catch (SocketTimeoutException ignored)
        {
        }
        catch (Exception e)
        {
            FancyChat.LOGGER.error("Failed to load inbuilt sound: {}!", resourceLocation, e);
        }
        soundCache.put(resourceLocation, sound);
        return (Clip)sound;
    }
}