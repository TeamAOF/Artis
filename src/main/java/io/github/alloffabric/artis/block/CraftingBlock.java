package io.github.alloffabric.artis.block;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingBlock extends Block {
	private ArtisTableType type;

	public CraftingBlock(ArtisTableType type, Block.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return super.activate(state, world, pos, player, hand, hit);
	}
}
