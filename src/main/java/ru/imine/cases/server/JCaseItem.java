package ru.imine.cases.server;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;

@GradleSideOnly(GradleSide.SERVER)
public class JCaseItem {
	@SerializedName("stack")
	private ItemStack itemStack;
	@SerializedName("rarity")
	private int rarity;
	@SerializedName("chance")
	private double chance;

	public int getId() {
		return Item.getIdFromItem(itemStack.getItem());
	}

	public int getMeta() {
		return this.itemStack.getMetadata();
	}

	public int getAmount() {
		return this.itemStack.getCount();
	}

	public String getName() {
		return this.itemStack.getDisplayName();
	}

	public int getRarity() {
		return this.rarity;
	}

	public double getChance() {
		return this.chance;
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public String format() {
		return String.format("%s,%s,%s", this.getId(), this.getMeta(), this.getRarity());
	}

	public String winFormat() {
		return String.format("%s,%s,%s,%s", this.getId(), this.getMeta(), this.getAmount(), this.rarity);
	}
}
