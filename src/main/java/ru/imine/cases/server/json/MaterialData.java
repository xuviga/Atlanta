package ru.will0376.cases.server.json;

import com.google.gson.*;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;

import java.lang.reflect.Type;

@GradleSideOnly(GradleSide.SERVER)
public class MaterialData implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		Item item = object.has("item") ? Item.getByNameOrId(object.get("item").getAsString()) : Items.AIR;
		int meta = object.has("meta") ? object.get("meta").getAsInt() : 0;
		if (item == null || item == Items.AIR)
			System.err.println("Check config! Item not found");
		return new ItemStack(item, 1, meta);
	}

	@Override
	public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject object = new JsonObject();
		object.add("item", context.serialize(src.getItem().getRegistryName()));
		object.addProperty("meta", src.getMetadata());
		return object;
	}
}
