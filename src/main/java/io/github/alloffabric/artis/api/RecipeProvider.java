package io.github.alloffabric.artis.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;

public interface RecipeProvider {
    void populateRecipeFinder(RecipeFinder finder);

    void clearCraftingSlots();

    boolean matches(Recipe recipe);

    int getCraftingResultSlotIndex();

    int getCraftingWidth();

    int getCraftingHeight();

    @Environment(EnvType.CLIENT)
    int getCraftingSlotCount();
}
