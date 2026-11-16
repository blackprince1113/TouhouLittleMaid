package com.github.tartaricacid.touhoulittlemaid.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SoulSpellCapability {
    private final List<ItemStack> spells = new ArrayList<>();

    public int size() { return spells.size(); }
    public ItemStack get(int index) { return index >= 0 && index < spells.size() ? spells.get(index).copy() : ItemStack.EMPTY; }
    public void add(ItemStack stack) { if (!stack.isEmpty()) spells.add(stack.copy()); }
    public void set(int index, ItemStack stack) {
        while (spells.size() <= index) spells.add(ItemStack.EMPTY);
        spells.set(index, stack.copy());
        while (!spells.isEmpty() && spells.get(spells.size() - 1).isEmpty()) spells.remove(spells.size() - 1);
    }
    public void copyFrom(SoulSpellCapability other) {
        spells.clear();
        for (ItemStack stack : other.spells) spells.add(stack.copy());
    }
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (ItemStack stack : spells) list.add(stack.save(new CompoundTag()));
        tag.put("Items", list);
        return tag;
    }
    public void deserializeNBT(CompoundTag tag) {
        spells.clear();
        ListTag list = tag.getList("Items", 10);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = ItemStack.of(list.getCompound(i));
            if (!stack.isEmpty()) spells.add(stack);
        }
    }
}
