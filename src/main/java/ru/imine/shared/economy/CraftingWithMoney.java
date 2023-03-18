package ru.imine.shared.economy;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.recipes.ShapedRecipe;
import crafttweaker.mc1120.recipes.RecipeConverter;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import ru.imine.shared.economy.item.CWMRecipe;
import ru.imine.shared.util.RobertoGarbagio;
import ru.imine.shared.util.annotations.KeepName;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ZenClass("ru.imine.CraftingWithMoney")
public class CraftingWithMoney
{
    private static List<CWMRecipe> recipes = new ArrayList<>();

    public static void registerMoneyRecipe(IRecipe recipe, long cost)
    {
        registerMoneyRecipe(new CWMRecipe(recipe, cost));
    }

    public static void registerMoneyRecipe(CWMRecipe recipe)
    {
        recipes.add(recipe);
    }

    public static List<CWMRecipe> getRecipes()
    {
        return new ArrayList<>(recipes);
    }

    public static CWMRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World world)
    {
        //RobertoGarbagio.LOGGER.info("finding recipes: "+recipes);
        return recipes.stream().filter(it->it.grid.matches(craftMatrix,world)).findFirst().orElse(null);
    }

    @ZenMethod
    @KeepName
    public static void addShapeless(IItemStack output, long money, IIngredient[] ingredients)
    {
        CraftTweakerAPI.apply(new Add(new CWMRecipe(new ShapelessOreRecipe(
                new ResourceLocation("cwm_shapeless"+ UUID.randomUUID()), toStack(output), toObjects(ingredients)), money)));
    }

    @ZenMethod
    @KeepName
    public static void addShaped(IItemStack output, long money, IIngredient[][] ingredients)
    {
        ShapedRecipe recipe = new ShapedRecipe("cwm_shaped"+UUID.randomUUID(), output, ingredients, null,null,false);
        IRecipe irecipe = RecipeConverter.convert(recipe, new ResourceLocation("imine","cwm_shaped"+UUID.randomUUID()));
        //IRecipe irecipe = new ShapedOreRecipe(new ResourceLocation("imine","cwm_shaped"+UUID.randomUUID()), toStack(output), toObjects(ingredients))
        CraftTweakerAPI.apply(new Add(new CWMRecipe(irecipe, money)));
    }

    private static class Add implements IAction
    {
        CWMRecipe recipe;

        public Add(CWMRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void apply()
        {
            CraftingWithMoney.registerMoneyRecipe(recipe);
        }

        @Override
        public String describe()
        {
            return "Adding Crafting With Money for " + recipe.grid.getRecipeOutput();
        }
    }

    private static ItemStack toStack(IItemStack item)
    {
        if (item == null)
        {
            return null;
        }
        Object internal = item.getInternal();
        if ((internal == null) || (!(internal instanceof ItemStack)))
        {
            Economy.LOGGER.error("Not a valid item stack: " + item);
        }
        return (ItemStack) internal;
    }

    private static Object toObject(IIngredient ingredient)
    {
        if (ingredient == null)
        {
            return null;
        }
        if ((ingredient instanceof IOreDictEntry))
            return toString((IOreDictEntry) ingredient);
        if ((ingredient instanceof IItemStack))
        {
            return toStack((IItemStack) ingredient);
        }
        return null;
    }


    private static Object[] toObjects(IIngredient[] list)
    {
        if (list == null)
        {
            return null;
        }
        Object[] ingredients = new Object[list.length];
        for (int x = 0; x < list.length; x++)
        {
            ingredients[x] = toObject(list[x]);
        }
        return ingredients;
    }

    private static Object toActualObject(IIngredient ingredient)
    {
        if (ingredient == null)
        {
            return null;
        }
        if ((ingredient instanceof IOreDictEntry))
            return net.minecraftforge.oredict.OreDictionary.getOres(toString((IOreDictEntry) ingredient));
        if ((ingredient instanceof IItemStack))
        {
            return toStack((IItemStack) ingredient);
        }
        return null;
    }


    private static String toString(IOreDictEntry entry)
    {
        return entry.getName();
    }
}
