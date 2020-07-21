package io.github.alloffabric.artis;

import com.mojang.serialization.Lifecycle;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import io.github.alloffabric.artis.block.ArtisTableItem;
import io.github.alloffabric.artis.event.ArtisEvents;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.ArtisNormalCraftingController;
import io.github.alloffabric.artis.util.ArtisRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Artis implements ModInitializer {
	public static final String MODID = "artis";

	public static final Logger logger = LogManager.getLogger();

    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BLOCKS = new ArrayList<>();

	public static final ArtisRegistry<ArtisTableType> ARTIS_TABLE_TYPES = new ArtisRegistry<>(RegistryKey.ofRegistry(new Identifier(MODID, "artis_table_types")), Lifecycle.stable());

	public static final ItemGroup ARTIS_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "artis_group"), () -> new ItemStack(Items.CRAFTING_TABLE));

	public static boolean isLoaded = false;

	@Override
	public void onInitialize() {
		if (!isLoaded) {
			ArtisData.loadData();
			ArtisData.loadConfig();
			ArtisEvents.init();
			isLoaded = true;
		}
	}

	public static <T extends ArtisTableType> T registerTable(T type, Block.Settings settings) {
		return registerTable(type, settings, ARTIS_GROUP);
	}

	public static <T extends ArtisTableType> T registerTable(T type, Block.Settings settings, ItemGroup group) {
		Identifier id = type.getId();
		if (type.shouldIncludeNormalRecipes()) {
			ExtendedScreenHandlerType<ArtisNormalCraftingController> screenHandlerType = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new ArtisNormalCraftingController(null, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
			ScreenHandlerRegistry.registerExtended(id, (syncId, playerInventory, buf) -> new ArtisNormalCraftingController(screenHandlerType, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
			//ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, containerId, player, buf) -> new ArtisNormalCraftingController(type, syncId, player, ScreenHandlerContext.create(player.world, buf.readBlockPos())));
		} else {
			ExtendedScreenHandlerType<ArtisCraftingController> screenHandlerType = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new ArtisCraftingController(null, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
			ScreenHandlerRegistry.registerExtended(id, (syncId, playerInventory, buf) -> new ArtisCraftingController(screenHandlerType, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
			//ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, containerId, player, buf) -> new ArtisCraftingController(type, syncId, player, ScreenHandlerContext.create(player.world, buf.readBlockPos())));
		}
		if (!(type instanceof ArtisExistingBlockType) && !(type instanceof ArtisExistingItemType)) {
			ArtisTableBlock block = Registry.register(Registry.BLOCK, id, new ArtisTableBlock(type, settings));
			ARTIS_TABLE_BLOCKS.add(block);
			Registry.register(Registry.ITEM, id, new ArtisTableItem(block, new Item.Settings().group(group)));
		}
		return Registry.register(ARTIS_TABLE_TYPES, id, type);
	}
}
