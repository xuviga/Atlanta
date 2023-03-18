package ru.imine.shared.economy;
//TODO NEI
//import codechicken.nei.api.API;
import crafttweaker.CraftTweakerAPI;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.imine.shared.core.ItemConfig;
import ru.imine.shared.economy.block.BlockWorkbenchWMoney;
import ru.imine.shared.economy.gui.GuiHandler;
import ru.imine.shared.economy.item.ItemBlockWorkbenchWMoney;
import ru.imine.shared.economy.item.ItemCoin;
import ru.imine.shared.economy.item.RecipeManager;

public class Economy
{
    public static final Logger LOGGER = LogManager.getLogger("Economy");

    public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("economy");

    public static void preInit(FMLPreInitializationEvent event)
    {
        ForgeRegistries.BLOCKS.register(BlockWorkbenchWMoney.BLOCK);
        ForgeRegistries.ITEMS.register(ItemBlockWorkbenchWMoney.ITEM);
        ForgeRegistries.ITEMS.register(ItemCoin.ITEM);

        CraftTweakerAPI.registerClass(CraftingWithMoney.class);
        //API.hideItem(ItemCoin.STACK);
        GuiHandler.init();
    }

    public static void init(FMLInitializationEvent event)
    {
        RecipeManager.registerRecipes();
    }
}