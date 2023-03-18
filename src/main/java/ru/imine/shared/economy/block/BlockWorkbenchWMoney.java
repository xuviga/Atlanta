package ru.imine.shared.economy.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.shared.core.AiMineCore;
import ru.imine.shared.core.AiMineGui;

import javax.annotation.Nullable;
import java.util.List;

public class BlockWorkbenchWMoney extends Block
{
    public final static BlockWorkbenchWMoney BLOCK = new BlockWorkbenchWMoney();

    protected BlockWorkbenchWMoney()
    {
        super(Material.IRON, MapColor.GOLD);
        setRegistryName("workbench_with_money");
        setTranslationKey("workbench_with_money");
        setCreativeTab(AiMineCore.TAB);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> info, boolean wut)
    {
        addInformation(itemStack, player.world, info, ITooltipFlag.TooltipFlags.NORMAL);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(I18n.format("tile.workbench_with_money.desc"));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            AiMineGui.openGui(playerIn, AiMineGui.GuiType.CRAFT_WITH_MONEY, worldIn, pos);
            playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION);
            return true;
        }
    }
}