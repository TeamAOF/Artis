package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.ArtisClient;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.api.RecipeProvider;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

public class ArtisCraftingController extends SyncedGuiDescription implements RecipeProvider {
	private ArtisTableType tableType;
	private PlayerEntity player;
	private ArtisCraftingInventory craftInv;
	private CraftingResultInventory resultInv;
	private ScreenHandlerContext context;

	private WPlainPanel panel;
	private WLabel label;
	private WLabel catalystCost;
	private WItemSlot grid;
	private WItemSlot catalyst;
	private WArtisResultSlot result;
	private WPlayerInvPanel playerInv;

	public ArtisCraftingController(ScreenHandlerType type, ArtisTableType tableType, int syncId, PlayerEntity player, ScreenHandlerContext context) {
		super(type, syncId, player.inventory, getBlockInventory(context), getBlockPropertyDelegate(context));

		this.tableType = tableType;
		this.player = player;
		this.context = context;

		this.resultInv = new CraftingResultInventory();
		this.craftInv = new ArtisCraftingInventory(this, tableType.getWidth(), tableType.getHeight());

		ContainerLayout layout = new ContainerLayout(tableType.getWidth(), tableType.getHeight());

		panel = new WPlainPanel();
		setRootPanel(panel);

		this.result = new WArtisResultSlot(player, craftInv, resultInv, 0, 1, 1, true, syncId);
		panel.add(result, layout.getResultX(), layout.getResultY() + 3);

		if (getTableType().hasCatalystSlot()) {
			this.catalyst = WItemSlot.of(craftInv, craftInv.size() - 1);
			panel.add(catalyst, layout.getCatalystX(), layout.getCatalystY());

			this.catalystCost = new WLabel("", 0xAA0000).setHorizontalAlignment(HorizontalAlignment.CENTER);
			panel.add(catalystCost, layout.getCatalystX(), layout.getCatalystY() + 18);
		}

		this.grid = WItemSlot.of(craftInv, 0, getTableType().getWidth(), getTableType().getHeight());
		panel.add(grid, layout.getGridX(), layout.getGridY());

		this.playerInv = this.createPlayerInventoryPanel();
		panel.add(playerInv, layout.getPlayerX(), layout.getPlayerY());

		this.label = new WLabel(ArtisClient.getName(tableType.getId()), 0x404040);
		panel.add(label, 0, 0);

		WSprite arrow = new WSprite(new Identifier(Artis.MODID, "textures/gui/arrow.png"));
		panel.add(arrow, layout.getArrowX(), layout.getArrowY() + 4, 22, 15);

		panel.validate(this);
	}

	public ArtisCraftingInventory getCraftInv() {
        return craftInv;
    }

    public PlayerEntity getPlayer() {
		return player;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void addPainters() {
	    int color = tableType.getColor();
	    if (tableType.hasColor()) {
            panel.setBackgroundPainter(BackgroundPainter.createColorful(color));
            grid.setBackgroundPainter(slotColor(color));
            if (tableType.hasCatalystSlot())
                catalyst.setBackgroundPainter(slotColor(color));
            result.setBackgroundPainter(slotColor(color));
            playerInv.setBackgroundPainter(slotColor(color));
        } else {
            panel.setBackgroundPainter(BackgroundPainter.VANILLA);
            grid.setBackgroundPainter(BackgroundPainter.SLOT);
            if (tableType.hasCatalystSlot())
                catalyst.setBackgroundPainter(BackgroundPainter.SLOT);
            result.setBackgroundPainter(BackgroundPainter.SLOT);
            playerInv.setBackgroundPainter(BackgroundPainter.SLOT);
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
							ScreenDrawing.drawBeveledPanel(x * 18 + left - 3, y * 18 + top - 3, 24, 24, lo, bg, hi);
						} else {
							ScreenDrawing.drawBeveledPanel(x * 18 + left, y * 18 + top, 18, 18, lo, bg, hi);
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
		return tableType.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return tableType.getHeight();
	}

	@Override
	public boolean matches(Recipe recipe) {
		return recipe.matches(craftInv, player.world);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingSlotCount() {
		return getTableType().getWidth() * getTableType().getHeight();
	}

	public void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftInv, CraftingResultInventory resultInv) {
		if (!world.isClient) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(this.tableType, craftInv, world);
            Optional<CraftingRecipe> optCrafting = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftInv, world);
            if (opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, recipe)) {
                    stack = recipe.craft(craftInv);
                }
            } else if (tableType.shouldIncludeNormalRecipes() && optCrafting.isPresent()) {
                CraftingRecipe recipe = optCrafting.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, recipe)) {
                    stack = recipe.craft(craftInv);
                }
            }

            resultInv.setStack(0, stack);
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, getCraftingResultSlotIndex(), stack));
        } else {
            Optional<CraftingRecipe> opt = world.getRecipeManager().getFirstMatch(this.tableType, craftInv, world);
            if (tableType.hasCatalystSlot() && opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (recipe instanceof ArtisCraftingRecipe) {
                    ArtisCraftingRecipe artisCraftingRecipe = (ArtisCraftingRecipe) recipe;
                    if (!artisCraftingRecipe.getCatalyst().isEmpty() && artisCraftingRecipe.getCatalystCost() > 0) {
                        catalystCost.setText(new LiteralText(Formatting.RED + "-" + artisCraftingRecipe.getCatalystCost()));
                    }
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

	public ArtisTableType getTableType() {
		return tableType;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasStack()) {
			ItemStack toTake = slot.getStack();
			stack = toTake.copy();
			if (slotIndex == getCraftingResultSlotIndex()) {
				this.context.run((world, pos) -> {
					toTake.getItem().onCraft(toTake, world, player);
				});
				if (!this.insertItem(toTake, 0, 35, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(toTake, stack);
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
			if (slotIndex == getCraftingResultSlotIndex()) {
				player.dropItem(takenStack, false);
			}
		}

		return stack;
	}

	@Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
		if (slotNumber == getCraftingResultSlotIndex() && action == SlotActionType.QUICK_MOVE) {
			return transferSlot(player, slotNumber);
		}

		return super.onSlotClick(slotNumber, button, action, player);
    }

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
