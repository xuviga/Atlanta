package ru.imine.shared.core;

import com.alibaba.fastjson.JSONObject;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import ru.imine.shared.AiMine;
import ru.imine.shared.util.Discord;
import ru.imine.shared.util.JsonUtil;

import java.io.File;
import java.io.IOException;

public class ItemConfig
{
    protected static JSONObject config;

    static
    {
        try
        {
            File file = new File("config/iMine/items.cfg");
            if (file.exists())
                config = JsonUtil.fromFile(file.toPath());
            else
                config = new JSONObject();
        }
        catch (IOException e)
        {
            AiMine.LOGGER.error("Failed to read item config", e);
            Discord.instance.sendErrorLog("iMineCore", "Failed to read rules config.", e);
        }
    }

    //TODO photon replace
    public static boolean isEnabled(Item item)
    {
        return isKeyEnabled(item.getRegistryName().getPath());
    }
    //TODO photon replace
    public static boolean isEnabled(Block block)
    {
        return isKeyEnabled(block.getRegistryName().getPath());
    }

    private static boolean isKeyEnabled(String key)
    {
        return !config.containsKey(key) || config.getBoolean(key);
    }
}
