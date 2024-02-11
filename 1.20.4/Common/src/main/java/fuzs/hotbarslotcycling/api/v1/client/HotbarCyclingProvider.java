package fuzs.hotbarslotcycling.api.v1.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public record HotbarCyclingProvider(Inventory inventory) implements SlotCyclingProvider {

    @Override
    public ItemStack getSelectedStack() {
        return this.inventory.getSelected();
    }

    @Override
    public ItemStack getForwardStack() {
        int forwardSlot = this.getFilledSlot(true);
        return forwardSlot != -1 ? this.inventory.getItem(forwardSlot) : ItemStack.EMPTY;
    }

    private int getFilledSlot(boolean forward) {
        return this.getFilledSlot(this.inventory.selected, forward);
    }

    private int getFilledSlot(int selected, boolean forward) {
        int inventoryRows = this.inventory.items.size() / Inventory.getSelectionSize();
        for (int i = 1; i < inventoryRows; i++) {
            int slot = ((i * (forward ? -1 : 1) + inventoryRows) % inventoryRows * Inventory.getSelectionSize() +
                    selected) % this.inventory.items.size();
            if (Inventory.isHotbarSlot(slot) || !this.inventory.getItem(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    public ItemStack getBackwardStack() {
        int backwardSlot = this.getFilledSlot(false);
        return backwardSlot != -1 ? this.inventory.getItem(backwardSlot) : ItemStack.EMPTY;
    }

    @Override
    public boolean cycleSlotForward() {
        return this.performSlotCycling(true);
    }

    private boolean performSlotCycling(boolean forward) {
        int slot = this.getFilledSlot(forward);
        if (slot != -1) {
            while (!Inventory.isHotbarSlot(slot)) {
                int otherSlot = this.getFilledSlot(slot, forward);
                swapInventorySlots(this.inventory.player, slot, otherSlot);
                slot = otherSlot;
            }
            ItemStack itemInHand = this.inventory.getItem(slot);
            if (!itemInHand.isEmpty()) itemInHand.setPopTime(5);
            return true;
        }
        return false;
    }

    private static void swapInventorySlots(Player player, int slot, int otherSlot) {
        Minecraft minecraft = Minecraft.getInstance();
        // so Minecraft 1.20.4 introduced a fun limitation where otherSlot can only be a hotbar (or offhand) slot, where previously freely swapping with any other inventory slot was possible
        // so instead of swapping slots directly, we have to use some hotbar slot (in our case simply the corresponding slot for that column) to temporarily put the items
        if (otherSlot >= 0 && otherSlot < Inventory.getSelectionSize()) {
            minecraft.gameMode.handleInventoryMouseClick(player.containerMenu.containerId, slot, otherSlot,
                    ClickType.SWAP, player
            );
        } else {
            // any hotbar slot would do, just a temporary place to put the items since the second slot must be in the hotbar or offhand now
            int hotbarSlot = otherSlot % Inventory.getSelectionSize();
            minecraft.gameMode.handleInventoryMouseClick(player.containerMenu.containerId, slot, hotbarSlot,
                    ClickType.SWAP, player
            );
            minecraft.gameMode.handleInventoryMouseClick(player.containerMenu.containerId, otherSlot, hotbarSlot,
                    ClickType.SWAP, player
            );
            minecraft.gameMode.handleInventoryMouseClick(player.containerMenu.containerId, slot, hotbarSlot,
                    ClickType.SWAP, player
            );
        }
    }

    @Override
    public boolean cycleSlotBackward() {
        return this.performSlotCycling(false);
    }
}
