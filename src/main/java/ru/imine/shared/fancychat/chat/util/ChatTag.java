package ru.imine.shared.fancychat.chat.util;

public enum ChatTag
{
    GENERAL("general", 0xAAAAAA),
    SERVER("server", 0x3fbf6d),
    LOCAL("local", 0x58a9aa),
    TRADE("trade", 0xffed87),
    ADMIN("admin", 0xff0000),
    UNKNOWN("unknown", 0xd11dd1);

    public final String name;
    public final int color;

    ChatTag(String name, int color)
    {
        this.name = name;
        this.color = color;
    }
}
