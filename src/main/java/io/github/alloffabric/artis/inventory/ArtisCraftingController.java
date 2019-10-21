package io.github.alloffabric.artis.inventory;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.recipe.RecipeType;

public class ArtisCraftingController extends CottonScreenController {
	private ArtisCraftingInventory craftInv;
	private CraftingResultInventory resultInv;

	public ArtisCraftingController(RecipeType<?> type, int syncId, PlayerInventory inv) {
		super(type, syncId, inv);
		WPlainPanel panel = new WPlainPanel();
		this.setRootPanel(panel);
		this.craftInv = new ArtisCraftingInventory(this, 3, 3);
		this.resultInv = new CraftingResultInventory();
		WItemSlot grid = new WItemSlot(craftInv, 0, 3, 3, false, true);
		WItemSlot catalyst = new WItemSlot(craftInv, 9, 1, 1, false, true);
		WItemSlot result = new WItemSlot(resultInv, 0, 1, 1, true, true);
		WPanel playerInv = new WPlayerInvPanel(inv);

		panel.validate(this);
	}

}
