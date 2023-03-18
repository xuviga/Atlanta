package ru.imine.version.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ru.imine.version.shared.v1_12_2.ItemMapper1_12_2;

import java.util.List;

public abstract class ItemMapper
{
    protected static ItemMapper instance;

    public static ItemMapper instance()
    {
        if (instance==null)
            instance = new ItemMapper1_12_2();
        return instance;
    }

    public abstract ItemStack onTake(EntityPlayer player, Slot slot, ItemStack itemStack);
    public abstract ItemStack fromNBT(NBTTagCompound tag);
    public abstract ItemStack getEmptyStack();
    public abstract boolean isEmpty(ItemStack stack);
    public abstract List<ItemStack> createInventory(int size);
}
