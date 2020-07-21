package io.github.alloffabric.artis.util;

import blue.endless.jankson.JsonObject;
import io.github.alloffabric.artis.Artis;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class BlockSettingsParser {
	public static final Map<String, Material> MATERIALS = new HashMap<>();
	public static final Map<String, MaterialColor> MATERIAL_COLORS = new HashMap<>();
	public static final Map<String, BlockSoundGroup> SOUND_GROUPS = new HashMap<>();

	public static Block.Settings parseSettings(JsonObject json) {
		if (json == null) {
			Artis.logger.error("[Artis] Cannot parse block settings that aren't a json object!");
			return FabricBlockSettings.copy(Blocks.CRAFTING_TABLE).build();
		}
		FabricBlockSettings settings;
		if (json.containsKey("copy")) {
			String copyTarg = json.get(String.class, "copy");
			settings = FabricBlockSettings.copy(Registry.BLOCK.get(new Identifier(copyTarg)));
		} else if (json.containsKey("material")) {
			String matTarg = json.get(String.class, "material");
			settings = FabricBlockSettings.of(MATERIALS.get(matTarg));
		} else {
			settings = FabricBlockSettings.copy(Blocks.CRAFTING_TABLE);
		}

		if (json.containsKey("break_by_hand")) {
			settings.breakByHand(json.getBoolean("break_by_hand", false));
		}

		if (json.containsKey("break_by_tool")) {
			JsonObject tool = json.getObject("break_by_tool");
			String tag = tool.get(String.class, "tool");
			if (tool.containsKey("level")) {
				int level = tool.getInt("level", 0);
				settings.breakByTool(ItemTags.getContainer().get(new Identifier(tag)), level);
			} else {
				settings.breakByTool(ItemTags.getContainer().get(new Identifier(tag)));
			}
		}

		if (json.containsKey("material_color")) {
			settings.materialColor(MATERIAL_COLORS.get(json.get(String.class, "material_color")));
		}

		if (json.containsKey("collidable")) {
			settings.collidable(json.getBoolean("collidable", true));
		}

		if (json.containsKey("non_opaque")) {
			settings.nonOpaque();
		}

		if (json.containsKey("sounds")) {
			String sounds = json.get(String.class, "sounds");
			settings.sounds(SOUND_GROUPS.get(sounds));
		}

		if (json.containsKey("light_level")) {
			settings.lightLevel(json.getInt("light_level", 0));
		}

		if (json.containsKey("hardness")) {
			settings.hardness(json.getFloat("hardness", 0));
		}

		if (json.containsKey("resistance")) {
			settings.resistance(json.getFloat("resistance", 0));
		}

		if (json.containsKey("slipperiness")) {
			settings.slipperiness(json.getFloat("slipperiness", 0));
		}

		if (json.containsKey("break_instantly")) {
			settings.breakInstantly();
		}

		if (json.containsKey("drops_nothing")) {
			settings.dropsNothing();
		} else if (json.containsKey("drops_like")) {
			settings.dropsLike(Registry.BLOCK.get(new Identifier(json.get(String.class, "drops_like"))));
		} else if (json.containsKey("drops")) {
			settings.drops(new Identifier(json.get(String.class, "drops")));
		}

		if (json.containsKey("dynamic_bounds")) {
			settings.dynamicBounds();
		}

		return settings.build();
	}

	static {
		MATERIALS.put("air", Material.AIR);
		MATERIALS.put("structure_void", Material.STRUCTURE_VOID);
		MATERIALS.put("portal", Material.PORTAL);
		MATERIALS.put("carpet", Material.CARPET);
		MATERIALS.put("plant", Material.PLANT);
		MATERIALS.put("underwater_plant", Material.UNDERWATER_PLANT);
		MATERIALS.put("replaceable_plant", Material.REPLACEABLE_PLANT);
		MATERIALS.put("replaceable_underwater_plant", Material.REPLACEABLE_UNDERWATER_PLANT);
		MATERIALS.put("water", Material.WATER);
		MATERIALS.put("bubble_column", Material.BUBBLE_COLUMN);
		MATERIALS.put("lava", Material.LAVA);
		MATERIALS.put("snow_block", Material.SNOW_BLOCK);
		MATERIALS.put("snow_layer", Material.SNOW_LAYER);
		MATERIALS.put("fire", Material.FIRE);
//		MATERIALS.put("part", Material.);
		MATERIALS.put("cobweb", Material.COBWEB);
		MATERIALS.put("redstone_lamp", Material.REDSTONE_LAMP);
		MATERIALS.put("organic_product", Material.ORGANIC_PRODUCT);
		MATERIALS.put("soil", Material.SOIL);
		MATERIALS.put("solid_organic", Material.SOLID_ORGANIC);
		MATERIALS.put("dense_ice", Material.DENSE_ICE);
		MATERIALS.put("aggregate", Material.AGGREGATE);
		MATERIALS.put("sponge", Material.SPONGE);
		MATERIALS.put("shulker_box", Material.SHULKER_BOX);
		MATERIALS.put("wood", Material.WOOD);
		MATERIALS.put("bamboo_sapling", Material.BAMBOO_SAPLING);
		MATERIALS.put("bamboo", Material.BAMBOO);
		MATERIALS.put("wool", Material.WOOL);
		MATERIALS.put("tnt", Material.TNT);
		MATERIALS.put("leaves", Material.LEAVES);
		MATERIALS.put("glass", Material.GLASS);
		MATERIALS.put("ice", Material.ICE);
		MATERIALS.put("cactus", Material.CACTUS);
		MATERIALS.put("stone", Material.STONE);
		MATERIALS.put("metal", Material.METAL);
		MATERIALS.put("snow_block", Material.SNOW_BLOCK);
		MATERIALS.put("repair_station", Material.REPAIR_STATION);
		MATERIALS.put("barrier", Material.BARRIER);
		MATERIALS.put("piston", Material.PISTON);
		MATERIALS.put("unused_plant", Material.UNUSED_PLANT);
		MATERIALS.put("gourd", Material.GOURD);
		MATERIALS.put("egg", Material.EGG);
		MATERIALS.put("cake", Material.CAKE);

		MATERIAL_COLORS.put("air", MaterialColor.CLEAR);
		MATERIAL_COLORS.put("grass", MaterialColor.GRASS);
		MATERIAL_COLORS.put("sand", MaterialColor.SAND);
		MATERIAL_COLORS.put("web", MaterialColor.WEB);
		MATERIAL_COLORS.put("lava", MaterialColor.LAVA);
		MATERIAL_COLORS.put("ice", MaterialColor.ICE);
		MATERIAL_COLORS.put("iron", MaterialColor.IRON);
		MATERIAL_COLORS.put("foliage", MaterialColor.FOLIAGE);
		MATERIAL_COLORS.put("white", MaterialColor.WHITE);
		MATERIAL_COLORS.put("clay", MaterialColor.CLAY);
		MATERIAL_COLORS.put("dirt", MaterialColor.DIRT);
		MATERIAL_COLORS.put("stone", MaterialColor.STONE);
		MATERIAL_COLORS.put("water", MaterialColor.WATER);
		MATERIAL_COLORS.put("wood", MaterialColor.WOOD);
		MATERIAL_COLORS.put("quartz", MaterialColor.QUARTZ);
		MATERIAL_COLORS.put("orange", MaterialColor.ORANGE);
		MATERIAL_COLORS.put("magenta", MaterialColor.MAGENTA);
		MATERIAL_COLORS.put("light_blue", MaterialColor.LIGHT_BLUE);
		MATERIAL_COLORS.put("yellow", MaterialColor.YELLOW);
		MATERIAL_COLORS.put("lime", MaterialColor.LIME);
		MATERIAL_COLORS.put("pink", MaterialColor.PINK);
		MATERIAL_COLORS.put("gray", MaterialColor.GRAY);
		MATERIAL_COLORS.put("light_gray", MaterialColor.LIGHT_GRAY);
		MATERIAL_COLORS.put("cyan", MaterialColor.CYAN);
		MATERIAL_COLORS.put("purple", MaterialColor.PURPLE);
		MATERIAL_COLORS.put("blue", MaterialColor.BLUE);
		MATERIAL_COLORS.put("brown", MaterialColor.BROWN);
		MATERIAL_COLORS.put("green", MaterialColor.GREEN);
		MATERIAL_COLORS.put("red", MaterialColor.RED);
		MATERIAL_COLORS.put("black", MaterialColor.BLACK);
		MATERIAL_COLORS.put("gold", MaterialColor.GOLD);
		MATERIAL_COLORS.put("diamond", MaterialColor.DIAMOND);
		MATERIAL_COLORS.put("lapis", MaterialColor.LAPIS);
		MATERIAL_COLORS.put("emerald", MaterialColor.EMERALD);
		MATERIAL_COLORS.put("spruce", MaterialColor.SPRUCE);
		MATERIAL_COLORS.put("nether", MaterialColor.NETHER);
		MATERIAL_COLORS.put("white_terracotta", MaterialColor.WHITE_TERRACOTTA);
		MATERIAL_COLORS.put("orange_terracotta", MaterialColor.ORANGE_TERRACOTTA);
		MATERIAL_COLORS.put("magenta_terracotta", MaterialColor.MAGENTA_TERRACOTTA);
		MATERIAL_COLORS.put("light_blue_terracotta", MaterialColor.LIGHT_BLUE_TERRACOTTA);
		MATERIAL_COLORS.put("yellow_terracotta", MaterialColor.YELLOW_TERRACOTTA);
		MATERIAL_COLORS.put("lime_terracotta", MaterialColor.LIME_TERRACOTTA);
		MATERIAL_COLORS.put("pink_terracotta", MaterialColor.PINK_TERRACOTTA);
		MATERIAL_COLORS.put("gray_terracotta", MaterialColor.GRAY_TERRACOTTA);
		MATERIAL_COLORS.put("light_gray_terracotta", MaterialColor.LIGHT_GRAY_TERRACOTTA);
		MATERIAL_COLORS.put("cyan_terracotta", MaterialColor.CYAN_TERRACOTTA);
		MATERIAL_COLORS.put("purple_terracotta", MaterialColor.PURPLE_TERRACOTTA);
		MATERIAL_COLORS.put("blue_terracotta", MaterialColor.BLUE_TERRACOTTA);
		MATERIAL_COLORS.put("brown_terracotta", MaterialColor.BROWN_TERRACOTTA);
		MATERIAL_COLORS.put("green_terracotta", MaterialColor.GREEN_TERRACOTTA);
		MATERIAL_COLORS.put("red_terracotta", MaterialColor.RED_TERRACOTTA);
		MATERIAL_COLORS.put("black_terracotta", MaterialColor.BLACK_TERRACOTTA);

		SOUND_GROUPS.put("wood", BlockSoundGroup.WOOD);
		SOUND_GROUPS.put("gravel", BlockSoundGroup.GRAVEL);
		SOUND_GROUPS.put("grass", BlockSoundGroup.GRASS);
		SOUND_GROUPS.put("stone", BlockSoundGroup.STONE);
		SOUND_GROUPS.put("metal", BlockSoundGroup.METAL);
		SOUND_GROUPS.put("glass", BlockSoundGroup.GLASS);
		SOUND_GROUPS.put("wool", BlockSoundGroup.WOOL);
		SOUND_GROUPS.put("sand", BlockSoundGroup.SAND);
		SOUND_GROUPS.put("snow", BlockSoundGroup.SNOW);
		SOUND_GROUPS.put("ladder", BlockSoundGroup.LADDER);
		SOUND_GROUPS.put("anvil", BlockSoundGroup.ANVIL);
		SOUND_GROUPS.put("slime", BlockSoundGroup.SLIME);
		SOUND_GROUPS.put("honey", BlockSoundGroup.HONEY);
		SOUND_GROUPS.put("wet_grass", BlockSoundGroup.WET_GRASS);
		SOUND_GROUPS.put("coral", BlockSoundGroup.CORAL);
		SOUND_GROUPS.put("babmoo", BlockSoundGroup.BAMBOO);
		SOUND_GROUPS.put("bamboo_sapling", BlockSoundGroup.BAMBOO_SAPLING);
		SOUND_GROUPS.put("scaffolding", BlockSoundGroup.SCAFFOLDING);
		SOUND_GROUPS.put("sweet_berry_bush", BlockSoundGroup.SWEET_BERRY_BUSH);
		SOUND_GROUPS.put("crop", BlockSoundGroup.CROP);
		SOUND_GROUPS.put("stem", BlockSoundGroup.STEM);
		SOUND_GROUPS.put("nether_wart", BlockSoundGroup.NETHER_WART);
		SOUND_GROUPS.put("lantern", BlockSoundGroup.LANTERN);
	}
}
