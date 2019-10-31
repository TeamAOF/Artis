package io.github.alloffabric.artis.compat.rei;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;

import io.github.alloffabric.artis.Artis;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.gui.widget.RecipeArrowWidget;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.system.Pointer;

public class ColorableRecipeArrowWidget extends RecipeArrowWidget {
    private int x;
    private int y;
    private int color;
    private boolean animated;

    public ColorableRecipeArrowWidget(int x, int y, int color, boolean animated) {
        super(x, y, animated);

        this.x = x;
        this.y = y;
        this.color = color;
        this.animated = animated;
    }

    public void render(int mouseX, int mouseY, float delta) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        GlStateManager.color4f(r / 255F, g / 255F, b / 255F, 1.0F);
        GuiLighting.disable();
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier(Artis.MODID, "textures/gui/display.png"));
        this.blit(this.x, this.y, 106, 91, 24, 17);
        if (this.animated) {
            int width = MathHelper.ceil((double)(System.currentTimeMillis() / 250L) % 24.0D / 1.0D);
            this.blit(this.x, this.y, 82, 91, width, 17);
        }
    }
}
