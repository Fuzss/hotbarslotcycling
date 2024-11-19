package fuzs.hotbarslotcycling.impl.client.handler;

import com.mojang.blaze3d.platform.InputConstants;
import fuzs.hotbarslotcycling.api.v1.client.ItemCyclingProvider;
import fuzs.hotbarslotcycling.api.v1.client.SlotCyclingProvider;
import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.config.ClientConfig;
import fuzs.hotbarslotcycling.impl.config.ModifierKey;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class CyclingInputHandler {
    public static final KeyMapping CYCLE_LEFT_KEY_MAPPING = KeyMappingHelper.registerKeyMapping(HotbarSlotCycling.id(
            "key_left"), InputConstants.KEY_G
    );
    public static final KeyMapping CYCLE_RIGHT_KEY_MAPPING = KeyMappingHelper.registerKeyMapping(HotbarSlotCycling.id(
            "key_right"), InputConstants.KEY_H
    );
    private static final int DEFAULT_SLOTS_DISPLAY_TICKS = 15;

    private static int slotsDisplayTicks;
    private static int globalPopTime;

    public static EventResult onMouseScroll(boolean leftDown, boolean middleDown, boolean rightDown, double horizontalAmount, double verticalAmount) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!((Player) minecraft.player).isSpectator() &&
                HotbarSlotCycling.CONFIG.get(ClientConfig.class).scrollingModifierKey.isActive()) {
            double accumulatedScroll = minecraft.mouseHandler.scrollWheelHandler.accumulatedScrollY == 0 ?
                    -minecraft.mouseHandler.scrollWheelHandler.accumulatedScrollX :
                    minecraft.mouseHandler.scrollWheelHandler.accumulatedScrollY;
            double totalScroll = verticalAmount + accumulatedScroll;
            if (totalScroll > 0.0) {
                if (cycleSlot(minecraft, minecraft.player, SlotCyclingProvider::cycleSlotBackward)) {
                    return EventResult.INTERRUPT;
                }
            } else if (totalScroll < 0.0) {
                if (cycleSlot(minecraft, minecraft.player, SlotCyclingProvider::cycleSlotForward)) {
                    return EventResult.INTERRUPT;
                }
            }
        }
        return EventResult.PASS;
    }

    public static void onStartClientTick(Minecraft minecraft) {
        if (slotsDisplayTicks > 0) slotsDisplayTicks--;
        if (globalPopTime > 0) globalPopTime--;
        if (minecraft.player != null && !minecraft.player.isSpectator()) {
            if (minecraft.getOverlay() == null && minecraft.screen == null) {
                handleModKeybinds(minecraft, minecraft.player);
                handleHotbarKeybinds(minecraft, minecraft.player, minecraft.options);
            }
            if (HotbarSlotCycling.CONFIG.get(ClientConfig.class).scrollingModifierKey.isActive()) {
                slotsDisplayTicks = DEFAULT_SLOTS_DISPLAY_TICKS;
            }
        }
    }

    private static void handleModKeybinds(Minecraft minecraft, Player player) {
        while (CYCLE_LEFT_KEY_MAPPING.consumeClick()) {
            cycleSlot(minecraft, player, SlotCyclingProvider::cycleSlotBackward);
        }
        while (CYCLE_RIGHT_KEY_MAPPING.consumeClick()) {
            cycleSlot(minecraft, player, SlotCyclingProvider::cycleSlotForward);
        }
    }

    private static void handleHotbarKeybinds(Minecraft minecraft, Player player, Options options) {
        if (!HotbarSlotCycling.CONFIG.get(ClientConfig.class).doublePressHotbarKey) return;
        boolean saveHotbarActivatorDown = options.keySaveHotbarActivator.isDown();
        boolean loadHotbarActivatorDown = options.keyLoadHotbarActivator.isDown();
        if (!player.isCreative() || !loadHotbarActivatorDown && !saveHotbarActivatorDown) {
            ModifierKey scrollingModifierKey = HotbarSlotCycling.CONFIG.get(ClientConfig.class).scrollingModifierKey;
            boolean forward = !scrollingModifierKey.isKey() || !scrollingModifierKey.isActive();
            for (int i = 0; i < options.keyHotbarSlots.length; i++) {
                while (i == player.getInventory().selected && options.keyHotbarSlots[i].consumeClick()) {
                    cycleSlot(minecraft,
                            player,
                            forward ? SlotCyclingProvider::cycleSlotForward : SlotCyclingProvider::cycleSlotBackward
                    );
                }
            }
        }
    }

    private static boolean cycleSlot(Minecraft minecraft, Player player, Predicate<SlotCyclingProvider> cycleAction) {
        SlotCyclingProvider provider = SlotCyclingProvider.getProvider(player);
        if (provider != null && cycleAction.test(provider)) {
            slotsDisplayTicks = DEFAULT_SLOTS_DISPLAY_TICKS;
            globalPopTime = 5;
            player.stopUsingItem();
            if (provider instanceof ItemCyclingProvider itemProvider) {
                clearItemRendererInHand(minecraft, itemProvider.interactionHand());
            }
            return true;
        }
        return false;
    }

    private static void clearItemRendererInHand(Minecraft minecraft, InteractionHand interactionHand) {
        // force the reequip animation for the new held item
        ItemInHandRenderer itemInHandRenderer = minecraft.gameRenderer.itemInHandRenderer;
        if (interactionHand == InteractionHand.OFF_HAND) {
            itemInHandRenderer.offHandItem = ItemStack.EMPTY;
        } else {
            itemInHandRenderer.mainHandItem = ItemStack.EMPTY;
        }
    }

    public static int getSlotsDisplayTicks() {
        return slotsDisplayTicks;
    }

    public static int getGlobalPopTime() {
        return globalPopTime;
    }
}
