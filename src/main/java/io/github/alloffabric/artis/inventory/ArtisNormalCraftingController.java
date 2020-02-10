package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;

/*
 * A special type of the ArtisCraftingController used for tables with support for vanilla crafting.
 */
public class ArtisNormalCraftingController extends ArtisCraftingController {
	public ArtisNormalCraftingController(ArtisTableType type, int syncId, PlayerEntity player, BlockContext context) {
		super(type, syncId, player, context);
	}
}
