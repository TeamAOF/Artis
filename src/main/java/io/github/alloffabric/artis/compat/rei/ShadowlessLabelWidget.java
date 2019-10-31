package io.github.alloffabric.artis.compat.rei;

import java.util.Collections;
import java.util.List;
import me.shedaniel.math.api.Rectangle;
import me.shedaniel.rei.gui.widget.WidgetWithBounds;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;

public class ShadowlessLabelWidget extends WidgetWithBounds {
    public int x;
    public int y;
    public String text;

    public ShadowlessLabelWidget(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public Rectangle getBounds() {
        int width = this.font.getStringWidth(this.text);
        return new Rectangle(this.x - width / 2 - 1, this.y - 5, width + 2, 14);
    }

    public List<? extends Element> children() {
        return Collections.emptyList();
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.drawCenteredStringShadowless(this.font, this.text, this.x, this.y, -1);
    }

    public void drawCenteredStringShadowless(TextRenderer textRenderer_1, String string_1, int int_1, int int_2, int int_3) {
        textRenderer_1.draw(string_1, (float)(int_1 - textRenderer_1.getStringWidth(string_1) / 2), (float)int_2, int_3);
    }
}
