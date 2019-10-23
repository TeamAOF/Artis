package io.github.alloffabric.artis.compat.rei;

import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArtisDisplay implements RecipeDisplay {
	private Item icon;
	private Identifier id;
	private int page;
	private List<ItemStack> inputs;
	private List<ItemStack> allInputs;

	public ArtisDisplay(Item icon, Identifier id, int page, List<ItemStack> inputs, List<ItemStack> allInputs) {
		this.icon = icon;
		this.id = id;
		this.page = page;
		this.inputs = inputs;
		this.allInputs = allInputs;
	}

	public int getPage() {
		return page;
	}

	@Override
	public List<List<ItemStack>> getInput() {
		return Collections.singletonList(inputs);
	}

	@Override
	public List<ItemStack> getOutput() {
		return Collections.singletonList(new ItemStack(icon));
	}

	@Override
	public Identifier getRecipeCategory() {
		return id;
	}

	@Override
	public List<List<ItemStack>> getRequiredItems() {
		return Collections.singletonList(allInputs);
	}
}
