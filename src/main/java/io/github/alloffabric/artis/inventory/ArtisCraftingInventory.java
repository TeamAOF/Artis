package io.github.alloffabric.artis.inventory;

import net.minecraft.container.Container;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;

public class ArtisCraftingInventory extends CraftingInventory {
	private ItemStack catalyst = ItemStack.EMPTY;
	private int catalystSlot;
	private Container container;

	public ArtisCraftingInventory(Container container, int width, int height) {
		super(container, width, height);
		this.container = container;
		this.catalystSlot = (width * height) + 1;
	}

	@Override
	public int getInvSize() {
		return super.getInvSize() + 1;
	}

	@Override
	public boolean isInvEmpty() {
		if (!catalyst.isEmpty()) return false;
		return super.isInvEmpty();
	}

	@Override
	public ItemStack getInvStack(int slot) {
		if (slot == catalystSlot) return catalyst;
		return super.getInvStack(slot);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		if (slot == catalystSlot) {
			ItemStack copy = catalyst.copy();
			catalyst = ItemStack.EMPTY;
			return copy;
		}
		return super.removeInvStack(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot == catalystSlot) {
			ItemStack stack = catalyst.split(amount);
			if (!stack.isEmpty()) {
				this.container.onContentChanged(this);
			}
			return stack;
		} else {
			return super.takeInvStack(slot, amount);
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		if (slot == catalystSlot) {
			catalyst = stack;
			this.container.onContentChanged(this);
		} else {
			super.setInvStack(slot, stack);
		}
	}

	@Override
	public void clear() {
		catalyst = ItemStack.EMPTY;
		super.clear();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		finder.addNormalItem(catalyst);
		super.provideRecipeInputs(finder);
	}

	public ItemStack getCatalyst() {
		return catalyst;
	}
}
