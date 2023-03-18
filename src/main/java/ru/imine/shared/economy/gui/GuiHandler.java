package ru.imine.shared.economy.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import ru.imine.client.economy.gui.GuiCraftingWMoney;
import ru.imine.shared.GuiMetaHandler;
import ru.imine.shared.economy.block.BlockWorkbenchWMoney;
import ru.imine.shared.economy.container.ContainerWorkbenchWMoney;
import ru.imine.shared.util.annotations.KeepName;

public class GuiHandler implements IGuiHandler
{
    public static void init()
    {
        GuiMetaHandler.addGuiHandler(new GuiHandler());
    }

    private GuiHandler()
    {
    }

    @Override
    @KeepName
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == BlockWorkbenchWMoney.BLOCK)
            return new ContainerWorkbenchWMoney(player.inventory,world,new BlockPos(x, y, z));
        return null;
    }

    @Override
    @KeepName
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == BlockWorkbenchWMoney.BLOCK)
            return new GuiCraftingWMoney(player.inventory, world, new BlockPos(x,y,z));
        return null;
    }
}

