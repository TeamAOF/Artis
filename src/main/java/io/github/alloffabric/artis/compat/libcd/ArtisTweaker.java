package io.github.alloffabric.artis.compat.libcd;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.recipe.ShapedArtisRecipe;
import io.github.alloffabric.artis.recipe.ShapelessArtisRecipe;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ArtisTweaker {
	private ArtisTableType type;
	private RecipeTweaker tweaker = RecipeTweaker.INSTANCE;

	public ArtisTweaker(ArtisTableType type) {
		this.type = type;
	}

	public void addShaped(Object[][] inputs, Object output, Object catalyst, int cost) {
		addShaped(inputs, output, catalyst, cost, "");
	}

	/**
	 * Add a shaped recipe from a 2D array of inputs, like a standard CraftTweaker recipe.
	 * @param inputs the 2D array (array of arrays) of inputs to use.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShaped(Object[][] inputs, Object output, Object catalyst, int cost, String group) {
		try {
			Object[] processed = RecipeParser.processGrid(inputs, type.getWidth(),  type.getHeight());
			int width = inputs[0].length;
			int height = inputs.length;
			addShaped(processed, output, width, height, catalyst, cost, group);
		} catch (Exception e) {
			tweaker.getLogger().error("Error parsing 2D array shaped recipe - " + e.getMessage());
		}
	}

	public void addShaped(Object[] inputs, Object output, int width, int height, Object catalyst, int cost) {
		addShaped(inputs, output, width, height, catalyst, cost, "");
	}

	/**
	 * Register a shaped crafting recipe from a 1D array of inputs.
	 * @param inputs The input item or tag ids required in order: left to right, top to bottom.
	 * @param output The output of the recipe.
	 * @param width How many rows the recipe needs.
	 * @param height How many columns the recipe needs.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShaped(Object[] inputs, Object output, int width, int height, Object catalyst, int cost, String group){
		try {
			ItemStack stack = RecipeParser.processItemStack(output);
			Identifier recipeId = tweaker.getRecipeId(stack);
			DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
			for (int i = 0; i < Math.min(inputs.length, width * height); i++) {
				Object id = inputs[i];
				if (id == null || id.equals("") || id.equals("minecraft:air")) continue;
				ingredients.set(i, RecipeParser.processIngredient(id));
			}
			Ingredient catalystIng = RecipeParser.processIngredient(catalyst);
			tweaker.addRecipe(new ShapedArtisRecipe(type, type.getShaped(), recipeId, group, width, height, ingredients, stack, catalystIng, cost));
		} catch (Exception e) {
			tweaker.getLogger().error("Error parsing 1D array shaped recipe - " + e.getMessage());
		}
	}

	public void addDictShaped(String[] pattern, Map<String, Object> dictionary, Object output, Object catalyst, int cost) {
		addDictShaped(pattern, dictionary, output, catalyst, cost, "");
	}

	/**
	 * Register a shaped crafting recipe from a pattern and dictionary.
	 * @param pattern A crafting pattern like one you'd find in a vanilla recipe JSON.
	 * @param dictionary A map of single characters to item or tag ids.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addDictShaped(String[] pattern, Map<String, Object> dictionary, Object output, Object catalyst, int cost, String group) {
		try {
			ItemStack stack = RecipeParser.processItemStack(output);
			Identifier recipeId = tweaker.getRecipeId(stack);
			pattern = RecipeParser.processPattern(pattern);
			Map<String, Ingredient> map = RecipeParser.processDictionary(dictionary);
			int x = pattern[0].length();
			int y = pattern.length;
			DefaultedList<Ingredient> ingredients = RecipeParser.getIngredients(pattern, map, x, y);
			Ingredient catalystIng = RecipeParser.processIngredient(catalyst);
			tweaker.addRecipe(new ShapedArtisRecipe(type, type.getShaped(), recipeId, group, x, y, ingredients, stack, catalystIng, cost));
		} catch (Exception e) {
			tweaker.getLogger().error("Error parsing dictionary shaped recipe - " + e.getMessage());
		}
	}

	public void addShapeless(Object[] inputs, Object output, Object catalyst, int cost) {
		addShapeless(inputs, output, catalyst, cost, "");
	}

	/**
	 * Register a shapeless crafting recipe from an array of inputs.
	 * @param inputs A list of input item or tag ids required for the recipe.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShapeless(Object[] inputs, Object output, Object catalyst, int cost, String group) {
		try {
			ItemStack stack = RecipeParser.processItemStack(output);
			Identifier recipeId = tweaker.getRecipeId(stack);
			DefaultedList<Ingredient> ingredients = DefaultedList.of();
			for (int i = 0; i < Math.min(inputs.length, type.getWidth() * type.getHeight()); i++) {
				Object id = inputs[i];
				if (id.equals("")) continue;
				ingredients.add(i, RecipeParser.processIngredient(id));
			}
			Ingredient catalystIng = RecipeParser.processIngredient(catalyst);
			tweaker.addRecipe(new ShapelessArtisRecipe(type, type.getShapeless(), recipeId, group, ingredients, stack, catalystIng, cost));
		} catch (Exception e) {
			tweaker.getLogger().error("Error parsing shapeless recipe - " + e.getMessage());
		}
	}
}
