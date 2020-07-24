package io.github.alloffabric.artis.inventory;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.ArtisClient;
import io.github.alloffabric.artis.api.ArtisCraftingRecipe;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.api.ContainerLayout;
import io.github.alloffabric.artis.api.RecipeProvider;
import io.github.alloffabric.artis.inventory.slot.WArtisResultSlot;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class ArtisCraftingController extends SyncedGuiDescription implements RecipeProvider {
    private final ArtisTableType tableType;
    private final PlayerEntity player;
    private final ArtisCraftingInventory craftInv;
    private final CraftingResultInventory resultInv;
    private final ScreenHandlerContext context;

    private final WPlainPanel panel;
    private final WLabel label;
    private final WItemSlot grid;
    private final WArtisResultSlot result;
    private final WPlayerInvPanel playerInv;
    private WLabel catalystCost;
    private WItemSlot catalyst;

    public ArtisCraftingController(ScreenHandlerType type, ArtisTableType tableType, int syncId, PlayerEntity player, ScreenHandlerContext context) {
        super(type, syncId, player.inventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        this.tableType = tableType;
        this.player = player;
        this.context = context;

        this.resultInv = new CraftingResultInventory();
        this.craftInv = new ArtisCraftingInventory(this, tableType.getWidth(), tableType.getHeight());
        if (tableType.hasBlockEntity()) {
            for (int i = 0; i < blockInventory.size(); i++) {
                craftInv.setStack(i, blockInventory.getStack(i));
            }
        }
        ContainerLayout layout = new ContainerLayout(tableType.getWidth(), tableType.getHeight());

        panel = new WPlainPanel();
        setRootPanel(panel);

        this.result = new WArtisResultSlot(player, craftInv, resultInv, 0, 1, 1, true, syncId);
        panel.add(result, layout.getResultX(), layout.getResultY() + 3);

        if (getTableType().hasCatalystSlot()) {
            this.catalyst = WItemSlot.of(craftInv, craftInv.size() - 1);
            panel.add(catalyst, layout.getCatalystX(), layout.getCatalystY());

            this.catalystCost = new WLabel("", 0xAA0000).setHorizontalAlignment(HorizontalAlignment.CENTER);
            panel.add(catalystCost, layout.getCatalystX(), layout.getCatalystY() + 18);
        }

        this.grid = WItemSlot.of(craftInv, 0, getTableType().getWidth(), getTableType().getHeight());
        panel.add(grid, layout.getGridX(), layout.getGridY());

        this.playerInv = this.createPlayerInventoryPanel();
        panel.add(playerInv, layout.getPlayerX(), layout.getPlayerY());

        this.label = new WLabel(ArtisClient.getName(tableType.getId()), 0x404040);
        panel.add(label, 0, 0);

        WSprite arrow = new WSprite(new Identifier(Artis.MODID, "textures/gui/translucent_arrow.png"));
        panel.add(arrow, layout.getArrowX(), layout.getArrowY() + 4, 22, 15);

        panel.validate(this);
        craftInv.setCheckMatrixChanges(true);
        if (player.world.isClient) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            ClientSidePacketRegistry.INSTANCE.sendToServer(Artis.request_sync, buf);
        }
    }

    private static BackgroundPainter slotColor(int color) {
        return (left, top, panel) -> {
            int lo = ScreenDrawing.multiplyColor(color, 0.5F);
            int bg = 0x4C000000;
            int hi = ScreenDrawing.multiplyColor(color, 1.25F);
            if (!(panel instanceof WItemSlot)) {
                ScreenDrawing.drawBeveledPanel(left - 1, top - 1, panel.getWidth() + 2, panel.getHeight() + 2, lo, bg, hi);
            } else {
                WItemSlot slot = (WItemSlot) panel;

                for (int x = 0; x < slot.getWidth() / 18; ++x) {
                    for (int y = 0; y < slot.getHeight() / 18; ++y) {
                        if (slot.isBigSlot()) {
                            ScreenDrawing.drawBeveledPanel(x * 18 + left - 3, y * 18 + top - 3, 24, 24, lo, bg, hi);
                        } else {
                            ScreenDrawing.drawBeveledPanel(x * 18 + left, y * 18 + top, 18, 18, lo, bg, hi);
                        }
                    }
                }
            }
        };
    }

    public ArtisCraftingInventory getCraftInv() {
        return craftInv;
    }

    public CraftingResultInventory getResultInv() {
        return resultInv;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void addPainters() {
        int color = tableType.getColor();
        if (tableType.hasColor()) {
            panel.setBackgroundPainter(BackgroundPainter.createColorful(color));
            grid.setBackgroundPainter(slotColor(color));
            if (tableType.hasCatalystSlot())
                catalyst.setBackgroundPainter(slotColor(color));
            result.setBackgroundPainter(slotColor(color));
            playerInv.setBackgroundPainter(slotColor(color));
        } else {
            panel.setBackgroundPainter(BackgroundPainter.VANILLA);
            grid.setBackgroundPainter(BackgroundPainter.SLOT);
            if (tableType.hasCatalystSlot())
                catalyst.setBackgroundPainter(BackgroundPainter.SLOT);
            result.setBackgroundPainter(BackgroundPainter.SLOT);
            playerInv.setBackgroundPainter(BackgroundPainter.SLOT);
        }
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, pos) -> {
            if (!tableType.hasBlockEntity()) {
                dropInventory(player, world, craftInv);
            } else {
                for (int i = 0; i < craftInv.size(); i++) {
                    blockInventory.setStack(i, craftInv.getStack(i));
                }
            }
        });
    }

    @Override
    public int getCraftingWidth() {
        return tableType.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return tableType.getHeight();
    }

    @Override
    public boolean matches(Recipe recipe) {
        return recipe.matches(craftInv, player.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingSlotCount() {
        return getTableType().getWidth() * getTableType().getHeight();
    }

    // update crafting
    //clientside only
    @Override
    public void updateSlotStacks(List<ItemStack> stacks) {
        craftInv.setCheckMatrixChanges(false);
        super.updateSlotStacks(stacks);
        craftInv.setCheckMatrixChanges(true);
        onContentChanged(null);
    }

    //leaving here in case it's needed
    public void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftInv, CraftingResultInventory resultInv) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<CraftingRecipe> opt = world.getServer().getRecipeManager().getFirstMatch(this.tableType, craftInv, world);
            Optional<CraftingRecipe> optCrafting = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftInv, world);
            if (opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, recipe)) {
                    stack = recipe.craft(craftInv);
                }
            } else if (tableType.shouldIncludeNormalRecipes() && optCrafting.isPresent()) {
                CraftingRecipe recipe = optCrafting.get();
                if (resultInv.shouldCraftRecipe(world, serverPlayer, recipe)) {
                    stack = recipe.craft(craftInv);
                }
            }

            resultInv.setStack(0, stack);
            serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, getCraftingResultSlotIndex(), stack));
        } else {
            Optional<CraftingRecipe> opt = world.getRecipeManager().getFirstMatch(this.tableType, craftInv, world);
            if (tableType.hasCatalystSlot() && opt.isPresent()) {
                CraftingRecipe recipe = opt.get();
                if (recipe instanceof ArtisCraftingRecipe) {
                    ArtisCraftingRecipe artisCraftingRecipe = (ArtisCraftingRecipe) recipe;
                    if (!artisCraftingRecipe.getCatalyst().isEmpty() && artisCraftingRecipe.getCatalystCost() > 0 && catalystCost != null) {
                        catalystCost.setText(new LiteralText(Formatting.RED + "-" + artisCraftingRecipe.getCatalystCost()));
                    }
                }
            } else if (tableType.hasCatalystSlot() && catalystCost != null) {
                catalystCost.setText(new LiteralText(""));
            }
        }
    }

    //like vanilla, but not a pile of lag
    public static void updateResult(World world, PlayerEntity player, CraftingInventory inv, CraftingResultInventory result, ArtisTableType artisTableType) {
        if (!world.isClient) {

            ItemStack itemstack = ItemStack.EMPTY;

            boolean isArtis = false;

            Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) result.getLastRecipe();
            //find artis recipe first
            if (recipe == null || !recipe.matches(inv, world)) {
                recipe = findArtisRecipe(artisTableType,inv, world);
                if (recipe != null) isArtis = true;
            }
            //else fall back to vanilla
            if (recipe == null && artisTableType.shouldIncludeNormalRecipes()) {
                recipe = findVanillaRecipe(inv,world);
            }
            //there is no matching recipe
            if (recipe != null) {
                itemstack = recipe.craft(inv);
            }

            result.setStack(0, itemstack);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeIdentifier(recipe != null ? recipe.getId(): Artis.dummy);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Artis.recipe_sync, buf);
            result.setLastRecipe(recipe);
        }
    }

    @Override
    public void onContentChanged(Inventory inv) {
        updateResult(world,player,craftInv,resultInv,tableType);
    }

    @Override
    public ArtisTableType getTableType() {
        return tableType;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasStack()) {
            if (slotIndex == getCraftingResultSlotIndex()) {
                int slotcount = getCraftingSlotCount() + (tableType.hasCatalystSlot() ? 1 : 0);
                return handleShiftCraft(player,this,slot,craftInv,resultInv,slotcount,slotcount + 36);
            }
            ItemStack toTake = slot.getStack();
            stack = toTake.copy();

            if (toTake.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (toTake.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack takenStack = slot.onTakeItem(player, toTake);
            if (slotIndex == getCraftingResultSlotIndex()) {
                player.dropItem(takenStack, false);
            }
        }

        return stack;
    }

    @Override
    public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
        if (slotNumber == getCraftingResultSlotIndex() && action == SlotActionType.QUICK_MOVE) {
            return transferSlot(player, slotNumber);
        }

        return super.onSlotClick(slotNumber, button, action, player);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        this.craftInv.provideRecipeInputs(finder);
    }

    @Override
    public void clearCraftingSlots() {
        this.craftInv.clear();
        this.resultInv.clear();
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.resultInv && super.canInsertIntoSlot(stack, slot);
    }

    public static ItemStack handleShiftCraft(PlayerEntity player, ArtisCraftingController container, Slot resultSlot, ArtisCraftingInventory input, CraftingResultInventory craftResult, int outStart, int outEnd) {
        ItemStack outputCopy = ItemStack.EMPTY;
        input.setCheckMatrixChanges(false);
        if (resultSlot != null && resultSlot.hasStack()) {

            Recipe<CraftingInventory> recipe = (Recipe<CraftingInventory>) craftResult.getLastRecipe();
            if (recipe == null && container.tableType.shouldIncludeNormalRecipes()) {
                recipe = findVanillaRecipe(input,player.world);
            }
            while (recipe != null && recipe.matches(input, player.world)) {
                ItemStack recipeOutput = resultSlot.getStack().copy();
                outputCopy = recipeOutput.copy();

                recipeOutput.getItem().onCraft(recipeOutput, player.world, player);

                if (!player.world.isClient && !container.insertItem(recipeOutput, outStart, outEnd,true)) {
                    input.setCheckMatrixChanges(true);
                    return ItemStack.EMPTY;
                }

                resultSlot.onStackChanged(recipeOutput, outputCopy);
                resultSlot.markDirty();

                if (!player.world.isClient && recipeOutput.getCount() == outputCopy.getCount()) {
                    input.setCheckMatrixChanges(true);
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack2 = resultSlot.onTakeItem(player, recipeOutput);
                player.dropItem(itemstack2, false);
            }
            input.setCheckMatrixChanges(true);
            updateResult(player.world, player, input, craftResult, container.tableType);
        }
        input.setCheckMatrixChanges(true);
        return craftResult.getLastRecipe() == null ? ItemStack.EMPTY : outputCopy;
    }

    public static Recipe<CraftingInventory> findArtisRecipe(ArtisTableType tableType, CraftingInventory inv, World world) {
        return (Recipe<CraftingInventory>) world.getRecipeManager().getFirstMatch(tableType, inv, world).orElse(null);
    }

    public static Recipe<CraftingInventory> findVanillaRecipe(CraftingInventory inv, World world) {
        return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inv, world).orElse(null);
    }
}
