package io.github.alloffabric.artis;

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
import net.minecraft.block.Block;
import net.minecraft.container.BlockContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;

public class Artis implements ModInitializer {
	public static final String MODID = "artis";

	public static final Logger logger = LogManager.getLogger();

    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BLOCKS = new ArrayList<>();

	public static final ArtisRegistry<ArtisTableType> ARTIS_TABLE_TYPES = new ArtisRegistry<>();

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

	public static void registerTable(ArtisTableType type, Block.Settings settings) {
		Identifier id = type.getId();
		if (type.shouldIncludeNormalRecipes()) {
            ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, containerId, player, buf) -> new ArtisNormalCraftingController(type, syncId, player, BlockContext.create(player.world, buf.readBlockPos())));
        } else {
            ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, containerId, player, buf) -> new ArtisCraftingController(type, syncId, player, BlockContext.create(player.world, buf.readBlockPos())));
        }
		if (!(type instanceof ArtisExistingBlockType) && !(type instanceof ArtisExistingItemType)) {
            ArtisTableBlock block = Registry.register(Registry.BLOCK, id, new ArtisTableBlock(type, settings));
            ARTIS_TABLE_BLOCKS.add(block);
            Registry.register(Registry.ITEM, id, new ArtisTableItem(block, new Item.Settings().group(ARTIS_GROUP)));
        }
        Registry.register(ARTIS_TABLE_TYPES, id, type);
	}
}
