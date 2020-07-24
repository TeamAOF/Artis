package io.github.alloffabric.artis.api;

import net.minecraft.item.ItemStack;

/**
 * An item that has special behavior when used as a catalyst in an Artis table.
 */
public interface SpecialCatalyst {

    /**
     * @param stack The stack being used as a catalyst.
     * @param cost  The catalyst cost of this recipe.
     * @return Whether this stack fills the required cost.
     */
    boolean matches(ItemStack stack, int cost);

    /**
     * Consume something from this stack as part of an Artis catalyst.
     *
     * @param catalyst The stack being consumed as catalyst.
     * @param cost     How many units are being consumed.
     * @return The remaining stack after consumption.
     */
    ItemStack consume(ItemStack catalyst, int cost);
}
