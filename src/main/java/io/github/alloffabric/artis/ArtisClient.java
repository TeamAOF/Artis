package io.github.alloffabric.artis;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.builder.assets.BlockStateBuilder;
import com.swordglowsblue.artifice.api.builder.assets.ModelBuilder;
import com.swordglowsblue.artifice.api.builder.assets.TranslationBuilder;
import com.swordglowsblue.artifice.api.util.Processor;
import io.github.alloffabric.artis.api.ArtisTableType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ArtisClient implements ClientModInitializer {

	public static final Map<Identifier, Processor<BlockStateBuilder>> BLOCKSTATES = new HashMap<>();
	public static final Map<Identifier, Processor<ModelBuilder>> ITEM_MODELS = new HashMap<>();
	public static final Map<Identifier, Processor<TranslationBuilder>> TRANSLATIONS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
			//TODO: lang?
			if (type.shouldMakeModel()) {
				BLOCKSTATES.put(type.getId(), builder -> builder.variant("", variant -> variant.model(new Identifier(Artis.MODID, "block/table" + (type.hasColor()? "_overlay" : "")))));
				ITEM_MODELS.put(type.getId(), builder -> builder.parent(new Identifier(Artis.MODID, "block/table" + (type.hasColor()? "_overlay" : ""))));
			}
			if (type.hasColor()) {
				ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> type.getColor(), Registry.BLOCK.get(type.getId()));
				ColorProviderRegistry.ITEM.register((stack, index) -> type.getColor(), Registry.ITEM.get(type.getId()));
			}
		}
		Artifice.registerAssets(new Identifier(Artis.MODID, "artis_assets"), assets -> {
			for (Identifier id : BLOCKSTATES.keySet()) {
				assets.addBlockState(id, BLOCKSTATES.get(id));
			}
			for (Identifier id : ITEM_MODELS.keySet()) {
				assets.addItemModel(id, ITEM_MODELS.get(id));
			}
		});
	}

}
