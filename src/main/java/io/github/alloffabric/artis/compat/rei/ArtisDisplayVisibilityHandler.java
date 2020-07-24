package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.api.ArtisTableType;
import me.shedaniel.rei.api.DisplayVisibilityHandler;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.ActionResult;

@Environment(EnvType.CLIENT)
public class ArtisDisplayVisibilityHandler implements DisplayVisibilityHandler {
    @Override
    public ActionResult handleDisplay(RecipeCategory<?> recipeCategory, RecipeDisplay recipeDisplay) {
        if (recipeDisplay.getRecipeLocation().isPresent() && RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).isPresent()) {
            Recipe recipe = RecipeHelper.getInstance().getRecipeManager().get(recipeDisplay.getRecipeLocation().get()).get();

            if (recipe.getType() instanceof ArtisTableType && recipeDisplay.getRecipeCategory().equals(DefaultPlugin.CRAFTING)) {
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public float getPriority() {
        return 10;
    }
}
