package ru.imine.client.economy.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import ru.imine.shared.economy.item.CWMRecipe;
import ru.imine.version.client.GuiMapper;

import java.util.List;

public class CWMRecipeWrapper implements ICraftingRecipeWrapper
{
    public final CWMRecipe moneyRecipe;
    private final IJeiHelpers jeiHelpers;

    public CWMRecipeWrapper(IJeiHelpers jeiHelpers, CWMRecipe recipe)
    {
        this.jeiHelpers = jeiHelpers;
        this.moneyRecipe = recipe;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        GuiMapper.instance().getFontRenderer().drawStringWithShadow(Long.toString(moneyRecipe.cost), 16, 60, 14737632);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        ItemStack recipeOutput = moneyRecipe.grid.getRecipeOutput();
        IStackHelper stackHelper = jeiHelpers.getStackHelper();

        try {
            List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(moneyRecipe.grid.getIngredients());
            ingredients.setInputLists(ItemStack.class, inputLists);
            ingredients.setOutput(ItemStack.class, recipeOutput);
        } catch (RuntimeException e) {
            String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(moneyRecipe.grid, moneyRecipe.grid.getIngredients(), recipeOutput);
            throw new BrokenCraftingRecipeException(info, e);
        }
    }
}
