package com.github.tartaricacid.touhoulittlemaid.inventory.container.other;

import com.github.tartaricacid.touhoulittlemaid.inventory.SoulSpellStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class SoulSpellContainer extends AbstractContainerMenu {
    public static final int PAGE_SIZE = 54;
    public static final MenuType<SoulSpellContainer> TYPE = IForgeMenuType.create((id, inv, data) -> new SoulSpellContainer(id, inv, data.readVarInt(), data.readVarInt()));
    private final SimpleContainer display = new SimpleContainer(PAGE_SIZE);
    private final int page;
    private final int pageCount;

    public SoulSpellContainer(int id, Inventory playerInventory, int page) {
        this(id, playerInventory, page, -1);
    }

    public SoulSpellContainer(int id, Inventory playerInventory, int page, int syncedPageCount) {
        super(TYPE, id);
        int size = SoulSpellStorage.getSize(playerInventory.player);
        this.pageCount = syncedPageCount > 0 ? syncedPageCount : Math.max(1, (size + PAGE_SIZE - 1) / PAGE_SIZE);
        this.page = Math.max(0, Math.min(page, pageCount - 1));
        SoulSpellStorage.loadPage(playerInventory.player, this.page, display);
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(display, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
    }

    public int getPage() { return page; }
    public int getPageCount() { return pageCount; }
    @Override public void clicked(int slotId, int button, ClickType clickType, Player player) {
        super.clicked(slotId, button, clickType, player);
        if (slotId >= 0 && slotId < this.slots.size()) SoulSpellStorage.savePage(player, page, display);
    }
    @Override public boolean stillValid(Player player) { return true; }
    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) return net.minecraft.world.item.ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return net.minecraft.world.item.ItemStack.EMPTY;

        net.minecraft.world.item.ItemStack stack = slot.getItem();
        net.minecraft.world.item.ItemStack original = stack.copy();
        boolean moved;
        if (index < PAGE_SIZE) {
            moved = this.moveItemStackTo(stack, PAGE_SIZE, this.slots.size(), true);
        } else {
            moved = this.moveItemStackTo(stack, 0, PAGE_SIZE, false);
        }
        if (!moved) return net.minecraft.world.item.ItemStack.EMPTY;

        if (stack.isEmpty()) slot.set(net.minecraft.world.item.ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        SoulSpellStorage.savePage(player, page, display);
        return original;
    }
}
