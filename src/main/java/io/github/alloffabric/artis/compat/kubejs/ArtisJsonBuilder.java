package io.github.alloffabric.artis.compat.kubejs;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.BuilderBase;
import net.minecraft.util.Identifier;

public class ArtisJsonBuilder extends BuilderBase {
    public JsonObject root;
    public JsonObject tables;
    public JsonObject table;
    public JsonObject settings;

    public ArtisJsonBuilder(String i) {
        super(i);
        root = new JsonObject();
        tables = new JsonObject();
        table = new JsonObject();
        settings = new JsonObject();
        root.add("tables", tables);
        tables.add(KubeJS.appendModId(i), table);
        table.add("settings", settings);
    }

    public ArtisJsonBuilder(String modId, String name) {
        super(name);
        root = new JsonObject();
        tables = new JsonObject();
        table = new JsonObject();
        settings = new JsonObject();
        root.add("tables", tables);
        tables.add(new Identifier(modId, name).toString(), table);
        table.add("settings", settings);
    }

    public ArtisJsonBuilder setType(String type) {
        table.addProperty("type", type);
        return this;
    }

    public ArtisJsonBuilder dimensions(int width, int height) {
        table.addProperty("width", width);
        table.addProperty("height", height);
        return this;
    }

    public ArtisJsonBuilder generateAssets() {
        table.addProperty("generate_assets", true);
        return this;
    }

    public ArtisJsonBuilder color(String color) {
        table.addProperty("color", color);
        return this;
    }

    public ArtisJsonBuilder blockEntity() {
        table.addProperty("block_entity", true);
        return this;
    }

    public ArtisJsonBuilder catalystSlots(int amount) {
        table.addProperty("catalyst_slot", amount);
        return this;
    }

    public ArtisJsonBuilder normalRecipes() {
        table.addProperty("normal_recipes", true);
        return this;
    }

    public ArtisJsonBuilder bypassCheck() {
        table.addProperty("bypass_check", true);
        return this;
    }

    public ArtisJsonBuilder copy(String original) {
        settings.addProperty("copy", original);
        return this;
    }

    public ArtisJsonBuilder material(String material) {
        settings.addProperty("material", material);
        return this;
    }

    public ArtisJsonBuilder materialColor(String material) {
        settings.addProperty("material_color", material);
        return this;
    }

    public ArtisJsonBuilder sounds(String sounds) {
        settings.addProperty("sounds", sounds);
        return this;
    }

    public ArtisJsonBuilder collidable(boolean value) {
        settings.addProperty("collidable", value);
        return this;
    }

    public ArtisJsonBuilder breakByHand(boolean value) {
        settings.addProperty("break_by_hand", value);
        return this;
    }

    // Currently no breakByTool, will add later

    public ArtisJsonBuilder nonOpaque() {
        settings.addProperty("non_opaque", true);
        return this;
    }

    public ArtisJsonBuilder lightLevel(int level) {
        settings.addProperty("light_level", level);
        return this;
    }

    public ArtisJsonBuilder hardness(float hardness) {
        settings.addProperty("hardness", hardness);
        return this;
    }

    public ArtisJsonBuilder resistance(float resistance) {
        settings.addProperty("resistance", resistance);
        return this;
    }

    public ArtisJsonBuilder slipperiness(float slipperiness) {
        settings.addProperty("slipperiness", slipperiness);
        return this;
    }

    public ArtisJsonBuilder breakInstantly() {
        settings.addProperty("break_instantly", true);
        return this;
    }

    public ArtisJsonBuilder dropsNothing() {
        settings.addProperty("drops_nothing", true);
        return this;
    }

    public ArtisJsonBuilder dropsLike(String value) {
        settings.addProperty("drops_like", value);
        return this;
    }

    public ArtisJsonBuilder drops(String value) {
        settings.addProperty("drops", value);
        return this;
    }

    public ArtisJsonBuilder dynamicBounds() {
        settings.addProperty("dynamic_bounds", true);
        return this;
    }

//    public void build() {
//        try {
//            ArtisData.loadData(ArtisData.jankson.load(root.toString()));
//            Artis.logger.info(root.toString());
//        } catch (SyntaxError syntaxError) {
//            Artis.logger.error("[Artis] Error converting JSON for KubeJS. Is JSON empty?");
//        }
//    }

    public String getType() {
        return "block";
    }
}
