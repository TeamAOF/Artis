package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.text.TranslatableText;

public class ArtisCraftingController extends CottonScreenController {
	private ArtisTableType type;
	private PlayerEntity player;
	private ArtisCraftingInventory craftInv;
	private CraftingResultInventory resultInv;

	private WPlainPanel panel;
	private WLabel label;
	private WItemSlot grid;
	private WItemSlot catalyst;
	private WItemSlot result;
	private WPlayerInvPanel playerInv;

	public ArtisCraftingController(ArtisTableType type, int syncId, PlayerEntity player) {
		super(type, syncId, player.inventory);
		this.type = type;
		this.player = player;
		panel = new WPlainPanel();
		this.setRootPanel(panel);
		this.craftInv = new ArtisCraftingInventory(this, 3, 3);
		this.resultInv = new CraftingResultInventory();
		label = new WLabel(new TranslatableText("container." + type.getId().getNamespace() + "." + type.getId().getPath()), 4210752);
		grid = new WItemSlot(craftInv, 0, 3, 3, false, true);
		catalyst = new WItemSlot(craftInv, 9, 1, 1, false, true);
		result = new WItemSlot(resultInv, 0, 1, 1, true, true);
		playerInv = new WPlayerInvPanel(player.inventory);
		//TODO: arrow?

		panel.add(label, 0, 0);
		panel.add(grid, 22, 16);
		panel.add(catalyst, 85, 34);
		panel.add(result, 116, 34);
		panel.add(playerInv, 0, 78);

		panel.validate(this);
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	@Override
	public void addPainters() {
		if (type.hasColor()) {
			panel.setBackgroundPainter(BackgroundPainter.createColorful(type.getColor()));
			grid.setBackgroundPainter(slotColor(type.getColor()));
			catalyst.setBackgroundPainter(slotColor(type.getColor()));
			result.setBackgroundPainter(slotColor(type.getColor()));
			playerInv.setBackgroundPainter(slotColor(type.getColor()));
		}
	}

	private static BackgroundPainter slotColor(int color) {
		return (left, top, panel) -> {
			int lo = ScreenDrawing.multiplyColor(color, 0.5F);
			int bg = 0x4C000000;
			int hi = ScreenDrawing.multiplyColor(color, 1.25F);
			if (!(panel instanceof WItemSlot)) {
				ScreenDrawing.drawBeveledPanel(left - 1, top - 1, panel.getWidth() + 2, panel.getHeight() + 2, lo, bg, hi);
			} else {
				WItemSlot slot = (WItemSlot)panel;

				for(int x = 0; x < slot.getWidth() / 18; ++x) {
					for(int y = 0; y < slot.getHeight() / 18; ++y) {
						if (slot.isBigSlot()) {
							ScreenDrawing.drawBeveledPanel(x * 18 + left - 4, y * 18 + top - 4, 24, 24, lo, bg, hi);
						} else {
							ScreenDrawing.drawBeveledPanel(x * 18 + left - 1, y * 18 + top - 1, 18, 18, lo, bg, hi);
						}
					}
				}
			}
		};
	}
}
