package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.recipe.ShapedArtisRecipe;
import io.github.alloffabric.artis.recipe.ShapelessArtisRecipe;
import me.shedaniel.rei.api.DisplayVisibilityHandler;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArtisDisplayVisibilityHandler implements DisplayVisibilityHandler {
    @Override
    public ActionResult handleDisplay(RecipeCategory<?> recipeCategory, RecipeDisplay recipeDisplay) {
        if (!(recipeCategory instanceof ArtisCategory) && recipeDisplay.getRecipeLocation().isPresent() && RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).isPresent()) {
            Recipe recipe = RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).get();

            if (recipe.getType() instanceof ArtisTableType) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}
