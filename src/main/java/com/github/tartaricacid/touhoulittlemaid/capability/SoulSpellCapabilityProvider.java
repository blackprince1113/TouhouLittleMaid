package com.github.tartaricacid.touhoulittlemaid.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SoulSpellCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<SoulSpellCapability> SOUL_SPELL_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private SoulSpellCapability instance;
    @Nonnull private SoulSpellCapability create() { if (instance == null) instance = new SoulSpellCapability(); return instance; }
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == SOUL_SPELL_CAP ? LazyOptional.of(this::create).cast() : LazyOptional.empty();
    }
    @Override public void deserializeNBT(CompoundTag nbt) { create().deserializeNBT(nbt); }
    @Override public CompoundTag serializeNBT() { return create().serializeNBT(); }
}
