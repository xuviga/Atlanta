package ru.imine.shared.economy.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCoin extends Item
{
    public static final ItemCoin ITEM = new ItemCoin();
    public static final ItemStack STACK = new ItemStack(ITEM);

    private ItemCoin()
    {
        super();
        setRegistryName("coin");
        setTranslationKey("coin");
    }
}