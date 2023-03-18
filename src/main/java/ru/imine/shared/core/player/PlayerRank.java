package ru.imine.shared.core.player;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import ru.imine.client.fancychat.smile.SmileDictionary;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.fancychat.smile.Smile;

public enum PlayerRank
{
    DEFAULT("default", "", 0, null, null),
    GOLD("gold", "§6Gold§r", 1, "imine", "gold"),
    PLATINUM("platinum", "§3Platinum§r", 2, "imine", "platinum"),
    DIAMOND("diamond", "§9Diamond§r" ,3, "imine", "diamond"),
    EMERALD("emerald", "§aEmerald§r", 4, "imine", "emerald");


    public final String name;
    public final String displayName;
    public final int level;
    public final Smile icon;

    PlayerRank(String name, String displayName, int level, String iconGroup, String iconName)
    {
        this.name = name;
        this.displayName=displayName;
        this.level = level;
        if (FMLCommonHandler.instance().getSide()== Side.SERVER)
            this.icon = SmileManager.getSmile(iconGroup, iconName);
        else
            this.icon = SmileDictionary.getSmile(iconGroup, iconName);
    }


}
