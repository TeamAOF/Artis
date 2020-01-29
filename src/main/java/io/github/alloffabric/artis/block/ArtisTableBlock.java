package io.github.alloffabric.artis.block;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArtisTableBlock extends Block {
	private ArtisTableType type;

	public ArtisTableBlock(ArtisTableType type, Block.Settings settings) {
		super(settings);
		this.type = type;
	}

    public ArtisTableType getType() {
        return type;
    }

    @Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) ContainerProviderRegistry.INSTANCE.openContainer(type.getId(), player, buf -> buf.writeBlockPos(pos));
		return ActionResult.SUCCESS;
	}
}
