package io.github.alloffabric.artis.inventory;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class ArtisCraftingScreen extends CottonInventoryScreen<ArtisCraftingController> {
	public ArtisCraftingScreen(ArtisCraftingController container, PlayerEntity player) {
		super(container, player);
	}


}