package io.github.alloffabric.artis.block;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.entity.ArtisTableBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ArtisTableBEBlock extends ArtisTableBlock implements BlockEntityProvider {
    public ArtisTableBEBlock(ArtisTableType type, Settings settings) {
        super(type, settings);
    }

    @Nullable
    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory) blockEntity : null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new ArtisTableBlockEntity(getType());
    }
}
