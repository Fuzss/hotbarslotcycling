package fuzs.hotbarslotcycling.api.v1.client;

import fuzs.hotbarslotcycling.impl.client.handler.SlotsRendererHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * The slot rendering implementation of Hotbar Slot Cycling exposed as an interface to allow for better support from mods such as AutoHUD.
 */
public interface CyclingSlotsRenderer {

    /**
     * Main method for rendering the additional slot cycling slots.
     */
    void renderSlots(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float partialTick, Font font, Player player, ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack);

    /**
     * Called inside {@link #renderSlots(GuiGraphics, int, int, float, Font, Player, ItemStack, ItemStack, ItemStack)}
     * by the default implementation to decide whether to proceed with rendering.
     */
    boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack);

    /**
     * Renders the cycling slots background textures, consisting of parts of the hotbar slots and offhand slot
     * textures.
     * <p>
     * How many slot backgrounds are drawn is decided in
     * {@link #renderSlots(GuiGraphics, int, int, float, Font, Player, ItemStack, ItemStack, ItemStack)}.
     */
    void renderSlotBackgrounds(GuiGraphics guiGraphics, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight);

    /**
     * Renders up to three items above the previously drawn slot backgrounds.
     * <p>
     * How many items are drawn is decided in
     * {@link #renderSlots(GuiGraphics, int, int, float, Font, Player, ItemStack, ItemStack, ItemStack)}.
     */
    void renderSlotItems(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight);

    /**
     * Renders an item on the screen.
     */
    void renderItemInSlot(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack itemStack);

    /**
     * Set the currently used {@link CyclingSlotsRenderer} instance, should be called once during mod construction.
     */
    static void setSlotsRenderer(CyclingSlotsRenderer slotsRenderer) {
        SlotsRendererHandler.setSlotsRenderer(slotsRenderer);
    }

    /**
     * Get the currently used {@link CyclingSlotsRenderer} instance, useful for retrieving an existing renderer to wrap.
     */
    static CyclingSlotsRenderer getSlotsRenderer() {
        return SlotsRendererHandler.getSlotsRenderer();
    }
}
