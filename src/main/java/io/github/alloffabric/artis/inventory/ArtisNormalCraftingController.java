package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;

/*
 * A special type of the ArtisCraftingController used for tables with support for vanilla crafting.
 */
public class ArtisNormalCraftingController extends ArtisCraftingController {
	public ArtisNormalCraftingController(ArtisTableType type, int syncId, PlayerEntity player, ScreenHandlerContext context) {
		super(type, syncId, player, context);
	}
}
