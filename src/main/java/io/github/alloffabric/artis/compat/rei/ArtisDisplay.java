package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class ArtisDisplay implements DefaultCraftingDisplay {
    private ArtisCraftingRecipe display;
    private Ingredient catalyst;
    private int catalystCost;
    private List<List<EntryStack>> input;
    private List<EntryStack> output;

    public ArtisDisplay(ArtisCraftingRecipe recipe) {
        this.display = recipe;
        this.input = recipe.getPreviewInputs().stream().map(i -> {
            List<EntryStack> entries = new ArrayList<>();
            for (ItemStack stack : i.getMatchingStacksClient()) {
                entries.add(EntryStack.create(stack));
            }
            return entries;
        }).collect(Collectors.toList());
        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
        this.catalyst = recipe.getCatalyst();
        this.catalystCost = recipe.getCatalystCost();
    }

    @Override
    public Identifier getRecipeCategory() {
        return ((ArtisTableType) display.getType()).getId();
    }

    @Override
    public Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(display).map(CraftingRecipe::getId);
    }

    @Override
    public Optional<Recipe<?>> getOptionalRecipe() {
        return Optional.ofNullable(this.display);
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return this.input;
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return this.output;
    }

    @Override
    public List<List<EntryStack>> getRequiredEntries() {
        return this.input;
    }

    @Override
    public int getWidth() {
        return display.getWidth();
    }

    @Override
    public int getHeight() {
        return display.getHeight();
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public int getCatalystCost() {
        return catalystCost;
    }
}
