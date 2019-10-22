package io.github.alloffabric.artis;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.impl.SyntaxError;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.cottonmc.staticdata.StaticData;
import io.github.cottonmc.staticdata.StaticDataItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ArtisData {
	public static final Jankson jankson = JanksonFactory.createJankson();

	public static void loadConfig() {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("artis.json5").toFile();
			if (!file.exists()) {
				Artis.logger.warn("[Artis] No config file found! Generating an empty file.");
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file, false);
				out.write("{ }".getBytes());
				out.flush();
				out.close();
				return;
			}
			JsonObject json = jankson.load(file);
			loadEntries("config", json);
		} catch (IOException | SyntaxError e) {
			Artis.logger.error("[Artis] Error loading config: {}", e.getMessage());
		}
	}

	public static void loadData() {
		Set<StaticDataItem> data = StaticData.getAll("artis.json5");
		for (StaticDataItem item : data) {
			try {
				JsonObject json = jankson.load(item.createInputStream());
				loadEntries(item.getIdentifier().toString(), json);
			} catch (IOException | SyntaxError e) {
				Artis.logger.error("[Artis] Error loading static data item {}: {}", item.getIdentifier().toString(), e.getMessage());
			}
		}
	}

	private static void loadEntries(String from, JsonObject json) {
		List<String> keys = new ArrayList<>(json.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			if (Artis.ARTIS_TABLE_TYPES.containsId(new Identifier(key))) {
				Artis.logger.error("[Artis] Table type named {} already exists, skipping it in {}", key, from);
				continue;
			}
			JsonElement elem = json.get(key);
			if (elem instanceof JsonObject) {
				JsonObject config = (JsonObject)elem;
				ArtisTableType type = getType(key, config);
				Artis.registerTable(type);
			}
		}
	}

	//TODO: more options for tables
	static ArtisTableType getType(String key, JsonObject json) {
		Identifier id = new Identifier(key);
		int width = json.get(Integer.class, "width");
		int height = json.get(Integer.class, "height");
		if (json.containsKey("color")) {
			return new ArtisTableType(id, width, height, Integer.decode(json.get(String.class, "color").replace("#", "0x")));
		}
		return new ArtisTableType(id, width, height);
	}

}
