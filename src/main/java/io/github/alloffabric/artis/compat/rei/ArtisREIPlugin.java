package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.util.Identifier;

public class ArtisREIPlugin implements REIPluginV0 {
	@Override
	public SemanticVersion getMinimumVersion() throws VersionParsingException {
		return SemanticVersion.parse("3.0-pre");
	}

	@Override
	public void registerPluginCategories(RecipeHelper recipeHelper) {

	}

	@Override
	public void registerRecipeDisplays(RecipeHelper recipeHelper) {

	}

	@Override
	public void registerOthers(RecipeHelper recipeHelper) {

	}

	@Override
	public Identifier getPluginIdentifier() {
		return new Identifier(Artis.MODID, "rei_plugin");
	}
}
