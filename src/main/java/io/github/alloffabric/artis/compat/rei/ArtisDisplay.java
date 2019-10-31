package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.recipe.ShapedArtisRecipe;
import io.github.alloffabric.artis.recipe.ShapelessArtisRecipe;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArtisDisplay implements DefaultCraftingDisplay {
    private Recipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;
    private Ingredient catalyst;
    private int catalystCost;

    public ArtisDisplay(Recipe recipe) {
        if (recipe instanceof CraftingRecipe && recipe instanceof ArtisCraftingRecipe) {
            this.display = recipe;
            this.input = ((CraftingRecipe)recipe).getPreviewInputs().stream().map(i -> Arrays.asList(i.getStackArray())).collect(Collectors.toList());
            this.output = Collections.singletonList(recipe.getOutput());
            this.catalyst = ((ArtisCraftingRecipe)recipe).getCatalyst();
            this.catalystCost = ((ArtisCraftingRecipe)recipe).getCatalystCost();
        }
    }

    @Override
    public Identifier getRecipeCategory() {
        return ((ArtisTableType) display.getType()).getId();
    }

    @Override
    public Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(display).map(Recipe::getId);
    }

    @Override
    public List<List<ItemStack>> getInput() {
        return input;
    }

    @Override
    public List<ItemStack> getOutput() {
        return output;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public int getCatalystCost() {
        return catalystCost;
    }

    @Override
    public List<List<ItemStack>> getRequiredItems() {
        return input;
    }

    @Override
    public int getHeight() {
        return ((ArtisTableType) display.getType()).getHeight();
    }

    @Override
    public Optional<Recipe<?>> getOptionalRecipe() {
        return Optional.ofNullable(display);
    }

    @Override
    public int getWidth() {
        return ((ArtisTableType) display.getType()).getWidth();
    }
}
