package io.github.alloffabric.artis.api;

import io.github.alloffabric.artis.recipe.ShapedArtisSerializer;
import io.github.alloffabric.artis.recipe.ShapelessArtisSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArtisTableType implements RecipeType {
	private Identifier id;
	private RecipeSerializer shaped;
	private RecipeSerializer shapeless;

	public ArtisTableType(Identifier id) {
		this.id = id;
		Identifier shapedId = new Identifier(id.getNamespace(), id.getPath() + "_shaped");
		Identifier shapelessId = new Identifier(id.getNamespace(), id.getPath() + "_shapeless");
		this.shaped = Registry.register(Registry.RECIPE_SERIALIZER, shapedId, new ShapedArtisSerializer());
		this.shapeless = Registry.register(Registry.RECIPE_SERIALIZER, shapelessId, new ShapelessArtisSerializer());
	}
}
