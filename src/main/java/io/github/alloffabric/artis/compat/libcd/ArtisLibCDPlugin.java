package io.github.alloffabric.artis.compat.libcd;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.ArtisData;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.event.ArtisEvents;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import net.minecraft.util.Identifier;

public class ArtisLibCDPlugin implements LibCDInitializer {
	@Override
	public void initTweakers(TweakerManager manager) {
		if (!Artis.isLoaded) {
			ArtisData.loadData();
			ArtisData.loadConfig();
			ArtisEvents.init();
			Artis.isLoaded = true;
		}

		for (Identifier id : Artis.ARTIS_TABLE_TYPES.getIds()) {
			ArtisTableType type = Artis.ARTIS_TABLE_TYPES.get(id);
			manager.addAssistant("artis.ArtisTweaker@" + id.toString(), new ArtisTweaker(type));
		}

	}

	@Override
	public void initConditions(ConditionManager conditionManager) {

	}
}
