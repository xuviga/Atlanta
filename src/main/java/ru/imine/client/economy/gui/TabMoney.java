package ru.imine.client.economy.gui;

//import codechicken.nei.jei.JEIIntegrationManager;
import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.util.helpers.StringHelper;
import mezz.jei.Internal;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.recipes.RecipeRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import ru.imine.shared.economy.item.ItemCoin;

import java.io.IOException;
import java.util.List;

public class TabMoney extends TabBase
{
    private static TextureAtlasSprite ICON_COIN;

    public static void init(TextureStitchEvent.Pre event)
    {
        ICON_COIN = event.getMap().registerSprite(new ResourceLocation("imine","items/economy/coin"));
    }

    public TabMoney(GuiContainerCore gui)
    {
        this(gui, 0);
    }

    public TabMoney(GuiContainerCore gui, int side)
    {
        super(gui, side);
    }

    public TextureAtlasSprite getIcon()
    {
        return ICON_COIN;
    }

    public String getTitle()
    {
        return StringHelper.localize("jei.coin_tab.info");
    }

    @Override
    protected void drawForeground()
    {
        this.drawTabIcon(this.getIcon());
    }

    @Override
    public void addTooltip(List<String> list)
    {
        if (!this.isFullyOpened())
        {
            list.add(this.getTitle());
        }
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        //todo nei
        RecipeRegistry registry = Internal.getRuntime().getRecipeRegistry();
        IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, ItemCoin.STACK);
        if (!registry.getRecipeCategories(focus).isEmpty()) {
            Internal.getRuntime().getRecipesGui().show(focus);
        }
        return true;
    }
}