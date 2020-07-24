package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.server.ContainerInfoHandler;

public class ArtisServerContainerPlugin implements Runnable {
    @Override
    public void run() {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            ContainerInfoHandler.registerContainerInfo(type.getId(), RecipeProviderInfoWrapper.create(ArtisCraftingController.class));
        }

        ContainerInfoHandler.registerContainerInfo(DefaultPlugin.CRAFTING, RecipeProviderInfoWrapper.create(ArtisCraftingController.class));
    }
}