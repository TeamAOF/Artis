package io.github.alloffabric.artis.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * Exists *purely* so we have access to a `containsId()` method on the server. Thaaaaaanks, ProGuard.
 */
public class ArtisRegistry<T> extends SimpleRegistry<T> {
	public boolean hasId(Identifier id) {
		return this.entries.containsKey(id);
	}
}
