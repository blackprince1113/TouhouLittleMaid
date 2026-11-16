package com.github.tartaricacid.touhoulittlemaid.event;

import com.github.tartaricacid.touhoulittlemaid.inventory.SoulSpellStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class PlayerTeleportCommandEvent {
    private PlayerTeleportCommandEvent() {}

    @SubscribeEvent
    public static void onPlayerTeleportCommand(EntityTeleportEvent.TeleportCommand event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SoulSpellStorage.storeOwnedMaidEntities(player, true);
        }
    }
}
