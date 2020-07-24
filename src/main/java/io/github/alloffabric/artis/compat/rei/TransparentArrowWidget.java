package io.github.alloffabric.artis.compat.rei;

import io.github.alloffabric.artis.Artis;
import me.shedaniel.math.Dimension;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.widgets.Arrow;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TransparentArrowWidget extends Arrow {
    @NotNull
    private final Rectangle bounds;
    private double animationDuration = -1;

    public TransparentArrowWidget(@NotNull me.shedaniel.math.Rectangle bounds) {
        this.bounds = new Rectangle(Objects.requireNonNull(bounds));
    }

    @NotNull
    public static Arrow create(@NotNull Point point) {
        return new TransparentArrowWidget(new Rectangle(point, new Dimension(24, 17)));
    }

    @Override
    public double getAnimationDuration() {
        return animationDuration;
    }

    @Override
    public void setAnimationDuration(double animationDurationMS) {
        this.animationDuration = animationDurationMS;
        if (this.animationDuration <= 0)
            this.animationDuration = -1;
    }

    @NotNull
    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier(Artis.MODID, "textures/gui/arrow.png"));
        drawTexture(matrices, getX(), getY(), 24, 0, 24, 17, 48, 17);
        if (getAnimationDuration() > 0) {
            int width = MathHelper.ceil((System.currentTimeMillis() / (animationDuration / 24) % 24d) / 1f);
            drawTexture(matrices, getX(), getY(), 0, 0, width, 17, 48, 17);
        }
        MinecraftClient.getInstance().getTextureManager().bindTexture(DefaultPlugin.getDisplayTexture());
    }

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}
