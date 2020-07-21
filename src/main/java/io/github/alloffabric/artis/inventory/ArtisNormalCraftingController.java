package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

/*
 * A special type of the ArtisCraftingController used for tables with support for vanilla crafting.
 */
public class ArtisNormalCraftingController extends ArtisCraftingController {
	public ArtisNormalCraftingController(ScreenHandlerType type, ArtisTableType tableType, int syncId, PlayerEntity player, ScreenHandlerContext context) {
		super(type, tableType, syncId, player, context);
	}
}
