package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.block.ArtisTableBlock;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ArtisREIPlugin implements REIPluginV0 {
    public static final Identifier PLUGIN = new Identifier(Artis.MODID, "artis_plugin");

    public static final Map<ArtisTableType, ItemConvertible> iconMap = new HashMap<>();

    public ArtisREIPlugin() {
        for (ArtisTableBlock block : Artis.ARTIS_TABLE_BLOCKS) {
            iconMap.put(block.getType(), block);
        }
    }

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            recipeHelper.registerCategory(new ArtisCategory<>(type));
        }
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            recipeHelper.registerRecipes(type.getId(), (Predicate<Recipe>) recipe -> recipe.getType() == type,
                    (Function<ArtisCraftingRecipe, RecipeDisplay>) recipe -> new ArtisDisplay(recipe, type));
        }
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipeVisibilityHandler(new ArtisDisplayVisibilityHandler());

        recipeHelper.registerAutoCraftingHandler(new ArtisCategoryHandler());

        for (ArtisTableBlock block : Artis.ARTIS_TABLE_BLOCKS) {
            recipeHelper.registerWorkingStations(block.getType().getId(), EntryStack.create(block.asItem()));
        }

        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            if (type instanceof ArtisExistingBlockType) {
                Block block = Registry.BLOCK.get(type.getId());
                recipeHelper.registerWorkingStations(type.getId(), EntryStack.create(block.asItem()));

                if (type.shouldIncludeNormalRecipes()) {
                    recipeHelper.registerWorkingStations(new Identifier("minecraft", "plugins/crafting"), EntryStack.create(block.asItem()));
                }
            } else if (type instanceof ArtisExistingItemType) {
                Item item = Registry.ITEM.get(type.getId());
                recipeHelper.registerWorkingStations(type.getId(), EntryStack.create(item));

                if (type.shouldIncludeNormalRecipes()) {
                    recipeHelper.registerWorkingStations(new Identifier("minecraft", "plugins/crafting"), EntryStack.create(item));
                }
            }
        }
    }
}
