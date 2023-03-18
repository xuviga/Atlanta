package ru.imine.shared.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.imine.shared.AiMine;

public class AiMineGui
{
    public static void openGui(EntityPlayer player, GuiType guiType, World world, BlockPos pos)
    {
        openGui(player,guiType,world,pos.getX(),pos.getY(),pos.getZ());
    }

    public static void openGui(EntityPlayer player, GuiType guiType, World world, int x, int y, int z)
    {
        player.openGui(AiMine.instance,guiType.ordinal(),world,x,y,z);
    }

    public enum GuiType
    {
        TRADE_BOOTH_STORAGE, TRADE_BOOTH_OWNER, TRADE_BOOTH_CUSTOMER,
        CRAFT_WITH_MONEY
    }
}
