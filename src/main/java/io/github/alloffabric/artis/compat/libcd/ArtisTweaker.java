package io.github.alloffabric.artis.compat.libcd;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.cottonmc.libcd.tweaker.Tweaker;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArtisTweaker {
	public static final ArtisTweaker INSTANCE = new ArtisTweaker();
	private Map<Identifier, TableTweaker> tweakers = new HashMap<>();

	public static void init() {
		for (Identifier id : Artis.ARTIS_TABLE_TYPES.getIds()) {
			ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(id);
			INSTANCE.tweakers.put(id, new TableTweaker(type));
		}

		Tweaker.addAssistant("ArtisTweaker", INSTANCE);
	}

	public TableTweaker get(String id) {
		return tweakers.get(new Identifier(id));
	}
}
