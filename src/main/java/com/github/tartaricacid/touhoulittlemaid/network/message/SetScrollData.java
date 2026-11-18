package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.item.ItemFoxScroll;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.event.TemporaryChunkLoader;
import com.github.tartaricacid.touhoulittlemaid.world.data.MaidWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetScrollData {
    private final String dimension;
    private final BlockPos pos;
    private final boolean loadChunk;

    public SetScrollData(String dimension, BlockPos pos, boolean loadChunk) {
        this.dimension = dimension;
        this.pos = pos;
        this.loadChunk = loadChunk;
    }

    public static void encode(SetScrollData message, FriendlyByteBuf buf) {
        buf.writeUtf(message.dimension);
        buf.writeBlockPos(message.pos);
        buf.writeBoolean(message.loadChunk);
    }

    public static SetScrollData decode(FriendlyByteBuf buf) {
        return new SetScrollData(buf.readUtf(), buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(SetScrollData message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                ItemStack item = sender.getMainHandItem();
                if (item.getItem() instanceof ItemFoxScroll) {
                    if (!message.loadChunk) {
                        ItemFoxScroll.setTrackInfo(item, message.dimension, message.pos);
                    } else if (item.is(InitItems.RED_FOX_SCROLL.get())) {
                        MaidWorldData data = MaidWorldData.get(sender.level);
                        boolean ownsMaidAtPosition = data != null && data.getPlayerMaidInfos(sender) != null
                                && data.getPlayerMaidInfos(sender).stream().anyMatch(info ->
                                info.getDimension().equals(message.dimension) && info.getChunkPos().equals(message.pos));
                        ResourceLocation dimensionId = ResourceLocation.tryParse(message.dimension);
                        ServerLevel targetLevel = dimensionId == null ? null : sender.server.getLevel(net.minecraft.resources.ResourceKey.create(
                                net.minecraft.core.Registry.DIMENSION_REGISTRY, dimensionId));
                        if (ownsMaidAtPosition && targetLevel != null) {
                            TemporaryChunkLoader.loadForThreeSeconds(targetLevel, new ChunkPos(message.pos));
                        }
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
