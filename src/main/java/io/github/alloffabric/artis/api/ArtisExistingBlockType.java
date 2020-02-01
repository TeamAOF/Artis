package io.github.alloffabric.artis.api;

import net.minecraft.util.Identifier;

public class ArtisExistingBlockType extends ArtisTableType {
	public ArtisExistingBlockType(Identifier id, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets) {
		super(id, width, height, catalystSlot, includeNormalRecipes, makeAssets, true);
	}

    public ArtisExistingBlockType(Identifier id, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets, int color) {
        super(id, width, height, catalystSlot, includeNormalRecipes, makeAssets, true, color);
    }
}
