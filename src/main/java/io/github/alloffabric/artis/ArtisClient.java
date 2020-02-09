package io.github.alloffabric.artis;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.builder.assets.BlockStateBuilder;
import com.swordglowsblue.artifice.api.builder.assets.ModelBuilder;
import com.swordglowsblue.artifice.api.util.Processor;
import io.github.alloffabric.artis.api.ArtisExistingBlockType;
import io.github.alloffabric.artis.api.ArtisExistingItemType;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.ArtisCraftingScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ArtisClient implements ClientModInitializer {

	public static final Map<Identifier, Processor<BlockStateBuilder>> BLOCKSTATES = new HashMap<>();
	public static final Map<Identifier, Processor<ModelBuilder>> ITEM_MODELS = new HashMap<>();

	@Override
	public void onInitializeClient() {
		for (ArtisTableType type : Artis.ARTIS_TABLE_TYPES) {
            ScreenProviderRegistry.INSTANCE.registerFactory(type.getId(), controller -> new ArtisCraftingScreen((ArtisCraftingController) controller, ((ArtisCraftingController) controller).getPlayer()));
		    if (!(type instanceof ArtisExistingBlockType) && !(type instanceof ArtisExistingItemType)) {
                if (type.shouldGenerateAssets()) {
                    BLOCKSTATES.put(type.getId(), builder -> builder.variant("", variant -> variant.model(new Identifier(Artis.MODID, "block/table" + (type.hasColor() ? "_overlay":"")))));
                    ITEM_MODELS.put(type.getId(), builder -> builder.parent(new Identifier(Artis.MODID, "block/table" + (type.hasColor() ? "_overlay":""))));
                }
                if (type.hasColor()) {
                    ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> type.getColor(), Registry.BLOCK.get(type.getId()));
                    ColorProviderRegistry.ITEM.register((stack, index) -> type.getColor(), Registry.ITEM.get(type.getId()));
                }
                BlockRenderLayerMap.INSTANCE.putBlock(Registry.BLOCK.get(type.getId()), RenderLayer.getCutout());
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

	public static Text getName(Identifier id) {
		String key = "block." + id.getNamespace() + "." + id.getPath();
		if (Language.getInstance().hasTranslation(key)) {
            return new TranslatableText(key);
        } else {
			String[] split = id.getPath().split("_");
			StringBuilder builder = new StringBuilder();
			for (String string : split) {
				builder.append(string.substring(0, 1).toUpperCase());
				builder.append(string.substring(1));
				builder.append(" ");
			}
			return new LiteralText(builder.toString().trim());
		}
	}

}
