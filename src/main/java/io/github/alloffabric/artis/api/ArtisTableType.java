package io.github.alloffabric.artis.api;

import io.github.alloffabric.artis.recipe.ShapedArtisSerializer;
import io.github.alloffabric.artis.recipe.ShapelessArtisSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArtisTableType implements RecipeType {
    private final Identifier id;
    private final int width;
    private final int height;
    private int color = 0;
    private final boolean blockEntity;
    private final boolean catalystSlot;
    private final boolean includeNormalRecipes;
    private final boolean generateAssets;
    private boolean hasColor = false;
    private final RecipeSerializer shaped;
    private final RecipeSerializer shapeless;

    public ArtisTableType(Identifier id, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets, int color) {
        this(id, width, height, blockEntity, catalystSlot, includeNormalRecipes, makeAssets);
        this.color = 0xFF000000 | color;
        this.hasColor = true;
    }

    public ArtisTableType(Identifier id, int width, int height, boolean blockEntity, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.blockEntity = blockEntity;
        this.catalystSlot = catalystSlot;
        this.includeNormalRecipes = includeNormalRecipes;
        this.generateAssets = makeAssets;
        Identifier shapedId = new Identifier(id.getNamespace(), id.getPath() + "_shaped");
        Identifier shapelessId = new Identifier(id.getNamespace(), id.getPath() + "_shapeless");
        this.shaped = Registry.register(Registry.RECIPE_SERIALIZER, shapedId, new ShapedArtisSerializer(this));
        this.shapeless = Registry.register(Registry.RECIPE_SERIALIZER, shapelessId, new ShapelessArtisSerializer(this));
    }

    public Identifier getId() {
        return id;
    }

    public RecipeSerializer getShaped() {
        return shaped;
    }

    public RecipeSerializer getShapeless() {
        return shapeless;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasBlockEntity() {
        return blockEntity;
    }

    public boolean hasCatalystSlot() {
        return catalystSlot;
    }

    public boolean shouldIncludeNormalRecipes() {
        return includeNormalRecipes;
    }

    public boolean shouldGenerateAssets() {
        return generateAssets;
    }

    public boolean hasColor() {
        return hasColor;
    }

    public int getColor() {
        return color;
    }
}
