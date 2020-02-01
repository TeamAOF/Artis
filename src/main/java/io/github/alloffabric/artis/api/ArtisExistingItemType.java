package io.github.alloffabric.artis.api;

import net.minecraft.util.Identifier;

public class ArtisExistingItemType extends ArtisTableType {
	public ArtisExistingItemType(Identifier id, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets) {
		super(id, width, height, catalystSlot, includeNormalRecipes, makeAssets, true);
	}

    public ArtisExistingItemType(Identifier id, int width, int height, boolean catalystSlot, boolean includeNormalRecipes, boolean makeAssets, int color) {
        super(id, width, height, catalystSlot, includeNormalRecipes, makeAssets, true, color);
    }
}
