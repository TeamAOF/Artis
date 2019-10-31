//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.alloffabric.artis.compat.rei;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.rei.RoughlyEnoughItemsCore;
import me.shedaniel.rei.api.ClientHelper;
import me.shedaniel.rei.api.Renderer;
import me.shedaniel.rei.gui.renderers.FluidRenderer;
import me.shedaniel.rei.gui.renderers.ItemStackRenderer;
import me.shedaniel.rei.gui.widget.EntryListWidget;
import me.shedaniel.rei.gui.widget.SlotWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import me.shedaniel.rei.impl.ScreenHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public class ColorableSlotWidget extends SlotWidget {
    private int color;

    public ColorableSlotWidget(int x, int y, int color, Renderer renderer, boolean drawBackground, boolean showToolTips) {
        this(x, y, color, Collections.singletonList(renderer), drawBackground, showToolTips);
    }

    public ColorableSlotWidget(int x, int y, int color, Renderer renderer, boolean drawBackground, boolean showToolTips, boolean clickToMoreRecipes) {
        this(x, y, color, Collections.singletonList(renderer), drawBackground, showToolTips, clickToMoreRecipes);
    }

    public ColorableSlotWidget(int x, int y, int color, List<Renderer> renderers, boolean drawBackground, boolean showToolTips) {
        super(x, y, renderers, drawBackground, showToolTips);
        this.color = color;
    }

    public ColorableSlotWidget(int x, int y, int color, List<Renderer> itemList, boolean drawBackground, boolean showToolTips, boolean clickToMoreRecipes) {
        super(x, y, itemList, drawBackground, showToolTips, clickToMoreRecipes);
        this.color = color;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        Renderer renderer = this.getCurrentRenderer();
        if (this.isDrawBackground()) {
            GlStateManager.color4f((r + 30) / 255F, (g + 30) / 255F, (b + 30) / 255F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(RECIPE_GUI);
            this.blit(this.x - 1, this.y - 1, 0, 222, 18, 18);
        }

        boolean highlighted = this.containsMouse(mouseX, mouseY);
        if (this.isCurrentRendererItem() && !this.getCurrentItemStack().isEmpty()) {
            renderer.setBlitOffset(200);
            renderer.render(this.x + 8, this.y + 6, mouseX, mouseY, delta);
            if (!this.getCurrentItemStack().isEmpty() && highlighted && this.isShowToolTips()) {
                this.queueTooltip(this.getCurrentItemStack(), delta);
            }
        } else if (this.isCurrentRendererFluid()) {
            renderer.setBlitOffset(200);
            renderer.render(this.x + 8, this.y + 6, mouseX, mouseY, delta);
            if (((FluidRenderer)renderer).getFluid() != null && highlighted && this.isShowToolTips()) {
                this.queueTooltip(((FluidRenderer)renderer).getFluid(), delta);
            }
        } else {
            renderer.setBlitOffset(200);
            renderer.render(this.x + 8, this.y + 6, mouseX, mouseY, delta);
        }

        if (this.isDrawHighlightedBackground() && highlighted) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.colorMask(true, true, true, false);
            int color = -2130706433;
            this.blitOffset = 300;
            this.fillGradient(this.x, this.y, this.x + 16, this.y + 16, color, color);
            this.blitOffset = 0;
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
        }
    }
}
