package ru.imine.shared.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.imine.shared.core.packet.CPacketUserUpdate;
import ru.imine.shared.core.packet.CPacketWelcome;
import ru.imine.shared.fancychat.item.ItemSmileCase;

public class AiMineCore
{
    public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper("iminecore");
    public static final CreativeTabs TAB = new CreativeTabs("iMine")
    {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemSmileCase.getItems().get(0));
        }
    };

    public static void preInit(FMLPreInitializationEvent e)
    {
        network.registerMessage(CPacketWelcome.Handler.class, CPacketWelcome.class, 0, Side.CLIENT);
        network.registerMessage(CPacketUserUpdate.Handler.class, CPacketUserUpdate.class, 1, Side.CLIENT);
    }

    public static void postInit(FMLPostInitializationEvent e)
    {
        RegistryNamespacedDefaultedByKey<ResourceLocation, Block> test = Block.REGISTRY;
        for (Block b : test)
            EntityEnderman.setCarriable(b, false);
    }
}