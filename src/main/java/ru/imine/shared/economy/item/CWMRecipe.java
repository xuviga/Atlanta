package ru.imine.shared.economy.item;

import net.minecraft.item.crafting.IRecipe;

public class CWMRecipe
{
    public IRecipe grid;
    public long cost;

    public CWMRecipe(IRecipe grid, long cost)
    {
        this.grid = grid;
        this.cost=cost;
    }
}
