package io.github.alloffabric.artis.compat.rei;

import com.mojang.blaze3d.platform.GlStateManager;
import me.shedaniel.math.Point;
import me.shedaniel.rei.gui.widget.EntryWidget;
import net.minecraft.client.util.math.MatrixStack;

public class ColorableEntryWidget extends EntryWidget {
    private int color;
    private int x;
    private int y;

    protected ColorableEntryWidget(int x, int y, int color) {
        this(new Point(x, y), color);
    }

    protected ColorableEntryWidget(Point point, int color) {
        super(point);
        this.x = point.getX();
        this.y = point.getY();
        this.color = color;
    }

    public static ColorableEntryWidget create(int x, int y, int color) {
        return create(new Point(x, y), color);
    }

    public static ColorableEntryWidget create(Point point, int color) {
        return new ColorableEntryWidget(point, color);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        GlStateManager.color4f((r + 30) / 255F, (g + 30) / 255F, (b + 30) / 255F, 1.0F);
        super.drawBackground(matrices, mouseX, mouseY, delta);
    }
}
