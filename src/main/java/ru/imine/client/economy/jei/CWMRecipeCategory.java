package ru.imine.client.economy.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import mezz.jei.config.Config;
import mezz.jei.startup.ForgeModIdHelper;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class CWMRecipeCategory implements IRecipeCategory<CWMRecipeWrapper>
{
    public static String NAME = "craft_with_money";
    private final IDrawable background;
    private final ICraftingGridHelper craftingGridHelper;

    public CWMRecipeCategory(IGuiHelper helper)
    {
        this.background = helper.createDrawable(new ResourceLocation("imine", "textures/gui/economy/craft_with_money.png"),
                20, 16, 125, 72, 0, 0, 0, 0);
        craftingGridHelper = helper.createCraftingGridHelper(1, 0);
    }

    @Override
    public String getUid()
    {
        return NAME;
    }

    @Override
    public String getTitle()
    {
        return I18n.format("jei.craft_with_money.title");
    }

    @Override
    public String getModName()
    {
        return "iMine";
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CWMRecipeWrapper recipeWrapper, IIngredients ingredients)
    {
        int craftOutputSlot = 0;
        int craftInputSlot1 = 1;

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(craftOutputSlot, false, 103, 18);

        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 3; ++x)
            {
                int index = craftInputSlot1 + x + (y * 3);
                guiItemStacks.init(index, true, 9 + x * 18, y * 18);
            }
        }

        if (recipeWrapper instanceof ICustomCraftingRecipeWrapper)
        {
            ICustomCraftingRecipeWrapper customWrapper = (ICustomCraftingRecipeWrapper) recipeWrapper;
            customWrapper.setRecipe(recipeLayout, ingredients);
            return;
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

        craftingGridHelper.setInputs(guiItemStacks, inputs);

        guiItemStacks.set(craftOutputSlot, outputs.get(0));

        ResourceLocation registryName = recipeWrapper.getRegistryName();
        if (registryName != null)
        {
            guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
                if (slotIndex == craftOutputSlot)
                {
                    //TODO photon replace
                    String recipeModId = registryName.getPath();

                    boolean modIdDifferent = false;
                    ResourceLocation itemRegistryName = ingredient.getItem().getRegistryName();
                    if (itemRegistryName != null)
                    {
                        //TODO photon replace
                        String itemModId = itemRegistryName.getPath();
                        modIdDifferent = !recipeModId.equals(itemModId);
                    }

                    if (modIdDifferent)
                    {
                        String modNameFormat = Config.getModNameFormat();
                        String modName = modNameFormat + ForgeModIdHelper.getInstance().getModNameForModId(recipeModId);
                        tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.grid.by", modName));
                    }

                    boolean showAdvanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips || GuiScreen.isShiftKeyDown();
                    if (showAdvanced)
                    {
                        //TODO photon replace
                        tooltip.add(TextFormatting.GRAY + registryName.getPath());
                    }
                }
            });
        }
    }
}