package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.Optional;

public class ArtisCraftingController extends CottonScreenController {
	private ArtisTableType type;
	private PlayerEntity player;
	private ArtisCraftingInventory craftInv;
	private CraftingResultInventory resultInv;
	private BlockContext context;

	private WPlainPanel panel;
	private WLabel label;
	private WItemSlot grid;
	private WItemSlot catalyst;
	private WItemSlot result;
	private WPlayerInvPanel playerInv;

	public ArtisCraftingController(ArtisTableType type, int syncId, PlayerEntity player, BlockContext context) {
		super(type, syncId, player.inventory);
		this.type = type;
		this.player = player;
		this.context = context;
		panel = new WPlainPanel();
		this.setRootPanel(panel);
		this.craftInv = new ArtisCraftingInventory(this, 3, 3);
		this.resultInv = new CraftingResultInventory();
		label = new WLabel(new TranslatableText("container." + type.getId().getNamespace() + "." + type.getId().getPath()), 4210752);
		grid = new WItemSlot(craftInv, 0, 3, 3, false, true);
		catalyst = new WItemSlot(craftInv, 9, 1, 1, false, true);
		result = new WArtisResultSlot(player, craftInv, resultInv, 0, 1, 1, true, true, syncId);
		playerInv = new WPlayerInvPanel(player.inventory);
		//TODO: arrow?

		panel.add(label, 0, 0);
		panel.add(result, 116, 34);
		panel.add(grid, 22, 16);
		panel.add(catalyst, 85, 34);
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

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.context.run((world, pos) ->{
			dropInventory(player, world, craftInv);
		});
	}

	@Override
	public int getCraftingWidth() {
		return 3;
	}

	@Override
	public int getCraftingHeight() {
		return 3;
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe) {
		return recipe.matches(craftInv, player.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingSlotCount() {
		return 11;
	}

	@Override
	public void onContentChanged(Inventory inv) {
		this.context.run((world, pos) -> {
			if (!world.isClient) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				ItemStack stack = ItemStack.EMPTY;

				Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(type, inv, world);
				if (opt.isPresent()) {
					CraftingRecipe craftingRecipe_1 = opt.get();
					if (resultInv.shouldCraftRecipe(world, serverPlayer, craftingRecipe_1)) {
						stack = craftingRecipe_1.craft((ArtisCraftingInventory)inv);
					}
				}
				resultInv.setInvStack(0, stack);
				serverPlayer.networkHandler.sendPacket(new GuiSlotUpdateS2CPacket(syncId, 0, stack));
			}
		});
	}

	ArtisTableType getTableType() {
		return type;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int slotIndex) { // + 11
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slotList.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack toTake = slot.getStack();
			stack = toTake.copy();
			if (slotIndex == 0) {
				this.context.run((world, pos) -> {
					toTake.getItem().onCraft(toTake, world, player);
				});
				if (!this.insertItem(toTake, 11, 47, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(toTake, stack);
			} else if (slotIndex >= 11 && slotIndex < 38) {
				if (!this.insertItem(toTake, 38, 47, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= 38 && slotIndex < 47) {
				if (!this.insertItem(toTake, 11, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(toTake, 11, 47, false)) {
				return ItemStack.EMPTY;
			}

			if (toTake.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (toTake.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack takenStack = slot.onTakeItem(player, toTake);
			if (slotIndex == 0) {
				player.dropItem(takenStack, false);
			}
		}

		return stack;
	}

	@Override
	public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
		if (action == SlotActionType.QUICK_MOVE) {
			return transferSlot(player, slotNumber);
		} else {
			return super.onSlotClick(slotNumber, button, action, player);
		}
	}
}
