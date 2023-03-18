package ru.imine.shared.core.player;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import ru.imine.client.fancychat.smile.SmileDictionary;
import ru.imine.server.fancychat.smile.SmileManager;
import ru.imine.shared.fancychat.smile.Smile;

public enum PlayerRole
{
    DEFAULT("default", "Игрок", 0, "misc", "radiohazard"),
    LOWMODER("lowmoder", "Мл.Модератор", 1, null, null),
    MOD("mod", "Модератор", 2, null, null),
    STMODER("stmoder", "Ст.Модератор", 3, null, null),
    HEADMODER("headmoder", "Гл. Модератор", 4, null, null),
    COORDINATOR("coordinator", "Координатор", 5, null, null),
    ADMIN("admin","Администратор", 6, "memes", "not_sure_if"),
    DEV("dev","Разработчик", 7, "twitch", "kappapride");

    public final String name;
    public final String displayName;
    public final int level;
    public final Smile icon;

    PlayerRole( String name, String displayName, int level, String iconGroup, String iconName)
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
