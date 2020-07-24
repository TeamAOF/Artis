package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.api.RecipeProvider;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.RecipeFinder;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class RecipeProviderInfoWrapper<T extends ScreenHandler> implements ContainerInfo<T> {
    private final Class<? extends ScreenHandler> containerClass;

    public RecipeProviderInfoWrapper(Class<T> containerClass) {
        this.containerClass = containerClass;
    }

    public static <R extends ScreenHandler> ContainerInfo<R> create(Class<R> containerClass) {
        return new RecipeProviderInfoWrapper(containerClass);
    }

    @Override
    public Class<? extends ScreenHandler> getContainerClass() {
        return containerClass;
    }

    @Override
    public int getCraftingResultSlotIndex(T container) {
        return ((RecipeProvider) container).getCraftingResultSlotIndex();
    }

    public ArtisTableType getTableType(T container) {
        return ((RecipeProvider) container).getTableType();
    }

    @Override
    public int getCraftingWidth(T container) {
        return ((RecipeProvider) container).getCraftingWidth();
    }

    @Override
    public int getCraftingHeight(T container) {
        return ((RecipeProvider) container).getCraftingHeight();
    }

    @Override
    public void clearCraftingSlots(T container) {
        ((RecipeProvider) container).clearCraftingSlots();
    }

    @Override
    public void populateRecipeFinder(T container, RecipeFinder var1) {
        ((RecipeProvider) container).populateRecipeFinder(new net.minecraft.recipe.RecipeFinder() {
            @Override
            public void addNormalItem(ItemStack itemStack_1) {
                var1.addNormalItem(itemStack_1);
            }
        });
    }
}
