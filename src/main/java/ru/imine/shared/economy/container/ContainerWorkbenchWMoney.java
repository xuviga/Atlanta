package ru.imine.shared.economy.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.imine.client.economy.gui.GuiCraftingWMoney;
import ru.imine.server.core.player.AiMinePlayerMP;
import ru.imine.shared.core.AiMineCore;
import ru.imine.shared.core.packet.CPacketUserUpdate;
import ru.imine.shared.economy.CraftingWithMoney;
import ru.imine.shared.economy.Economy;
import ru.imine.shared.economy.block.BlockWorkbenchWMoney;
import ru.imine.shared.economy.item.CWMRecipe;
import ru.imine.shared.util.RobertoGarbagio;
import ru.imine.version.shared.ItemMapper;

import javax.annotation.Nullable;
import java.sql.SQLException;

public class ContainerWorkbenchWMoney extends Container
{
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    public long cost;
    private final World world;
    private final BlockPos pos;

    public ContainerWorkbenchWMoney(InventoryPlayer playerInventory, World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;

        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));

        for (int k = 0; k < 3; ++k)
            for (int i1 = 0; i1 < 9; ++i1)
                this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 113 + k * 18));

        for (int l = 0; l < 9; ++l)
            this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 171));

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        if (player.world.isRemote)
        {
            if (slotId == 0)
            {
                if (GuiCraftingWMoney.isMoneyDeposited)
                {
                    GuiCraftingWMoney.isMoneyDeposited = false;
                    return super.slotClick(0, 0, ClickType.PICKUP, player);
                }
                else
                    return ItemMapper.instance().getEmptyStack();
                //else
                //    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_VILLAGER_TRADING, 1.0F));
            }
        }
        else
        {
            //RobertoGarbagio.LOGGER.info("press "+slotId);
            if (slotId == 0)
            {
                try
                {
                    AiMinePlayerMP iPlayer = AiMinePlayerMP.get(player);
                    if (iPlayer.getMoney() >= cost)
                    {
                        iPlayer.changeMoney( - cost);
                        //RobertoGarbagio.LOGGER.info("here");
                        AiMineCore.network.sendTo(new CPacketUserUpdate(iPlayer.asUser(iPlayer),2), iPlayer.getEntity());
                        return super.slotClick(0, 0, ClickType.PICKUP, player);
                    }
                }
                catch (SQLException e)
                {
                    Economy.LOGGER.error("Сфейлили произвести крафт из монеток", e);
                }
                return ItemMapper.instance().getEmptyStack();
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        CWMRecipe recipe = CraftingWithMoney.findMatchingRecipe(this.craftMatrix, world);
        if (recipe == null)
        {
            this.craftResult.setInventorySlotContents(0, ItemMapper.instance().getEmptyStack());
            cost = 0;
        }
        else
        {
            this.craftResult.setInventorySlotContents(0, recipe.grid.getRecipeOutput().copy());
            cost = recipe.cost;
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote)
        {
            this.clearContainer(playerIn, this.world, this.craftMatrix);
        }
    }


    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return world.getBlockState(pos).getBlock() == BlockWorkbenchWMoney.BLOCK
                && AiMinePlayerMP.get(player)!=null
                && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemMapper.instance().getEmptyStack();
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return ItemMapper.instance().getEmptyStack();
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return ItemMapper.instance().getEmptyStack();
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return ItemMapper.instance().getEmptyStack();
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return ItemMapper.instance().getEmptyStack();
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack(ItemMapper.instance().getEmptyStack());
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return ItemMapper.instance().getEmptyStack();
            }

            ItemMapper.instance().onTake(playerIn,slot,itemstack1);
        }

        return itemstack;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}