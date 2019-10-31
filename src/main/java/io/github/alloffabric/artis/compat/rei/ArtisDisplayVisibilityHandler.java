package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.recipe.ShapedArtisRecipe;
import io.github.alloffabric.artis.recipe.ShapelessArtisRecipe;
import me.shedaniel.rei.api.DisplayVisibilityHandler;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;

public class ArtisDisplayVisibilityHandler implements DisplayVisibilityHandler {
    @Override
    public ActionResult handleDisplay(RecipeCategory<?> recipeCategory, RecipeDisplay recipeDisplay) {
        if (!(recipeCategory instanceof ArtisCategory) && recipeDisplay.getRecipeLocation().isPresent() && RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).isPresent()) {
            Recipe recipe = RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).get();

            if (recipe instanceof ShapedArtisRecipe || recipe instanceof ShapelessArtisRecipe) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}
