package io.github.alloffabric.artis.inventory.slot;

import io.github.alloffabric.artis.inventory.ArtisCraftingInventory;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;

public class WArtisResultSlot extends WItemSlot {

    private final PlayerEntity player;
    private final ArtisCraftingInventory craftingInventory;
    private final Inventory inventory;
    private final int syncId;

    public WArtisResultSlot(PlayerEntity player, ArtisCraftingInventory craftingInventory, Inventory resultInv, int startIndex, int slotsWide, int slotsHigh, boolean big, int syncId) {
        super(resultInv, startIndex, slotsWide, slotsHigh, big);
        this.player = player;
        this.craftingInventory = craftingInventory;
        this.inventory = resultInv;
        this.syncId = syncId;
    }

    @Override
    protected ValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
        return new ValidatedArtisResultSlot(player, craftingInventory, this.inventory, index, x, y, syncId);
    }
}
