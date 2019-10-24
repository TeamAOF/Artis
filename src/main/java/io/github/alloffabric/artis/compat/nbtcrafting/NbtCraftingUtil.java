package io.github.alloffabric.artis.compat.nbtcrafting;

import de.siphalor.nbtcrafting.util.RecipeUtil;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;

import java.util.Optional;

public class NbtCraftingUtil {
	public static ItemStack getOutputStack(ItemStack output, DefaultedList<Ingredient> ingredients, CraftingInventory inv) {
		ItemStack stack = RecipeUtil.getDollarAppliedOutputStack(output, ingredients, inv);
		return Optional.ofNullable(stack).orElse(output);
	}
}
