package io.github.alloffabric.artis.recipe;

import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class ShapedArtisSerializer implements RecipeSerializer<ShapedArtisRecipe> {

	@Override
	public ShapedArtisRecipe read(Identifier identifier, JsonObject jsonObject) {
		return null;
	}

	@Override
	public ShapedArtisRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
		return null;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, ShapedArtisRecipe shapedArtisRecipe) {

	}
}
