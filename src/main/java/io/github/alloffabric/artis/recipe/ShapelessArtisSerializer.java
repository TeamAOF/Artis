package io.github.alloffabric.artis.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ShapelessArtisSerializer implements RecipeSerializer<ShapelessArtisRecipe> {
	@Override
	public ShapelessArtisRecipe read(Identifier identifier, JsonObject jsonObject) {
		return null;
	}

	@Override
	public ShapelessArtisRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
		return null;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, ShapelessArtisRecipe shapelessArtisRecipe) {

	}
}
