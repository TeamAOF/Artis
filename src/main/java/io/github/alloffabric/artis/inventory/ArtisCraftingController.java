package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.ArtisClient;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.recipe.ShapedArtisRecipe;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.ContainerSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

public class ArtisCraftingController extends CottonCraftingController {
	private ArtisTableType type;
	private PlayerEntity player;
	private ArtisCraftingInventory craftInv;
	private CraftingResultInventory resultInv;
	private BlockContext context;

	private WPlainPanel panel;
	private WLabel label;
	private WLabel catalystCost;
	private WItemSlot grid;
	private WItemSlot catalyst;
	private WItemSlot result;
	private WPlayerInvPanel playerInv;

	private int resultSlot = 0;
	private int catalystSlot;
	private int firstPlayerInvSlot;
	private int firstPlayerHotbarSlot;
	private int lastSlot;

	public ArtisCraftingController(ArtisTableType type, int syncId, PlayerEntity player, BlockContext context) {
		super(type, syncId, player.inventory);
		catalystSlot = type.getWidth() * type.getHeight() + 1;
		firstPlayerInvSlot = catalystSlot + 1;
		firstPlayerHotbarSlot = firstPlayerInvSlot + 27;
		lastSlot = firstPlayerHotbarSlot + 9;
		this.type = type;
		this.player = player;
		this.context = context;
		panel = new WPlainPanel();
		this.setRootPanel(panel);
		this.craftInv = new ArtisCraftingInventory(this, type.getWidth(), type.getHeight());
		this.resultInv = new CraftingResultInventory();
		label = new WLabel(ArtisClient.getName(type.getId()), 0x404040);
		grid = new WItemSlot(craftInv, 0, type.getWidth(), type.getHeight(), false, true);
		catalyst = new WItemSlot(craftInv, craftInv.getInvSize() - 1, 1, 1, false, true);
		catalystCost = new WLabel("", 0xAA0000).setAlignment(Alignment.CENTER);
		result = new WArtisResultSlot(player, craftInv, resultInv, 0, 1, 1, true, true, syncId);
		playerInv = new WPlayerInvPanel(player.inventory);
		WSprite arrow = new WSprite(new Identifier(Artis.MODID, "textures/gui/arrow.png"));
		ContainerLayout layout = new ContainerLayout(type.getWidth(), type.getHeight());

		panel.add(label, 0, 0);
		panel.add(result, layout.getResultX(), layout.getResultY());
		panel.add(grid, layout.getGridX(), layout.getGridY());
		panel.add(catalyst, layout.getCatalystX(), layout.getCatalystY());
		panel.add(catalystCost, layout.getCatalystX(), layout.getCatalystY() + 18);
		panel.add(playerInv, layout.getPlayerX(), layout.getPlayerY());
		panel.add(arrow, layout.getArrowX(), layout.getArrowY(), 22, 15);

		panel.validate(this);
	}

    public int getCatalystSlot() {
        return catalystSlot;
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
		return type.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return type.getHeight();
	}

	@Override
	public boolean matches(Recipe<? super Inventory> recipe) {
		return recipe.matches(craftInv, player.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return resultSlot;
	}

	@Override
	public int getCraftingSlotCount() {
		return firstPlayerInvSlot;
	}

	public void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftInv, CraftingResultInventory resultInv) {
		if (!world.isClient) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(this.type, craftInv, world);
            if (opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, recipe)) {
                    stack = recipe.craft(craftInv);
                }
            }

            resultInv.setInvStack(0, stack);
            serverPlayer.networkHandler.sendPacket(new ContainerSlotUpdateS2CPacket(syncId, 0, stack));
        } else if (world.isClient) {
            MinecraftClient client = MinecraftClient.getInstance();
            Optional<CraftingRecipe> opt = client.world.getRecipeManager().getFirstMatch(this.type, craftInv, world);
            if (opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (recipe instanceof ArtisCraftingRecipe) {
                    ArtisCraftingRecipe artisCraftingRecipe = (ArtisCraftingRecipe) recipe;
                    catalystCost.setText(new LiteralText(Formatting.RED + "-" + artisCraftingRecipe.getCatalystCost()));
                }
            } else {
                catalystCost.setText(new LiteralText(""));
            }
        }
	}

	@Override
	public void onContentChanged(Inventory inv) {
		this.context.run((world, pos) -> {
			updateResult(this.syncId, world, this.player, this.craftInv, this.resultInv);
		});
	}

	ArtisTableType getTableType() {
		return type;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack toTake = slot.getStack();
			stack = toTake.copy();
			if (slotIndex == resultSlot) {
				this.context.run((world, pos) -> {
					toTake.getItem().onCraft(toTake, world, player);
				});
				if (!this.insertItem(toTake, firstPlayerInvSlot, lastSlot, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(toTake, stack);
			} else if (slotIndex >= firstPlayerInvSlot && slotIndex < firstPlayerHotbarSlot) {
				if (!this.insertItem(toTake, firstPlayerHotbarSlot, lastSlot, false)) {
					return ItemStack.EMPTY;
				}
			} else if (slotIndex >= firstPlayerHotbarSlot && slotIndex < lastSlot) {
				if (!this.insertItem(toTake, firstPlayerInvSlot, firstPlayerHotbarSlot, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(toTake, firstPlayerInvSlot, lastSlot, false)) {
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
			if (slotIndex == resultSlot) {
				player.dropItem(takenStack, false);
			}
		}

		return stack;
	}

//	@Override
//	public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
//		if (action == SlotActionType.QUICK_MOVE) {
//			return transferSlot(player, slotNumber);
//		} else {
//			return super.onSlotClick(slotNumber, button, action, player);
//		}
//	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder) {
		this.craftInv.provideRecipeInputs(finder);
	}

	@Override
	public void clearCraftingSlots() {
		this.craftInv.clear();
		this.resultInv.clear();
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
	}
}
