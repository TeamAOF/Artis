package io.github.alloffabric.artis;

import com.mojang.serialization.Lifecycle;
import dev.latvian.kubejs.script.ScriptType;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBEBlock;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import io.github.alloffabric.artis.block.ArtisTableItem;
import io.github.alloffabric.artis.block.entity.ArtisTableBlockEntity;
import io.github.alloffabric.artis.compat.kubejs.ArtisJsonRegistryEventJS;
import io.github.alloffabric.artis.event.ArtisEvents;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.util.ArtisRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

public class Artis implements ModInitializer {
    public static final String MODID = "artis";

    public static final Identifier recipe_sync = new Identifier(MODID,"sync_recipe");
    public static final Identifier request_sync = new Identifier(MODID,"request_sync");
    public static final Identifier dummy = new Identifier("null","null");

    public static final Logger logger = LogManager.getLogger();

    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BLOCKS = new ArrayList<>();
    public static final ArrayList<ArtisTableBlock> ARTIS_TABLE_BE_BLOCKS = new ArrayList<>();

    public static final ArtisRegistry<ArtisTableType> ARTIS_TABLE_TYPES = new ArtisRegistry<>(RegistryKey.ofRegistry(new Identifier(MODID, "artis_table_types")), Lifecycle.stable());
    public static final ItemGroup ARTIS_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "artis_group"), () -> new ItemStack(Items.CRAFTING_TABLE));
    public static BlockEntityType<ArtisTableBlockEntity> ARTIS_BLOCK_ENTITY;
    public static boolean isLoaded = false;

    public static <T extends ArtisTableType> T registerTable(T type, Block.Settings settings) {
        return registerTable(type, settings, ARTIS_GROUP);
    }

    public static <T extends ArtisTableType> T registerTable(T type, Block.Settings settings, ItemGroup group) {
        Identifier id = type.getId();
        ExtendedScreenHandlerType<ArtisCraftingController> screenHandlerType = new ExtendedScreenHandlerType<>((syncId, playerInventory, buf) -> new ArtisCraftingController(null, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
        ScreenHandlerRegistry.registerExtended(id, (syncId, playerInventory, buf) -> new ArtisCraftingController(screenHandlerType, type, syncId, playerInventory.player, ScreenHandlerContext.create(playerInventory.player.world, buf.readBlockPos())));
        if (!(type instanceof ArtisExistingBlockType) && !(type instanceof ArtisExistingItemType)) {
            ArtisTableBlock block;
            if (!type.hasBlockEntity()) {
                block = Registry.register(Registry.BLOCK, id, new ArtisTableBlock(type, settings));
            } else {
                block = Registry.register(Registry.BLOCK, id, new ArtisTableBEBlock(type, settings));
                ARTIS_TABLE_BE_BLOCKS.add(block);
            }
            ARTIS_TABLE_BLOCKS.add(block);
            Registry.register(Registry.ITEM, id, new ArtisTableItem(block, new Item.Settings().group(group)));
        }
        return Registry.register(ARTIS_TABLE_TYPES, id, type);
    }

    private static <B extends BlockEntity> BlockEntityType<B> registerBlockEntity(String name, Supplier<B> supplier, Block... supportedBlocks) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(supplier, supportedBlocks).build(null));
    }

    @Override
    public void onInitialize() {
        if (!isLoaded) {
            if (FabricLoader.getInstance().isModLoaded("kubejs"))
                new ArtisJsonRegistryEventJS().post(ScriptType.STARTUP, "artis.registry");

            ArtisData.loadData();
            ArtisData.loadConfig();
            ArtisEvents.init();
            isLoaded = true;
            ARTIS_BLOCK_ENTITY = registerBlockEntity("artis_table", ArtisTableBlockEntity::new, Arrays.copyOf(ARTIS_TABLE_BLOCKS.toArray(), ARTIS_TABLE_BLOCKS.size(), ArtisTableBlock[].class));

            //seems to be required to not have the recipe vanish when initially opened
            ServerSidePacketRegistry.INSTANCE.register(Artis.request_sync,
                    (packetContext, attachedData) -> {
                        packetContext.getTaskQueue().execute(() -> {
                            ScreenHandler container = packetContext.getPlayer().currentScreenHandler;
                            if (container instanceof ArtisCraftingController) {
                                container.onContentChanged(null);
                            }
                        });
                    });
        }
    }
}
