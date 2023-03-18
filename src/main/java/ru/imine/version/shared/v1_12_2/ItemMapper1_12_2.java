package ru.imine.version.shared.v1_12_2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import ru.imine.version.shared.ItemMapper;

import java.util.List;

public class ItemMapper1_12_2 extends ItemMapper
{
    @Override
    public ItemStack onTake(EntityPlayer player, Slot slot, ItemStack itemStack)
    {
        return slot.onTake(player, itemStack);
    }

    @Override
    public ItemStack fromNBT(NBTTagCompound tag)
    {
        return new ItemStack(tag);
    }

    @Override
    public ItemStack getEmptyStack()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isEmpty(ItemStack stack)
    {
        return stack.isEmpty();
    }

    @Override
    public List<ItemStack> createInventory(int size)
    {
        return NonNullList.withSize(size, ItemMapper.instance().getEmptyStack());
    }
}
