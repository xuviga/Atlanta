package ru.imine.shared.economy.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapedOreRecipe;
import ru.imine.shared.economy.CraftingWithMoney;

public class RecipeManager
{
    public static void registerRecipes()
    {
        {
            ResourceLocation name = new ResourceLocation("imine", "workbench_with_money");
            ShapedOreRecipe recipe = new ShapedOreRecipe(name,
                    new ItemStack(ItemBlockWorkbenchWMoney.ITEM),
                    "ggg", "gtg", "ggg",
                    'g', "ingotGold",
                    't', "workbench");
            recipe.setRegistryName(name);
            ForgeRegistries.RECIPES.register(recipe);
            CraftingWithMoney.registerMoneyRecipe(recipe, 50);
        }
        {
            ResourceLocation name = new ResourceLocation("minecraft", "white_shulker_box");

            ShapedOreRecipe recipe = new ShapedOreRecipe(name,
                    new ItemStack(Blocks.WHITE_SHULKER_BOX),
                    " p ", "iti", "igi",
                    'p', "enderpearl", 'g', "ingotGold",
                    'i', "ingotIron", 't', "chestWood");
            recipe.setRegistryName(name);
            ForgeRegistries.RECIPES.register(recipe);
            CraftingWithMoney.registerMoneyRecipe(recipe, 5);
        }
    }
}