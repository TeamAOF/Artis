package io.github.alloffabric.artis;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.util.BlockSettingsParser;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.cottonmc.staticdata.StaticData;
import io.github.cottonmc.staticdata.StaticDataItem;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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
			loadEntries("config", json.containsKey("tables")? json.getObject("tables") : json);
		} catch (IOException | SyntaxError e) {
			Artis.logger.error("[Artis] Error loading config: {}", e.getMessage());
		}
	}

	public static void loadData() {
		Set<StaticDataItem> data = StaticData.getAll("artis.json5");
		for (StaticDataItem item : data) {
			try {
				JsonObject json = jankson.load(item.createInputStream());
				loadEntries(item.getIdentifier().toString(), json.containsKey("tables")? json.getObject("tables") : json);
			} catch (IOException | SyntaxError e) {
				Artis.logger.error("[Artis] Error loading static data item {}: {}", item.getIdentifier().toString(), e.getMessage());
			}
		}
	}

	private static void loadEntries(String from, JsonObject json) {
		List<String> keys = new ArrayList<>(json.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			if (Artis.ARTIS_TABLE_TYPES.hasId(new Identifier(key))) {
				Artis.logger.error("[Artis] Table type named {} already exists, skipping it in {}", key, from);
				continue;
			}
			JsonElement elem = json.get(key);
			if (elem instanceof JsonObject) {
				JsonObject config = (JsonObject)elem;
				ArtisTableType type = getType(key, config);
				Block.Settings settings;
				if (config.containsKey("settings")) {
					settings = BlockSettingsParser.parseSettings(config.getObject("settings"));
				} else {
					settings = FabricBlockSettings.copy(Blocks.CRAFTING_TABLE).build();
				}
				Artis.registerTable(type, settings);
			}
		}
	}

	//TODO: more options for tables - multiple catalyst slots, fluids, etc?
	static ArtisTableType getType(String key, JsonObject json) {
        Identifier id = new Identifier(key);
        String tableType = json.containsKey("type") ? json.get(String.class, "type"): "normal";
        int width = json.getInt("width", 3);
        int height = json.getInt("height", 3);
        if (width > 9) {
            Artis.logger.error("[Artis] Table type named {} has too many columns, clamping it to 9", key);
            width = 9;
        }
        if (height > 9) {
            Artis.logger.error("[Artis] Table type named {} has too many rows, clamping it to 9", key);
            height = 9;
        }
        boolean catalystSlot = json.getInt("catalyst_slot", 0) != 0;
        boolean includeNormalRecipes = json.getBoolean("normal_recipes", false);
        boolean genAssets = json.getBoolean("generate_assets", false);
        if (tableType.equals("existing_block")) {
            if (Registry.BLOCK.containsId(id) || json.getBoolean("bypass_check", false)) {
                if (json.containsKey("color")) {
                    return new ArtisExistingBlockType(id, width, height, catalystSlot, includeNormalRecipes, genAssets, Integer.decode(json.get(String.class, "color").replace("#", "0x")));
                }
                return new ArtisExistingBlockType(id, width, height, catalystSlot, includeNormalRecipes, genAssets);
            } else {
                Artis.logger.error("[Artis] Table type named {} could not find the block specified. Are you sure it exists? If it definitely exists, try setting bypass_check to true.", key);
            }
        } else if (tableType.equals("existing_item")) {
            if (Registry.ITEM.containsId(id) || json.getBoolean("bypass_check", false)) {
                if (json.containsKey("color")) {
                    return new ArtisExistingItemType(id, width, height, catalystSlot, includeNormalRecipes, genAssets, Integer.decode(json.get(String.class, "color").replace("#", "0x")));
                }
                return new ArtisExistingItemType(id, width, height, catalystSlot, includeNormalRecipes, genAssets);
            } else {
                Artis.logger.error("[Artis] Table type named {} could not find the item specified. Are you sure it exists? If it definitely exists, try setting bypass_check to true.", key);
            }
        }
        if (json.containsKey("color")) {
            return new ArtisTableType(id, width, height, catalystSlot, includeNormalRecipes, genAssets, Integer.decode(json.get(String.class, "color").replace("#", "0x")));
        }
        return new ArtisTableType(id, width, height, catalystSlot, includeNormalRecipes, genAssets);
	}

}
