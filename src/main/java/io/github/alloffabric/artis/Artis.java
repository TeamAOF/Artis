package io.github.alloffabric.artis;

import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Artis implements ModInitializer {
	public static final String MODID = "starter";

	public static final Logger logger = LogManager.getLogger();

	private final Registry<ArtisTableType> ARTIS_TABLE_TYPES = new SimpleRegistry<>();

	@Override
	public void onInitialize() {

	}


}
