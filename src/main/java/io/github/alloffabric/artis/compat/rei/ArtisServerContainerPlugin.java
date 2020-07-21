package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.ArtisNormalCraftingController;
import me.shedaniel.rei.plugin.containers.CraftingContainerInfoWrapper;
import me.shedaniel.rei.server.ContainerInfoHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public class ArtisServerContainerPlugin implements Runnable {
    @Override
    public void run() {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            ContainerInfoHandler.registerContainerInfo(type.getId(), new RecipeProviderInfoWrapper<ArtisCraftingController>(ArtisCraftingController.class) {
                @Override
                public int getCraftingWidth(ArtisCraftingController container) {
                    return 1;
                }

                @Override
                public int getCraftingHeight(ArtisCraftingController container) {
                    return container.getCraftingWidth() * container.getCraftingHeight() + 1;
                }
            });
        }

        ContainerInfoHandler.registerContainerInfo(new Identifier("minecraft", "plugins/crafting"), RecipeProviderInfoWrapper.create(ArtisNormalCraftingController.class));
    }
}