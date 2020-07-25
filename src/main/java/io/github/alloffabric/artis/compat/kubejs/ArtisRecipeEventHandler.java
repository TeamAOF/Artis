package io.github.alloffabric.artis.compat.kubejs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSInitializer;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArtisRecipeEventHandler implements KubeJSInitializer {
	@Override
	public void onKubeJSInitialization() {
		RegisterRecipeHandlersEvent.EVENT.register(event -> {
			for (ArtisTableType tableType : Artis.ARTIS_TABLE_TYPES) {
				event.register(new RecipeTypeJS(tableType.getShaped(), ArtisShapedRecipeJS::new));
				event.register(new RecipeTypeJS(tableType.getShapeless(), ArtisShapelessRecipeJS::new));
			}
		});
	}

	public class ArtisShapedRecipeJS extends RecipeJS {
		private final List<String> pattern = new ArrayList<>();
		private final List<String> key = new ArrayList<>();
		public IngredientJS catalystItem = EmptyItemStackJS.INSTANCE;
		public int cost = 1;

		@Override
		public void create(ListJS args) {
			if (args.size() < 3) {
				if (args.size() < 2) {
					throw new RecipeExceptionJS("Shaped recipe requires 3 arguments - result, pattern and keys!");
				}
				// simple define
				ItemStackJS result = ItemStackJS.of(args.get(0));
				if (result.isEmpty()) {
					throw new RecipeExceptionJS("Shaped recipe result " + args.get(0) + " is not a valid item!");
				}
				outputItems.add(result);
				ListJS vertical = ListJS.orSelf(args.get(1));
				if (vertical.isEmpty()) {
					throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
				}
				int id = 0;
				for (Object o : vertical) {
					StringBuilder horizontalPattern = new StringBuilder();
					ListJS horizontal = ListJS.orSelf(o);
					for (Object item : horizontal) {
						IngredientJS ingredient = IngredientJS.of(item);

						if (!ingredient.isEmpty()) {
							String currentId = String.valueOf(id++);
							horizontalPattern.append(currentId);
							inputItems.add(ingredient);
							key.add(currentId);
						} else {
							horizontalPattern.append(" ");
						}
					}
					pattern.add(horizontalPattern.toString());
				}
				return;
			}

			ItemStackJS result = ItemStackJS.of(args.get(0));

			if (result.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe result " + args.get(0) + " is not a valid item!");
			}

			outputItems.add(result);

			ListJS pattern1 = ListJS.orSelf(args.get(1));

			if (pattern1.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
			}

			for (Object p : pattern1) {
				pattern.add(String.valueOf(p));
			}

			MapJS key1 = MapJS.of(args.get(2));

			if (key1 == null || key1.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe key map is empty!");
			}

			for (String k : key1.keySet()) {
				IngredientJS i = IngredientJS.of(key1.get(k));

				if (!i.isEmpty()) {
					inputItems.add(i);
					key.add(k);
				} else {
					throw new RecipeExceptionJS("Shaped recipe ingredient " + key1.get(k) + " with key '" + k + "' is not a valid ingredient!");
				}
			}

			if (args.size() > 3) {
				IngredientJS catalyst = IngredientJS.of(args.get(3));

				if (!catalyst.isEmpty()) {
					catalystItem = catalyst;
				}
			}

			if (args.size() > 4 && args.get(4) instanceof Integer) {
				cost = (Integer) args.get(4);
			}
		}

		@Override
		public void deserialize() {
			ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

			if (result.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe result " + json.get("result") + " is not a valid item!");
			}

			outputItems.add(result);

			for (JsonElement e : json.get("pattern").getAsJsonArray()) {
				pattern.add(e.getAsString());
			}

			if (pattern.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe pattern is empty!");
			}

			for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet()) {
				IngredientJS i = IngredientJS.ingredientFromRecipeJson(entry.getValue());

				if (!i.isEmpty()) {
					inputItems.add(i);
					key.add(entry.getKey());
				} else {
					throw new RecipeExceptionJS("Shaped recipe ingredient " + entry.getValue() + " with key '" + entry.getKey() + "' is not a valid ingredient!");
				}
			}

			if (key.isEmpty()) {
				throw new RecipeExceptionJS("Shaped recipe key map is empty!");
			}

			IngredientJS catalyst = IngredientJS.ingredientFromRecipeJson(json.get("catalyst"));

			if (!catalyst.equals(EmptyItemStackJS.INSTANCE) && !catalyst.isEmpty()) {
				catalystItem = catalyst;
			}

			if (!json.get("cost").isJsonNull()) {
				cost = json.get("cost").getAsInt();
			}
		}

		@Override
		public void serialize() {
			JsonArray patternJson = new JsonArray();

			for (String s : pattern) {
				patternJson.add(s);
			}

			json.add("pattern", patternJson);

			JsonObject keyJson = new JsonObject();

			for (int i = 0; i < key.size(); i++) {
				keyJson.add(key.get(i), inputItems.get(i).toJson());
			}

			json.add("key", keyJson);
			json.add("result", outputItems.get(0).toResultJson());
			if (!catalystItem.equals(EmptyItemStackJS.INSTANCE))
				json.add("catalyst", catalystItem.toJson());
			json.addProperty("cost", cost);
		}
	}

	public class ArtisShapelessRecipeJS extends RecipeJS {
		public IngredientJS catalystItem = EmptyItemStackJS.INSTANCE;
		public int cost = 0;

		public final boolean hasCatalyst(IngredientJS ingredient, boolean exact) {
			if (exact ? catalystItem.equals(ingredient) : catalystItem.anyStackMatches(ingredient)) {
				return true;
			}
			return false;
		}

		@Override
		public void create(ListJS args) {
			ItemStackJS result = ItemStackJS.of(args.get(0));

			if (result.isEmpty()) {
				throw new RecipeExceptionJS("Shapeless recipe result " + args.get(0) + " is not a valid item!");
			}

			outputItems.add(result);

			ListJS ingredients1 = ListJS.orSelf(args.get(1));

			if (ingredients1.isEmpty()) {
				throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
			}

			for (Object o : ingredients1) {
				IngredientJS in = IngredientJS.of(o);

				if (!in.isEmpty()) {
					inputItems.add(in);
				} else {
					throw new RecipeExceptionJS("Shapeless recipe ingredient " + o + " is not a valid ingredient!");
				}
			}

			if (inputItems.isEmpty()) {
				throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
			}

			if (args.size() > 2) {
				IngredientJS catalyst = IngredientJS.of(args.get(2));

				if (!catalyst.isEmpty()) {
					catalystItem = catalyst;
				}
			}

			if (args.size() > 3 && args.get(3) instanceof Integer) {
				cost = (Integer) args.get(3);
			}
		}

		@Override
		public void deserialize() {
			ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

			if (result.isEmpty()) {
				throw new RecipeExceptionJS("Shapeless recipe result " + json.get("result") + " is not a valid item!");
			}

			outputItems.add(result);

			for (JsonElement e : json.get("ingredients").getAsJsonArray()) {
				IngredientJS in = IngredientJS.ingredientFromRecipeJson(e);

				if (!in.isEmpty()) {
					inputItems.add(in);
				} else {
					throw new RecipeExceptionJS("Shapeless recipe ingredient " + e + " is not a valid ingredient!");
				}
			}

			if (inputItems.isEmpty()) {
				throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
			}

			IngredientJS catalyst = IngredientJS.ingredientFromRecipeJson(json.get("catalyst"));

			if (!catalyst.equals(EmptyItemStackJS.INSTANCE) && !catalyst.isEmpty()) {
				catalystItem = catalyst;
			}

			if (!json.get("cost").isJsonNull()) {
				cost = json.get("cost").getAsInt();
			}
		}

		@Override
		public void serialize() {
			JsonArray ingredientsJson = new JsonArray();

			for (IngredientJS in : inputItems) {
				ingredientsJson.add(in.toJson());
			}

			json.add("ingredients", ingredientsJson);
			json.add("result", outputItems.get(0).toResultJson());
			if (!catalystItem.equals(EmptyItemStackJS.INSTANCE))
				json.add("catalyst", catalystItem.toJson());
			json.addProperty("cost", cost);
		}
	}
}
