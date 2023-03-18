package ru.imine.shared.fancychat.chat.util;

public enum ChatBcgColor
{
    /*SERVER("server", 0x111111),
    RED("red", 0xFF5555),
    DEFAULT("default", 0x000000),
    DARK_BLUE("dark_blue", 0x0000AA),
    DARK_GREEN("dark_green", 0x00AA00),
    DARK_AQUA("dark_aqua", 0x00AAAA),
    DARK_PURPLE("dark_purple", 0xAA00AA),
    GOLD("gold", 0xFFAA00),
    GRAY("gray", 0xAAAAAA),
    DARK_GRAY("dark_gray", 0x555555),
    BLUE("blue", 0x5555FF),
    GREEN("green", 0x55FF55),
    AQUA("aqua", 0x55FFFF),
    LIGHT_PURPLE("purple", 0xFF55FF),
    YELLOW("yellow", 0xFFFF55),
    PINK("pink", 0xFEC1E3),
    BROWN("brown", 0x964B00);*/
    SERVER("server", 0x111111),
    RED("red", 0xFF5555),
    DEFAULT("default", 0x000000),
    DARK_BLUE("dark_blue", 0x000055),
    DARK_GREEN("dark_green", 0x005500),
    DARK_AQUA("dark_aqua", 0x005555),
    DARK_PURPLE("dark_purple", 0x550055),
    GOLD("gold", 0x7F5500),
    GRAY("gray", 0x555555),
    DARK_GRAY("dark_gray", 0x333333),
    BLUE("blue", 0x33337F),
    GREEN("green", 0x337F33),
    AQUA("aqua", 0x337F7F),
    LIGHT_PURPLE("purple", 0x7F337F),
    YELLOW("yellow", 0x7F7F33),
    //PINK("pink", 0x7F6071),
    LIGHT_BLUE("light_blue", 0x21437A),
    PINK("pink", 0xFEC1E3),
    BROWN("brown", 0x4B2500);

    public final String name;
    public final int value;

    ChatBcgColor(String name, int value)
    {
        this.name = name;
        this.value = value;
    }
}
