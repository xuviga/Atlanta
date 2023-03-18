package ru.imine.cases.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import ru.justagod.cutter.GradleSide;
import ru.justagod.cutter.GradleSideOnly;
import ru.imine.cases.server.json.ItemStackAdapter;
import ru.imine.cases.server.json.MaterialData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GradleSideOnly(GradleSide.SERVER)
public class JCases {
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(MaterialData.class, new MaterialData())
			.registerTypeAdapter(ItemStack.class, new ItemStackAdapter()).setPrettyPrinting().create();
	private static final Type CASES_TYPE = new TypeToken<List<JCase>>() {
	}.getType();
	private static final Type PLAYERS_TYPE = new TypeToken<Map<String, Map<String, Integer>>>() {
	}.getType();
	public static Map<String, Map<String, Integer>> playersCases = new HashMap<>();
	public static Map<String, JCase> caseMap = new HashMap<>();
	public static File confPath = null;
	public static String Default = "[\n" +
			"  {\n" +
			"    \"name\": \"Name\",\n" +
			"    \"texture\": \"http://localhost/case.png\",\n" +
			"    \"items\": [\n" +
			"      {\n" +
			"        \"stack\": {\n" +
			"          \"material\": {\n" +
			"            \"item\": \"minecraft:stone\",\n" +
			"            \"meta\": 0\n" +
			"          },\n" +
			"          \"amount\": 1,\n" +
			"          \"displayName\": \"&bИмя\",\n" +
			"          \"lore\": [\n" +
			"            \"Строка 1\",\n" +
			"            \"Строка 2\",\n" +
			"            \"Строка 3\"\n" +
			"          ],\n" +
			"          \"enchants\": {\n" +
			"            \"1\": 1,\n" +
			"            \"2\": 1\n" +
			"          }\n" +
			"        },\n" +
			"        \"rarity\": 1,\n" +
			"        \"chance\": 0.64\n" +
			"      }\n" +
			"    ]\n" +
			"  }\n" +
			"]";

	public static int getAvailableCases(final EntityPlayerMP player, final String caseName) {
		String playerName = player.getName();
		if (playersCases.containsKey(playerName)) {
			final Map<String, Integer> caseMap = playersCases.get(playerName);
			if (caseMap.containsKey(caseName)) {
				return caseMap.get(caseName);
			}
		}
		return 0;
	}

	public static void decrementCases(EntityPlayerMP player, String caseName, int value) {
		decrementCases(player.getName(), caseName, value);
	}

	public static void decrementCases(String player, String caseName, int value) {
		if (playersCases.containsKey(player)) {
			Map<String, Integer> caseMap = playersCases.get(player);
			if (caseMap.containsKey(caseName)) {
				int oldValue = caseMap.remove(caseName);
				int newValue = oldValue - value;
				if (newValue > 0) {
					caseMap.put(caseName, newValue);
				} else if (caseMap.isEmpty()) {
					playersCases.remove(player);
				}
			}
		}
	}

	public static void savePlayers() {
		try (FileWriter writer = new FileWriter(new File(confPath.getAbsolutePath(), "players.json"))) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(playersCases, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void incrementCases(String player, String caseName, int value) {
		Map<String, Integer> caseMap;
		if (!playersCases.containsKey(player)) {
			playersCases.put(player, caseMap = Maps.newHashMap());
		} else {
			caseMap = playersCases.get(player);
		}

		if (caseMap.containsKey(caseName)) {
			caseMap.put(caseName, caseMap.remove(caseName) + value);
		} else {
			caseMap.put(caseName, value);
		}
	}

	public static void initCases() {
		File casesFile = new File(confPath, "cases.json");
		if (!casesFile.exists()) {
			try {
				casesFile.createNewFile();
				FileWriter writer = new FileWriter(casesFile);
				writer.write(Default);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			List<JCase> cases = JCases.GSON.fromJson(Files.toString(casesFile, Charsets.UTF_8), JCases.CASES_TYPE);
			caseMap = Maps.uniqueIndex(cases, JCase::getName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void initPlayers() {
		final File playersFile = new File(confPath, "players.json");
		if (!playersFile.exists()) {
			try {
				playersFile.createNewFile();
				FileWriter writer = new FileWriter(playersFile);
				writer.write("{}");
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			playersCases = JCases.GSON.fromJson(Files.toString(playersFile, Charsets.UTF_8), JCases.PLAYERS_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
