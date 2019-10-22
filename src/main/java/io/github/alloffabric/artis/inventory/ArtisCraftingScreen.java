package io.github.alloffabric.artis.inventory;

import io.github.cottonmc.cotton.gui.client.CottonScreen;
import net.minecraft.entity.player.PlayerEntity;

public class ArtisCraftingScreen extends CottonScreen<ArtisCraftingController> {
	public ArtisCraftingScreen(ArtisCraftingController container, PlayerEntity player) {
		super(container, player);
	}
}