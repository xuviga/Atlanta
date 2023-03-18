package ru.imine.shared.util;

import net.minecraft.util.text.TextFormatting;

public enum Rarity implements Comparable<Rarity>
{
    NORMAL("normal", "Обычный", TextFormatting.DARK_AQUA, 0x00AAAA , 1),
    RARE("rare", "Редкий", TextFormatting.BLUE, 0x5555FF, 2),
    EPIC("epic", "Эпичный", TextFormatting.DARK_PURPLE, 0xAA00AA, 3),
    LEGEND("legend", "Легендарный", TextFormatting.GOLD, 0xFFAA00, 4),
    SPECIAL("special", "Особый", TextFormatting.GRAY, 0xAAAAAA, 10);

    public final String name;
    public final String displayName;
    public final TextFormatting textFormatting;
    public final int color;
    public final int value;

    Rarity(String name, String displayName, TextFormatting textFormatting, int color, int value)
    {
        this.textFormatting = textFormatting;
        this.color = color;
        this.displayName = displayName;
        this.value = value;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public static Rarity getByValue(int value)
    {
        for (Rarity rarity : values())
        {
            if (rarity.value==value)
                return rarity;
        }
        return null;
    }

    @Override
    public String toString()
    {
        return this.textFormatting.toString();
    }

    public String getDisplayName()
    {
        return this.textFormatting.toString() + displayName + TextFormatting.RESET;
    }
}