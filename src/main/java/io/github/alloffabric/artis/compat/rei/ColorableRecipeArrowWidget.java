package io.github.alloffabric.artis.compat.rei;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.alloffabric.artis.Artis;
import me.shedaniel.rei.gui.widget.RecipeArrowWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ColorableRecipeArrowWidget extends RecipeArrowWidget {
    private int x;
    private int y;
    private double time = 250d;
    private int color;
    private boolean animated;

    public ColorableRecipeArrowWidget(int x, int y, int color, boolean animated) {
        super(x, y, animated);

        this.x = x;
        this.y = y;
        this.color = color;
        this.animated = animated;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public boolean isAnimated() {
        return animated;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        GlStateManager.color4f(r / 255F, g / 255F, b / 255F, 1.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier(Artis.MODID, "textures/gui/display.png"));
        drawTexture(matrices, x, y, 106, 91, 24, 17);
        if (animated) {
            int width = MathHelper.ceil((System.currentTimeMillis() / (time / 24) % 24d) / 1f);
            drawTexture(matrices, x, y, 82, 91, width, 17);
        }
    }
}
