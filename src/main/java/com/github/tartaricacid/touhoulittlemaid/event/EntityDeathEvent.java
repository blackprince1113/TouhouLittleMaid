package com.github.tartaricacid.touhoulittlemaid.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.SoulSpellStorage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EntityDeathEvent {
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level.isClientSide) {
            SoulSpellStorage.storeOwnedMaidEntities((net.minecraft.server.level.ServerPlayer) player);
        }
        Entity causingEntity = event.getSource().getEntity();
        if (causingEntity instanceof EntityMaid maid) {
            maid.getKillRecordManager().onTargetDeath(maid, event.getEntity());
        }
    }
}
