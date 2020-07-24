package io.github.alloffabric.artis.block.entity;

import io.github.alloffabric.artis.Artis;
import io.github.alloffabric.artis.api.ArtisTableType;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import io.github.alloffabric.artis.inventory.DefaultInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class ArtisTableBlockEntity extends BlockEntity implements DefaultInventory, BlockEntityClientSerializable, ExtendedScreenHandlerFactory {
    private ArtisTableType tableType;
    private DefaultedList<ItemStack> stacks;

    public ArtisTableBlockEntity() {
        super(Artis.ARTIS_BLOCK_ENTITY);
    }

    public ArtisTableBlockEntity(ArtisTableType tableType) {
        super(Artis.ARTIS_BLOCK_ENTITY);

        this.tableType = tableType;
        this.stacks = DefaultedList.ofSize((tableType.getWidth() * tableType.getHeight()) + 1, ItemStack.EMPTY);
    }

    public <T extends BlockEntity> ArtisTableBlockEntity(BlockEntityType<T> type) {
        super(type);
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new ArtisCraftingController(Registry.SCREEN_HANDLER.get(tableType.getId()), tableType, syncId, player, ScreenHandlerContext.create(world, getPos()));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        tableType = Artis.ARTIS_TABLE_TYPES.get(new Identifier(tag.getString("tableType")));
        stacks = DefaultedList.ofSize((tableType.getWidth() * tableType.getHeight()) + 1, ItemStack.EMPTY);
        Inventories.fromTag(tag, stacks);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(getCachedState(), tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (tableType != null)
            tag.putString("tableType", tableType.getId().toString());

        if (stacks != null)
            Inventories.toTag(tag, stacks);
        return super.toTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return toTag(tag);
    }
}