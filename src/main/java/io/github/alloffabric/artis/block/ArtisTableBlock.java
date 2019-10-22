package io.github.alloffabric.artis.block;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArtisTableBlock extends Block {
	private ArtisTableType type;

	//TODO: better support for block settings?
	public ArtisTableBlock(ArtisTableType type) {
		super(FabricBlockSettings.copy(Blocks.CRAFTING_TABLE).build());
		this.type = type;
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) ContainerProviderRegistry.INSTANCE.openContainer(type.getId(), player, buf -> {});
		return true;
	}
}
