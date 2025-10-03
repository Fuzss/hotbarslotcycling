package fuzs.hotbarslotcycling.impl.client.handler;

import fuzs.hotbarslotcycling.api.v1.client.CyclingSlotsRenderer;
import fuzs.hotbarslotcycling.api.v1.client.SlotCyclingProvider;
import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.util.GuiGraphicsHelper;
import fuzs.hotbarslotcycling.impl.config.ClientConfig;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Objects;

public final class SlotsRendererHandler implements CyclingSlotsRenderer {
    private static final ResourceLocation HOTBAR_SPRITE = ResourceLocationHelper.withDefaultNamespace("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocationHelper.withDefaultNamespace(
            "hud/hotbar_offhand_right");

    private static CyclingSlotsRenderer instance = new SlotsRendererHandler();

    public static void onAfterRenderGuiLayer(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

        Minecraft minecraft = Minecraft.getInstance();

        if (!minecraft.options.hideGui && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {

            if (minecraft.getCameraEntity() instanceof Player player) {

                SlotCyclingProvider provider = SlotCyclingProvider.getProvider(player);
                if (provider != null) {

                    ItemStack forwardStack = provider.getForwardStack();
                    ItemStack selectedStack = provider.getSelectedStack();
                    ItemStack backwardStack = provider.getBackwardStack();
                    CyclingSlotsRenderer.getSlotsRenderer()
                            .renderSlots(guiGraphics,
                                    guiGraphics.guiWidth(),
                                    guiGraphics.guiHeight(),
                                    deltaTracker.getGameTimeDeltaPartialTick(false),
                                    minecraft.font,
                                    player,
                                    backwardStack,
                                    selectedStack,
                                    forwardStack);
                }
            }
        }
    }

    public static void setSlotsRenderer(CyclingSlotsRenderer slotsRenderer) {
        Objects.requireNonNull(slotsRenderer, "slots renderer is null");
        SlotsRendererHandler.instance = slotsRenderer;
    }

    public static CyclingSlotsRenderer getSlotsRenderer() {
        return instance;
    }

    @Override
    public void renderSlots(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float partialTick, Font font, Player player, ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {

        if (HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsDisplayState
                == ClientConfig.SlotsDisplayState.NEVER) {
            return;
        }
        if (!CyclingSlotsRenderer.getSlotsRenderer().testValidStacks(backwardStack, selectedStack, forwardStack)) {
            return;
        }

        boolean renderToRight = player.getMainArm().getOpposite() == HumanoidArm.LEFT;

        if (ItemStack.matches(forwardStack, backwardStack)) {
            if (renderToRight) {
                forwardStack = ItemStack.EMPTY;
            } else {
                backwardStack = ItemStack.EMPTY;
            }
        }

        int posX = screenWidth / 2 + (91 + HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsXOffset) * (
                renderToRight ? 1 : -1);
        int posY = screenHeight - HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsYOffset;
        if (HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsDisplayState == ClientConfig.SlotsDisplayState.KEY) {
            if (CyclingInputHandler.getSlotsDisplayTicks() > 0) {
                posY += (int) ((screenHeight - posY + 23) * (1.0F - Math.min(1.0F,
                        (CyclingInputHandler.getSlotsDisplayTicks() - partialTick) / 5.0F)));
            } else {
                return;
            }
        }

        CyclingSlotsRenderer.getSlotsRenderer()
                .renderSlotBackgrounds(guiGraphics,
                        posX,
                        posY,
                        !forwardStack.isEmpty(),
                        !backwardStack.isEmpty(),
                        renderToRight);
        CyclingSlotsRenderer.getSlotsRenderer()
                .renderSlotItems(guiGraphics,
                        posX,
                        posY - (16 + 3),
                        partialTick,
                        font,
                        player,
                        selectedStack,
                        forwardStack,
                        backwardStack,
                        renderToRight);
    }

    @Override
    public boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
        return !backwardStack.isEmpty() && !selectedStack.isEmpty() && !forwardStack.isEmpty();
    }

    @Override
    public void renderSlotBackgrounds(GuiGraphics guiGraphics, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight) {

        if (renderToRight) {

            // left slot cycling slot, use right offhand sprite as it lines up
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_OFFHAND_RIGHT_SPRITE, posX, posY - 23, 29, 24);
            if (renderForwardStack) {

                // right slot cycling slot, use left offhand sprite for better resource pack support in case something is intentionally different?
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        HOTBAR_OFFHAND_LEFT_SPRITE,
                        posX + 40 + 7,
                        posY - 23,
                        29,
                        24);
            }

            // center slot cycling slot
            GuiGraphicsHelper.blitTiledSprite(guiGraphics,
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SPRITE,
                    posX + 27,
                    posY - 22,
                    22,
                    22,
                    182,
                    22);
            // selected hotbar slot overlay
            GuiGraphicsHelper.blitTiledSprite(guiGraphics,
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SELECTION_SPRITE,
                    posX + 27 - 1,
                    posY - 22 - 1,
                    24,
                    24,
                    24,
                    23);
        } else {

            if (renderBackwardStack) {

                // left slot cycling slot, use right offhand sprite as it lines up
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        HOTBAR_OFFHAND_RIGHT_SPRITE,
                        posX - 29 - 40 - 7,
                        posY - 23,
                        29,
                        24);
            }

            // right slot cycling slot, use left offhand sprite for better resource pack support in case something is intentionally different?
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                    HOTBAR_OFFHAND_LEFT_SPRITE,
                    posX - 29,
                    posY - 23,
                    29,
                    24);
            // center slot cycling slot
            GuiGraphicsHelper.blitTiledSprite(guiGraphics,
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SPRITE,
                    posX - 29 - 20,
                    posY - 22,
                    22,
                    22,
                    182,
                    22);
            // selected hotbar slot overlay
            GuiGraphicsHelper.blitTiledSprite(guiGraphics,
                    RenderPipelines.GUI_TEXTURED,
                    HOTBAR_SELECTION_SPRITE,
                    posX - 29 - 20 - 1,
                    posY - 22 - 1,
                    24,
                    24,
                    24,
                    23);
        }
    }

    @Override
    public void renderSlotItems(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight) {

        if (renderToRight) {

            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX + 10, posY, partialTick, font, player, backwardStack);
            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX + 10 + 20, posY, partialTick, font, player, selectedStack);
            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX + 10 + 20 + 20, posY, partialTick, font, player, forwardStack);
        } else {

            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX - 26, posY, partialTick, font, player, forwardStack);
            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX - 26 - 20, posY, partialTick, font, player, selectedStack);
            CyclingSlotsRenderer.getSlotsRenderer()
                    .renderItemInSlot(guiGraphics, posX - 26 - 20 - 20, posY, partialTick, font, player, backwardStack);
        }
    }

    @Override
    public void renderItemInSlot(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack itemStack) {
        float popTime = CyclingInputHandler.getGlobalPopTime() - partialTick;
        this.renderSlot(guiGraphics, font, posX, posY, player, itemStack, popTime);
    }

    /**
     * @see Gui#renderSlot(GuiGraphics, int, int, DeltaTracker, Player, ItemStack, int)
     */
    private void renderSlot(GuiGraphics guiGraphics, Font font, int x, int y, Player player, ItemStack itemStack, float popTime) {
        if (!itemStack.isEmpty()) {
            if (popTime > 0.0F) {
                float g = 1.0F + popTime / 5.0F;
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(x + 8, y + 12);
                guiGraphics.pose().scale(1.0F / g, (g + 1.0F) / 2.0F);
                guiGraphics.pose().translate(-(x + 8), -(y + 12));
            }

            guiGraphics.renderItem(player, itemStack, x, y, 0);
            if (popTime > 0.0F) {
                guiGraphics.pose().popMatrix();
            }

            guiGraphics.renderItemDecorations(font, itemStack, x, y);
        }
    }
}
