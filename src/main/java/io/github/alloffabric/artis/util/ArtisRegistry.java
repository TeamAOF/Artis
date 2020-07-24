package io.github.alloffabric.artis.util;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * Exists *purely* so we have access to a `containsId()` method on the server. Thaaaaaanks, ProGuard.
 */
public class ArtisRegistry<T> extends SimpleRegistry<T> {
    public ArtisRegistry(RegistryKey<Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    public boolean hasId(Identifier id) {
        return this.entriesById.containsKey(id);
    }
}
