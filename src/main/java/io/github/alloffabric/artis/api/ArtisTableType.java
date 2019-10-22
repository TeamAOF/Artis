package io.github.alloffabric.artis.api;

import io.github.alloffabric.artis.recipe.ShapedArtisSerializer;
import io.github.alloffabric.artis.recipe.ShapelessArtisSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArtisTableType implements RecipeType {
	private Identifier id;
	private int color = 0;
	private boolean hasColor = false;
	private RecipeSerializer shaped;
	private RecipeSerializer shapeless;

	public ArtisTableType(Identifier id, int color) {
		this(id);
		this.color = 0xFF000000 | color;
		this.hasColor = true;
	}

	//TODO: block settings?
	public ArtisTableType(Identifier id) {
		this.id = id;
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

	public boolean hasColor() {
		return hasColor;
	}

	public int getColor() {
		return color;
	}
}
