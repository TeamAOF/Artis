package io.github.alloffabric.artis.block;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ArtisTableItem extends BlockItem {
	public ArtisTableItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (context.isAdvanced()) tooltip.add(new TranslatableText("tooltip.artis.source").formatted(Formatting.BLUE, Formatting.ITALIC));
	}
}
