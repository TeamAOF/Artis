package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.SpecialCatalyst;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.util.DefaultedList;

public class ValidatedArtisResultSlot extends ValidatedSlot {

	private final ArtisCraftingInventory craftingInv;
	private final PlayerEntity player;
	private int amount;
	private int syncId;

	public ValidatedArtisResultSlot(PlayerEntity player, ArtisCraftingInventory inventory, Inventory inventoryIn, int index, int xPosition, int yPosition, int syncId) {
		super(inventoryIn, index, xPosition, yPosition);
		this.player = player;
		this.craftingInv = inventory;
		this.syncId = syncId;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount += Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void onTake(int amount) {
		this.amount += amount;
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		if (this.amount > 0) {
			stack.onCraft(this.player.world, this.player, this.amount);
		}

		if (this.inventory instanceof RecipeUnlocker) {
			((RecipeUnlocker) this.inventory).unlockLastRecipe(this.player);
		}

		this.amount = 0;
	}

	@Override
	public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		DefaultedList<ItemStack> remainders = player.world.getRecipeManager().getRemainingStacks(craftingInv.getType(), this.craftingInv, player.world);

		for (int i = 0; i < remainders.size() - 1; ++i) {
			ItemStack input = this.craftingInv.getInvStack(i);
			ItemStack remainder = remainders.get(i);
			if (!input.isEmpty()) {
				this.craftingInv.takeInvStack(i, 1);
				input = this.craftingInv.getInvStack(i);
			}

			if (!remainder.isEmpty()) {
				if (input.isEmpty()) {
					this.craftingInv.setInvStack(i, remainder);
				} else if (ItemStack.areItemsEqualIgnoreDamage(input, remainder) && ItemStack.areTagsEqual(input, remainder)) {
					remainder.increment(input.getCount());
					this.craftingInv.setInvStack(i, remainder);
				} else if (!this.player.inventory.insertStack(remainder)) {
					this.player.dropItem(remainder, false);
				}
			}
		}

		if (this.inventory instanceof RecipeUnlocker) {
			Recipe lastRecipe = ((RecipeUnlocker) inventory).getLastRecipe();
			if (lastRecipe instanceof ArtisCraftingRecipe) {
				ArtisCraftingRecipe recipe = (ArtisCraftingRecipe) lastRecipe;
				int catalystSlot = remainders.size() - 1;
				ItemStack remainder = remainders.get(catalystSlot).copy();
				if (!remainder.isEmpty()) {
					this.craftingInv.setInvStack(catalystSlot, remainder);
				} else {
					ItemStack catalyst = this.craftingInv.getCatalyst().copy();
					if (catalyst.isDamageable()) {
						catalyst.damage(recipe.getCatalystCost(), craftingInv.getPlayer(), (user) -> user.sendToolBreakStatus(user.getActiveHand()));
					} else if (catalyst.getItem() instanceof SpecialCatalyst) {
						catalyst = ((SpecialCatalyst)catalyst.getItem()).consume(catalyst, recipe.getCatalystCost());
					} else {
						catalyst.decrement(recipe.getCatalystCost());
					}
					this.craftingInv.setInvStack(catalystSlot, catalyst);
					if (!player.world.isClient) ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, new GuiSlotUpdateS2CPacket(syncId, catalystSlot + 1, catalyst));
				}
			}
		}

		return stack;
	}
}