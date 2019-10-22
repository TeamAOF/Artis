package io.github.alloffabric.artis.api;

import net.minecraft.recipe.Ingredient;

public interface ArtisCraftingRecipe {
	Ingredient getCatalyst();
	int getCatalystCost();
}
