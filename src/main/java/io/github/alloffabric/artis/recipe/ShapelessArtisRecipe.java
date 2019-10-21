package io.github.alloffabric.artis.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

public class ShapelessArtisRecipe extends ShapelessRecipe {
	private RecipeType type;
	private RecipeSerializer serializer;
	private Ingredient catalyst;
	private int catalystCost;

	public ShapelessArtisRecipe(RecipeType type, RecipeSerializer serializer, Identifier id, String group, DefaultedList<Ingredient> ingredients,ItemStack output,  Ingredient catalyst, int catalystCost) {
		super(id, group, output, ingredients);
		this.type = type;
		this.serializer = serializer;
		this.catalyst = catalyst;
		this.catalystCost = catalystCost;
	}
}
