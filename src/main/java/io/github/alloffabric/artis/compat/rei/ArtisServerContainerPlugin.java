package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.ArtisNormalCraftingController;
import me.shedaniel.rei.plugin.containers.CraftingContainerInfoWrapper;
import me.shedaniel.rei.server.ContainerInfoHandler;
import net.minecraft.container.*;
import net.minecraft.util.Identifier;

public class ArtisServerContainerPlugin implements Runnable {
    @Override
    public void run() {
        for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            ContainerInfoHandler.registerContainerInfo(type.getId(), CraftingContainerInfoWrapper.create(ArtisCraftingController.class));
        }

        ContainerInfoHandler.registerContainerInfo(new Identifier("minecraft", "plugins/crafting"), CraftingContainerInfoWrapper.create(ArtisNormalCraftingController.class));
    }
}