package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

public class ArtisScreenFactory implements ExtendedScreenHandlerFactory {
    private final ArtisTableType tableType;
    private final Block block;
    private final BlockHitResult blockHitResult;

    public ArtisScreenFactory(ArtisTableType tableType, Block block, BlockHitResult blockHitResult) {
        this.tableType = tableType;
        this.block = block;
        this.blockHitResult = blockHitResult;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ArtisCraftingController(Registry.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(player.world, blockHitResult.getBlockPos()));
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(blockHitResult.getBlockPos());
    }
}
