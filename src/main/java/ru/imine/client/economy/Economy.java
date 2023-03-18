package ru.imine.client.economy;
//TODO NEI
//import codechicken.nei.api.API;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.imine.client.economy.gui.TabMoney;
import ru.imine.client.economy.jei.CWMRecipeCategory;
import ru.imine.client.economy.jei.CWMRecipeWrapper;
import ru.imine.shared.economy.CraftingWithMoney;
import ru.imine.shared.economy.container.ContainerWorkbenchWMoney;
import ru.imine.shared.economy.item.CWMRecipe;
import ru.imine.shared.economy.item.ItemBlockWorkbenchWMoney;
import ru.imine.shared.economy.item.ItemCoin;

import java.util.Collections;
import java.util.Objects;

@JEIPlugin
public class Economy implements IModPlugin
{
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(Economy.class);
    }

    public static void init(FMLInitializationEvent event)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemBlockWorkbenchWMoney.ITEM, 0,
                new ModelResourceLocation(Objects.requireNonNull(ItemBlockWorkbenchWMoney.ITEM.getRegistryName()), "inventory"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemCoin.ITEM, 0,
                new ModelResourceLocation(Objects.requireNonNull(ItemCoin.ITEM.getRegistryName()), "inventory"));
    }

    @SubscribeEvent
    public static void handleTextureStitchPreEvent(TextureStitchEvent.Pre event)
    {
        TabMoney.init(event);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        CWMRecipeCategory category = new CWMRecipeCategory(registry.getJeiHelpers().getGuiHelper());
        registry.addRecipeCategories(category);
    }

    @Override
    public void register(IModRegistry registry)
    {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();

        //Инфа про монетки
        //TODO NEI
        //API.hideItem(ItemCoin.STACK);
        registry.addIngredientInfo(Collections.singletonList(ItemCoin.STACK),
                ItemStack.class,
                "item.coin.info");

        //Инфа про крафтостолик
        registry.handleRecipes(CWMRecipe.class, it -> new CWMRecipeWrapper(jeiHelpers, it), CWMRecipeCategory.NAME);
        registry.addRecipes(CraftingWithMoney.getRecipes(), CWMRecipeCategory.NAME);
        transferRegistry.addRecipeTransferHandler(ContainerWorkbenchWMoney.class, CWMRecipeCategory.NAME, 1, 9, 10, 36);
        registry.addRecipeCatalyst(new ItemStack(ItemBlockWorkbenchWMoney.ITEM), CWMRecipeCategory.NAME);
    }
}