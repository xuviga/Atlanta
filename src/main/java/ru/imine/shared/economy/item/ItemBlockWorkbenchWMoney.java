package ru.imine.shared.economy.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import ru.imine.shared.core.AiMineCore;
import ru.imine.shared.economy.block.BlockWorkbenchWMoney;

public class ItemBlockWorkbenchWMoney extends ItemBlock
{
    public static final ItemBlockWorkbenchWMoney ITEM = new ItemBlockWorkbenchWMoney(BlockWorkbenchWMoney.BLOCK);

    private ItemBlockWorkbenchWMoney(Block block)
    {
        super(block);
        setRegistryName("workbench_with_money");
        setTranslationKey("workbench_with_money");
        setCreativeTab(AiMineCore.TAB);
    }
}