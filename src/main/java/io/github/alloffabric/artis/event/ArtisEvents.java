package io.github.alloffabric.artis.event;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisScreenFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public class ArtisEvents {
    public static void init() {
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            Identifier identifier = Registry.BLOCK.getId(block);
            if (Artis.ARTIS_TABLE_TYPES.hasId(identifier)) {
                ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(identifier);
                if (type instanceof ArtisExistingBlockType) {
                    if (!world.isClient) playerEntity.openHandledScreen(new ArtisScreenFactory(type, block, blockHitResult));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            if (!playerEntity.getStackInHand(hand).isEmpty()) {
                Item item = playerEntity.getStackInHand(hand).getItem();
                Identifier identifier = Registry.ITEM.getId(item);
                if (Artis.ARTIS_TABLE_TYPES.hasId(identifier)) {
                    ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(identifier);
                    if (type instanceof ArtisExistingItemType) {
                        if (!world.isClient)
                            ContainerProviderRegistry.INSTANCE.openContainer(identifier, playerEntity, buf -> buf.writeBlockPos(playerEntity.getBlockPos()));
                        return TypedActionResult.success(playerEntity.getStackInHand(hand));
                    }
                }
            }
            return TypedActionResult.pass(playerEntity.getStackInHand(hand));
        });
    }
}
