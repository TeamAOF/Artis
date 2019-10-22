package io.github.alloffabric.artis.recipe;

import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.inventory.ArtisCraftingInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Random;

public class ShapedArtisRecipe extends ShapedRecipe implements ArtisCraftingRecipe {
	private RecipeType type;
	private RecipeSerializer serializer;
	private Ingredient catalyst;
	private int catalystCost;

	public ShapedArtisRecipe(RecipeType type, RecipeSerializer serializer, Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output, Ingredient catalyst, int catalystCost) {
		super(id, group, width, height, ingredients, output);
		this.type = type;
		this.serializer = serializer;
		this.catalyst = catalyst;
		this.catalystCost = catalystCost;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (!(inventory instanceof ArtisCraftingInventory)) return false;
		ArtisCraftingInventory artis = (ArtisCraftingInventory)inventory;
		ItemStack toTest = artis.getCatalyst();
		if (!catalyst.test(toTest)) return false;
		if (toTest.isDamageable()) {
			if (toTest.getMaxDamage() - toTest.getDamage() < catalystCost) return false;
		} else {
			if (toTest.getCount() < catalystCost) return false;
		}

		for(int i = 0; i <= artis.getWidth() - this.getWidth(); ++i) {
			for(int j = 0; j <= artis.getHeight() - this.getHeight(); ++j) {
				if (this.matchesSmall(artis, i, j, true)) {
					return true;
				}

				if (this.matchesSmall(artis, i, j, false)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean matchesSmall(CraftingInventory inventory, int maxWidth, int maxHeight, boolean tall) {
		for(int i = 0; i < inventory.getWidth(); ++i) {
			for(int j = 0; j < inventory.getHeight(); ++j) {
				int x = i - maxWidth;
				int y = j - maxHeight;
				Ingredient ingredient = Ingredient.EMPTY;
				if (x >= 0 && y >= 0 && x < this.getWidth() && y < this.getHeight()) {
					if (tall) {
						ingredient = this.getPreviewInputs().get(this.getWidth() - x - 1 + y * this.getWidth());
					} else {
						ingredient = this.getPreviewInputs().get(x + y * this.getWidth());
					}
				}

				if (!ingredient.test(inventory.getInvStack(i + j * inventory.getWidth()))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack craft(CraftingInventory inv) {
		return this.getOutput().copy();
	}

	@Override
	public RecipeType getType() {
		return type;
	}

	@Override
	public RecipeSerializer getSerializer() {
		return serializer;
	}

	@Override
	public Ingredient getCatalyst() {
		return catalyst;
	}

	@Override
	public int getCatalystCost() {
		return catalystCost;
	}

}
