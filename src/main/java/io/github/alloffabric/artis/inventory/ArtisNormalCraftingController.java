package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.ArtisClient;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.packet.ContainerSlotUpdateS2CPacket;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

/*
 * A special type of the ArtisCraftingController used for tables with support for vanilla crafting.
 */
public class ArtisNormalCraftingController extends ArtisCraftingController {
	public ArtisNormalCraftingController(ArtisTableType type, int syncId, PlayerEntity player, BlockContext context) {
		super(type, syncId, player, context);
	}
}
