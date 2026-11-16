package com.github.tartaricacid.touhoulittlemaid.inventory;

import com.github.tartaricacid.touhoulittlemaid.capability.SoulSpellCapability;
import com.github.tartaricacid.touhoulittlemaid.capability.SoulSpellCapabilityProvider;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.ItemSmartSlab;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class SoulSpellStorage {
    private static final String OLD_KEY = "TouhouLittleMaidSoulSpells";
    private static final int PAGE_SIZE = 54;
    private SoulSpellStorage() {}

    private static SoulSpellCapability get(Player player) {
        SoulSpellCapability capability = player.getCapability(SoulSpellCapabilityProvider.SOUL_SPELL_CAP).orElse(null);
        if (capability != null && capability.size() == 0 && player.getPersistentData().contains(OLD_KEY, 9)) {
            ListTag oldList = player.getPersistentData().getList(OLD_KEY, 10);
            for (int i = 0; i < oldList.size(); i++) capability.add(ItemStack.of(oldList.getCompound(i)));
            player.getPersistentData().remove(OLD_KEY);
        }
        return capability;
    }

    public static int getSize(Player player) {
        SoulSpellCapability capability = get(player);
        return capability == null ? 0 : capability.size();
    }
    public static void add(Player player, ItemStack stack) {
        SoulSpellCapability capability = get(player);
        if (capability != null) capability.add(stack);
    }
    public static void storeOwnedMaidEntities(ServerPlayer player) {
        storeOwnedMaidEntities(player, false);
    }
    public static void storeOwnedMaidEntities(ServerPlayer player, boolean skipSittingMaids) {
        List<EntityMaid> ownedMaids = new ArrayList<>();
        for (ServerLevel level : player.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof EntityMaid maid && player.getUUID().equals(maid.getOwnerUUID())
                        && (!skipSittingMaids || !maid.isOrderedToSit())) {
                    ownedMaids.add(maid);
                }
            }
        }
        for (EntityMaid maid : ownedMaids) {
            ItemStack soulSpell = new ItemStack(InitItems.SMART_SLAB_HAS_MAID.get());
            maid.setHomeModeEnable(false);
            ItemSmartSlab.storeMaidData(soulSpell, maid);
            add(player, soulSpell);
            maid.discard();
        }
    }
    public static void loadPage(Player player, int page, SimpleContainer inventory) {
        SoulSpellCapability capability = get(player);
        if (capability == null) return;
        int start = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE; i++) {
            ItemStack stack = capability.get(start + i);
            if (stack.is(InitItems.SMART_SLAB_HAS_MAID.get())) inventory.setItem(i, stack);
        }
    }
    public static void savePage(Player player, int page, SimpleContainer inventory) {
        SoulSpellCapability capability = get(player);
        if (capability == null) return;
        int start = page * PAGE_SIZE;
        for (int i = 0; i < PAGE_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            capability.set(start + i, stack.is(InitItems.SMART_SLAB_HAS_MAID.get()) ? stack : ItemStack.EMPTY);
        }
    }
    public static void copy(Player from, Player to) {
        SoulSpellCapability oldCapability = get(from);
        SoulSpellCapability newCapability = to.getCapability(SoulSpellCapabilityProvider.SOUL_SPELL_CAP).orElse(null);
        if (oldCapability != null && newCapability != null) newCapability.copyFrom(oldCapability);
    }
}
