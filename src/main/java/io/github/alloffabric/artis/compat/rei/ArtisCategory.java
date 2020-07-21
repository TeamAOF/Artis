package io.github.alloffabric.artis.compat.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.api.RecipeProvider;
import it.unimi.dsi.fastutil.ints.IntList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.TransferRecipeCategory;
import me.shedaniel.rei.api.widgets.Panel;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.*;
import me.shedaniel.rei.impl.ScreenHelper;
import me.shedaniel.rei.server.ContainerInfo;
import me.shedaniel.rei.server.ContainerInfoHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class ArtisCategory<R extends Recipe> implements TransferRecipeCategory<ArtisDisplay> {
	private final ArtisTableType artisTableType;

	ArtisCategory(ArtisTableType artisTableType) {
        this.artisTableType = artisTableType;
    }

	@Override
	public Identifier getIdentifier() {
		return artisTableType.getId();
	}

	@Override
	public String getCategoryName() {
		return I18n.translate("rei.category." + artisTableType.getId().getPath());
	}

    @Override
    public EntryStack getLogo() {
        return EntryStack.create(Registry.BLOCK.get(artisTableType.getId()));
    }

    public static int getSlotWithSize(ArtisDisplay recipeDisplay, int num, int craftingGridWidth) {
        int x = num % recipeDisplay.getDisplay().getWidth();
        int y = (num - x) / recipeDisplay.getDisplay().getWidth();
        return craftingGridWidth * y + x;
    }

	@Override
	public List<Widget> setupDisplay(ArtisDisplay recipeDisplay, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - (getDisplayWidth(recipeDisplay) / 2) + 17, bounds.getCenterY() - (getDisplayHeight() / 2) + 15);

        List<Widget> widgets = new LinkedList(Arrays.asList(Widgets.createRecipeBase(bounds).color(artisTableType.getColor(), artisTableType.getColor())));

        List<List<EntryStack>> input = recipeDisplay.getInputEntries();
        List<ColorableEntryWidget> slots = Lists.newArrayList();

        for (int y = 0; y < artisTableType.getHeight(); y++)
            for (int x = 0; x < artisTableType.getWidth(); x++)
                if (artisTableType.hasColor())
                    slots.add(ColorableEntryWidget.create(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18, artisTableType.getColor()));
                else
                    slots.add(ColorableEntryWidget.create(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18, 0xFFFFFF));
        for (int i = 0; i < input.size(); i++) {
            if (recipeDisplay != null) {
                if (!input.get(i).isEmpty())
                    slots.get(getSlotWithSize(recipeDisplay, i, artisTableType.getWidth())).entries(input.get(i));
            } else if (!input.get(i).isEmpty())
                slots.get(i).entries(input.get(i));
        }

        widgets.addAll(slots);

        if (artisTableType.hasColor()) {
            widgets.add(Widgets.createArrow(new Point(slots.get(slots.size() - 1).getX() + 24, startPoint.y + (getDisplayHeight() / 2) - 23)).disableAnimation());
            widgets.add(ColorableEntryWidget.create(slots.get(slots.size() - 1).getX() + 55, startPoint.y + (getDisplayHeight() / 2) - 22, artisTableType.getColor()).entry(recipeDisplay.getOutputEntries().get(0)));
            if (artisTableType.hasCatalystSlot())
                widgets.add(ColorableEntryWidget.create(slots.get(slots.size() - 1).getX() + 28, startPoint.y + (getDisplayHeight() / 2) - 4, artisTableType.getColor()).entries(Stream.of(recipeDisplay.getCatalyst().getMatchingStacksClient()).map(EntryStack::create).collect(Collectors.toList())));
        } else {
            widgets.add(Widgets.createArrow(new Point(slots.get(slots.size() - 1).getX() + 24, startPoint.y + (getDisplayHeight() / 2) - 23)).disableAnimation());
            //widgets.add(RecipeArrowWidget.create(new Point(slots.get(slots.size() - 1).getX() + 24, startPoint.y + (getDisplayHeight() / 2) - 23), false));
            widgets.add(Widgets.createSlot(new Point(slots.get(slots.size() - 1).getX() + 55, startPoint.y + (getDisplayHeight() / 2) - 22)).entry(recipeDisplay.getOutputEntries().get(0)));
            if (artisTableType.hasCatalystSlot())
                widgets.add(Widgets.createSlot(new Point(slots.get(slots.size() - 1).getX() + 28, startPoint.y + (getDisplayHeight() / 2) - 4)).entries(Stream.of(recipeDisplay.getCatalyst().getMatchingStacksClient()).map(EntryStack::create).collect(Collectors.toList())));
        }

        if (artisTableType.hasCatalystSlot())
            widgets.add(Widgets.createLabel(new Point(slots.get(slots.size() - 1).getX() + 35, startPoint.y + (getDisplayHeight() / 2) + 14), new LiteralText(Formatting.RED + "-" + recipeDisplay.getCatalystCost())).centered());

        return widgets;
	}

    @Override
    public void renderRedSlots(MatrixStack matrices, List<Widget> widgets, Rectangle bounds, ArtisDisplay display, IntList redSlots) {
		ScreenHelper screenHelper = ScreenHelper.getInstance();
	    ContainerInfo<ScreenHandler> info = (ContainerInfo<ScreenHandler>) ContainerInfoHandler.getContainerInfo(getIdentifier(), screenHelper.getPreviousContainerScreen().getScreenHandler().getClass());
        if (info == null)
            return;
        RenderSystem.translatef(0, 0, 400);
        Point startPoint = new Point(bounds.getCenterX() - (getDisplayWidth(display) / 2) + 17, bounds.getCenterY() - (getDisplayHeight() / 2) + 15);
        int width = ((RecipeProvider) screenHelper.getPreviousContainerScreen().getScreenHandler()).getCraftingWidth();
        int catalystSlot = info.getCraftingHeight(screenHelper.getPreviousContainerScreen().getScreenHandler()) - 1;
        for (Integer slot : redSlots) {
            if (catalystSlot == slot) {
                int y = MathHelper.floor(catalystSlot / (float) width) - 1;
                DrawableHelper.fill(matrices, startPoint.x + 11 + width * 18, startPoint.y + 1 + y * 18, startPoint.x + 11 + width * 18 + 16, startPoint.y + 1 + y * 18 + 16, 0x60ff0000);
            } else {
                int i = slot;
                int x = i % width;
                int y = MathHelper.floor(i / (float) width);
                DrawableHelper.fill(matrices, startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18, startPoint.x + 1 + x * 18 + 16, startPoint.y + 1 + y * 18 + 16, 0x60ff0000);
            }
        }
        RenderSystem.translatef(0, 0, -400);
    }

	@Override
	public int getDisplayHeight() {
		return 29 + (artisTableType.getHeight() * 18);
	}

    @Override
    public int getDisplayWidth(ArtisDisplay display) {
        return 90 + (artisTableType.getWidth() * 18);
    }
}