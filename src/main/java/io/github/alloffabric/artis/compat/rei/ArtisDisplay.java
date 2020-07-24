package io.github.alloffabric.artis.compat.rei;

import com.google.common.collect.Lists;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.server.ContainerInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ArtisDisplay implements DefaultCraftingDisplay {
    private final ArtisCraftingRecipe display;
    private final ArtisTableType type;
    private final Ingredient catalyst;
    private final int catalystCost;
    private final List<List<EntryStack>> input;
    private final List<EntryStack> output;

    public ArtisDisplay(ArtisCraftingRecipe recipe, ArtisTableType type) {
        this.display = recipe;
        this.type = type;
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

    public ArtisCraftingRecipe getDisplay() {
        return display;
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

    @Override
    public List<List<EntryStack>> getOrganisedInputEntries(ContainerInfo<ScreenHandler> containerInfo, ScreenHandler container) {
        List<List<EntryStack>> entries = DefaultCraftingDisplay.super.getOrganisedInputEntries(containerInfo, container);
        if (type.hasCatalystSlot()) {
            List<List<EntryStack>> out = Lists.newArrayListWithCapacity(entries.size() + 1);
            out.add(Stream.of(catalyst.getMatchingStacksClient()).map(EntryStack::create).collect(Collectors.toList()));
            out.addAll(entries);

            return out;
        }
        return entries;
    }
}
