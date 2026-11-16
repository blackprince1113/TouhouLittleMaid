package com.github.tartaricacid.touhoulittlemaid.command.subcommand;

import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.SoulSpellContainer;
import com.github.tartaricacid.touhoulittlemaid.inventory.SoulSpellStorage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

public final class SoulSpellCommand {
    private SoulSpellCommand() {}
    public static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> get() {
        return Commands.literal("maid").executes(ctx -> {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            int pageCount = Math.max(1, (SoulSpellStorage.getSize(player) + SoulSpellContainer.PAGE_SIZE - 1) / SoulSpellContainer.PAGE_SIZE);
            NetworkHooks.openScreen(player, new SimpleMenuProvider((id, inv, p) -> new SoulSpellContainer(id, inv, 0), Component.translatable("gui.touhou_little_maid.soul_spell.title")), buf -> buf.writeVarInt(0).writeVarInt(pageCount));
            return 1;
        });
    }
}
