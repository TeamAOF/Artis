package io.github.alloffabric.artis.inventory;

import com.google.common.collect.Lists;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

import java.util.List;

public class WArtisResultSlot extends WItemSlot {

	private final List<Slot> peers = Lists.newArrayList();
	private int startIndex;
	private int slotsWide;
	private int slotsHigh;
	private final PlayerEntity player;
	private final ArtisCraftingInventory craftingInventory;
	private Inventory inventory;
	private int syncId;

	public WArtisResultSlot(PlayerEntity player, ArtisCraftingInventory craftingInventory, Inventory resultInv, int startIndex, int slotsWide, int slotsHigh, boolean big, boolean ltr, int syncId) {
		super(resultInv, startIndex, slotsWide, slotsHigh, big, ltr);
		this.player = player;
		this.craftingInventory = craftingInventory;
		this.inventory = resultInv;
		this.startIndex = startIndex;
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.syncId = syncId;
	}

	@Override
	public void createPeers(GuiDescription c) {
		peers.clear();
		int index = startIndex;
		for (int y = 0; y < slotsHigh; y++) {
			for (int x = 0; x < slotsWide; x++) {
				ValidatedArtisResultSlot slot = new ValidatedArtisResultSlot(player, craftingInventory, inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18), syncId);
				peers.add(slot);
				c.addSlotPeer(slot);
				index++;
			}
		}
	}
}
