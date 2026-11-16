package com.github.tartaricacid.touhoulittlemaid.network.message;

import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.SoulSpellContainer;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Supplier;

public class SoulSpellPageMessage {
    private final int page;
    public SoulSpellPageMessage(int page) { this.page = page; }
    public static void encode(SoulSpellPageMessage msg, FriendlyByteBuf buf) { buf.writeVarInt(msg.page); }
    public static SoulSpellPageMessage decode(FriendlyByteBuf buf) { return new SoulSpellPageMessage(buf.readVarInt()); }
    public static void handle(SoulSpellPageMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !(player.containerMenu instanceof SoulSpellContainer menu)) return;
            int page = Math.max(0, Math.min(msg.page, menu.getPageCount() - 1));
            int pageCount = menu.getPageCount();
            NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, p) -> new SoulSpellContainer(id, inv, page), net.minecraft.network.chat.Component.translatable("gui.touhou_little_maid.soul_spell.title")), buf -> buf.writeVarInt(page).writeVarInt(pageCount));
        });
        ctx.get().setPacketHandled(true);
    }
}
