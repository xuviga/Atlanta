package ru.will0376.cases.server.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.item.ItemStack;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.will0376.cases.server.ItemStackBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@GradleSideOnly(GradleSide.SERVER)
public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
	@Override
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		ItemStack material = object.has("material") ? (ItemStack) context.deserialize(object.get("material"), MaterialData.class) : null;
		int amount = object.has("amount") ? object.getAsJsonPrimitive("amount").getAsInt() : 1;
		String displayName = object.has("displayName") ? object.getAsJsonPrimitive("displayName").getAsString() : null;
		ArrayList<Enchants> enchants = object.has("enchants") ? new Gson().fromJson(object.get("enchants"),new TypeToken<List<Enchants>>() {}.getType()) : new ArrayList<>();
		ArrayList<String> lore = object.has("lore") ? convert(object.get("lore").getAsJsonArray()) : new ArrayList<>();
		if (displayName != null)
			displayName = displayName.replace('&', '\u00a7');

		return new ItemStackBuilder(material).withAmount(amount).withName(displayName).withEnchant(enchants).withLore(lore).build();

	}

	@Override
	public JsonElement serialize(ItemStack itemStack, Type typeOfSrc, JsonSerializationContext context) {
		final JsonObject object = new JsonObject();
		object.add("material", context.serialize(itemStack));
		object.addProperty("amount", itemStack.getCount());
		object.addProperty("displayName", itemStack.getDisplayName());
		return object;
	}
	private ArrayList<String> convert(JsonArray ja) {
		ArrayList<String> listdata = new ArrayList<>();
		if (ja != null) {
			for (int i=0;i<ja.size();i++){
				listdata.add(ja.get(i).getAsString());
			}
		}
		return listdata;
	}
}
