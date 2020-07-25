package io.github.alloffabric.artis.compat.kubejs;

import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
public class ArtisJsonRegistryEventJS extends EventJS {
	public ArtisJsonBuilder create(String name) {
		ArtisJsonBuilder builder = new ArtisJsonBuilder(name);
		return builder;
	}

	public ArtisJsonBuilder createExistingBlock(String modId, String name) {
		ArtisJsonBuilder builder = new ArtisJsonBuilder(modId, name);
		builder = builder.setType("existing_block");
		return builder;
	}

	public ArtisJsonBuilder createExistingItem(String modId, String name) {
		ArtisJsonBuilder builder = new ArtisJsonBuilder(modId, name);
		builder = builder.setType("existing_item");
		return builder;
	}
}