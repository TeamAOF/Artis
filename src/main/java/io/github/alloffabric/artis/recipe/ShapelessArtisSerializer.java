package io.github.alloffabric.artis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class ShapelessArtisSerializer implements RecipeSerializer<ShapelessArtisRecipe> {
	private RecipeType type;

	public ShapelessArtisSerializer(RecipeType type) {
		this.type = type;
	}

	@Override
	public ShapelessArtisRecipe read(Identifier id, JsonObject jsonObject) {
		String group = JsonHelper.getString(jsonObject, "group", "");
		DefaultedList<Ingredient> ingredients = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
		if (ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		} else if (ingredients.size() > 9) {
			throw new JsonParseException("Too many ingredients for shapeless recipe");
		} else {
			ItemStack output = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
			Ingredient catalyst = Ingredient.fromJson(jsonObject.get("catalyst"));
			int cost = JsonHelper.getInt(jsonObject, "cost");
			return new ShapelessArtisRecipe(type, this, id, group, ingredients, output, catalyst, cost);
		}
	}

	@Override
	public ShapelessArtisRecipe read(Identifier id, PacketByteBuf buf) {
		String group = buf.readString(32767);
		int size = buf.readVarInt();
		DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);

		for(int i = 0; i < ingredients.size(); ++i) {
			ingredients.set(i, Ingredient.fromPacket(buf));
		}

		ItemStack output = buf.readItemStack();

		Ingredient catalyst = Ingredient.fromPacket(buf);
		int cost = buf.readInt();

		return new ShapelessArtisRecipe(type, this, id, group, ingredients, output, catalyst, cost);
	}

	@Override
	public void write(PacketByteBuf buf, ShapelessArtisRecipe recipe) {
		buf.writeString(recipe.getGroup());
		buf.writeVarInt(recipe.getPreviewInputs().size());

		for (Ingredient ingredient : recipe.getPreviewInputs()) {
			ingredient.write(buf);
		}

		buf.writeItemStack(recipe.getOutput());

		recipe.getCatalyst().write(buf);
		buf.writeInt(recipe.getCatalystCost());
	}

	private static DefaultedList<Ingredient> getIngredients(JsonArray array) {
		DefaultedList<Ingredient> ingredients = DefaultedList.of();

		for(int i = 0; i < array.size(); ++i) {
			Ingredient ingredient = Ingredient.fromJson(array.get(i));
			if (!ingredient.isEmpty()) {
				ingredients.add(ingredient);
			}
		}

		return ingredients;
	}

}
