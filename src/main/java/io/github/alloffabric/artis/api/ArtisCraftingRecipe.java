package io.github.alloffabric.artis.api;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;

public interface ArtisCraftingRecipe extends CraftingRecipe {
    Ingredient getCatalyst();

    int getCatalystCost();

    int getWidth();

    int getHeight();
}
