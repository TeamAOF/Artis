package io.github.alloffabric.artis.recipe;

import io.github.alloffabric.artis.inventory.ArtisCraftingInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ShapedArtisRecipe extends ShapedRecipe {
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
		return super.method_17728(inventory, world); //ShapedRecipe.matches (mapping gets lost because of generics)
	}

	@Override
	public RecipeType getType() {
		return type;
	}

	@Override
	public RecipeSerializer getSerializer() {
		return serializer;
	}

	public Ingredient getCatalyst() {
		return catalyst;
	}

	public int getCatalystCost() {
		return catalystCost;
	}
}
