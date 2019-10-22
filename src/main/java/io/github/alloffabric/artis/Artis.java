package io.github.alloffabric.artis;

import blue.endless.jankson.JsonObject;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.ArtisCraftingScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Artis implements ModInitializer {
	public static final String MODID = "artis";

	public static final Logger logger = LogManager.getLogger();

	public static final Registry<ArtisTableType> ARTIS_TABLE_TYPES = new SimpleRegistry<>();

	public static final ItemGroup ARTIS_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "artis_group"), () -> new ItemStack(Items.CRAFTING_TABLE));

	public static Block TEST_TABLE;

	@Override
	public void onInitialize() {
//		TEST_TABLE = registerTable(new ArtisTableType(new Identifier(MODID, "test_table"), 0x7F76D1));
		ArtisData.loadData();
		ArtisData.loadConfig();
	}

	public static Block registerTable(ArtisTableType type) {
		Identifier id = type.getId();
		ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, containerId, player, buf) -> new ArtisCraftingController(type, syncId, player, BlockContext.create(player.world, buf.readBlockPos())));
		ScreenProviderRegistry.INSTANCE.registerFactory(id, controller -> new ArtisCraftingScreen((ArtisCraftingController) controller, ((ArtisCraftingController) controller).getPlayer()));
		Block block = Registry.register(Registry.BLOCK, id, new ArtisTableBlock(type));
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ARTIS_GROUP)));
		return block;
	}
}
