package io.github.alloffabric.artis.block;

import io.github.alloffabric.artis.ArtisClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArtisTableItem extends BlockItem {
	private Identifier tableId;
	public ArtisTableItem(ArtisTableBlock block, Settings settings) {
		super(block, settings);
		this.tableId = block.getType().getId();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (context.isAdvanced()) tooltip.add(new TranslatableText("tooltip.artis.source").formatted(Formatting.BLUE, Formatting.ITALIC));
	}

	@Override
	public Text getName(ItemStack stack) {
		return ArtisClient.getName(tableId);
	}

	@Override
	public Text getName() {
		return ArtisClient.getName(tableId);
	}
}
