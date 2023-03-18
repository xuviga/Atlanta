package ru.will0376.cases.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.will0376.cases.server.json.Enchants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@GradleSideOnly(GradleSide.SERVER)
public class ItemStackBuilder {
	private final Item item;
	private final int data;
	private int amount;
	private String displayName;
	private ArrayList<String> lore = new ArrayList<>();
	private Map<Enchantment, Integer> enchants = new HashMap<>();

	public ItemStackBuilder(Item item, int data) {
		this.amount = 1;
		this.item = item;
		this.data = data;
	}

	public ItemStackBuilder(ItemStack stack) {
		this(stack.getItem(), stack.getMetadata());
	}

	public ItemStackBuilder withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemStackBuilder withLore(String... lore) {
		return withLore(Lists.newArrayList(lore));
	}

	public ItemStackBuilder withLore(ArrayList<String> lore) {
		this.lore = lore;
		return this;
	}

	public ItemStackBuilder withEnchant(int id, int level) {
		return withEnchant(Enchantment.getEnchantmentByID(id), level);
	}

	public ItemStackBuilder withEnchant(Enchantment enchant, int level) {
		if (this.enchants == null) {
			this.enchants = Maps.newHashMap();
		}

		this.enchants.put(enchant, level);
		return this;
	}

	public ItemStackBuilder withEnchants(Map<Enchantment, Integer> enchants) {
		this.enchants = enchants;
		return this;
	}

	public ItemStackBuilder withEnchant(ArrayList<Enchants> enchants) {
		if (enchants != null && !enchants.isEmpty()) {
			enchants.forEach(e -> withEnchant(e.getId(), e.getLvl()));
		}
		return this;
	}
	/*public ItemStackBuilder withRawIdEnchants(Map<Enchantment, Integer> enchants) {
		if (enchants == null) {
			return this;
		}
		Map<Enchantment, Integer> map = Maps.newHashMap();

		for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchants.entrySet()) {
			map.put(Enchantment.getEnchantmentByID(enchantmentIntegerEntry.getValue()), (enchantmentIntegerEntry).getValue());
		}

		return withEnchants(map);
	}

	public ItemStackBuilder withRawNameEnchants(Map<Enchantment, Integer> enchants) {
		if (enchants == null) {
			return this;
		}
		Map<Enchantment, Integer> map = Maps.newHashMap();

		for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : enchants.entrySet()) {
			map.put(Enchantment.getEnchantmentByLocation(enchantmentIntegerEntry.getKey().getName()), enchantmentIntegerEntry.getValue());
		}

		return withEnchants(map);
	}*/

	public ItemStackBuilder withName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public ItemStack build() {
		ItemStack itemStack = new ItemStack(item, amount, data);

		if (!enchants.isEmpty())
			enchants.forEach(itemStack::addEnchantment);

		if (lore != null && !lore.isEmpty()) {
			NBTTagCompound nbt = itemStack.getTagCompound();
			if (nbt == null) nbt = new NBTTagCompound();

			NBTTagList lore = new NBTTagList();
			this.lore.forEach(e -> lore.appendTag(new NBTTagString(e)));
			NBTTagCompound display = new NBTTagCompound();
			display.setTag("Lore", lore);

			nbt.setTag("display", display);
			itemStack.setTagCompound(nbt);
		}
		if (displayName != null)
			itemStack.setStackDisplayName(displayName);

		return itemStack;
	}

}
