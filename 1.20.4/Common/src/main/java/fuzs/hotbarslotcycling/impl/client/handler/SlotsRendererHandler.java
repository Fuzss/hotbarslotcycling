package fuzs.hotbarslotcycling.impl.client.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.hotbarslotcycling.api.v1.client.CyclingSlotsRenderer;
import fuzs.hotbarslotcycling.api.v1.client.SlotCyclingProvider;
import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Objects;

public final class SlotsRendererHandler implements CyclingSlotsRenderer {
    private static final ResourceLocation HOTBAR_SPRITE = new ResourceLocation("hud/hotbar");
    private static final ResourceLocation HOTBAR_SELECTION_SPRITE = new ResourceLocation("hud/hotbar_selection");
    private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = new ResourceLocation("hud/hotbar_offhand_left");
    private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = new ResourceLocation("hud/hotbar_offhand_right");

    private static CyclingSlotsRenderer instance = new SlotsRendererHandler();

    public static void onRenderGui(Minecraft minecraft, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {

        if (!minecraft.options.hideGui && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {

            if (minecraft.getCameraEntity() instanceof Player player) {

                SlotCyclingProvider provider = SlotCyclingProvider.getProvider(player);
                if (provider != null) {

                    ItemStack forwardStack = provider.getForwardStack();
                    ItemStack selectedStack = provider.getSelectedStack();
                    ItemStack backwardStack = provider.getBackwardStack();
                    CyclingSlotsRenderer.getSlotsRenderer().renderSlots(guiGraphics, screenWidth, screenHeight, partialTicks, minecraft.font, (Player) minecraft.getCameraEntity(), backwardStack, selectedStack, forwardStack);
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

        if (HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsDisplayState == ClientConfig.SlotsDisplayState.NEVER) return;
        if (!this.testValidStacks(backwardStack, selectedStack, forwardStack)) return;

        boolean renderToRight = player.getMainArm().getOpposite() == HumanoidArm.LEFT;

        if (ItemStack.matches(forwardStack, backwardStack)) {
            if (renderToRight) {
                forwardStack = ItemStack.EMPTY;
            } else {
                backwardStack = ItemStack.EMPTY;
            }
        }

        int posX = screenWidth / 2 + (91 + HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsXOffset) * (renderToRight ? 1 : -1);
        int posY = screenHeight - HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsYOffset;
        if (HotbarSlotCycling.CONFIG.get(ClientConfig.class).slotsDisplayState == ClientConfig.SlotsDisplayState.KEY) {
            if (CyclingInputHandler.getSlotsDisplayTicks() > 0) {
                posY += (int) ((screenHeight - posY + 23) * (1.0F - Math.min(1.0F, (CyclingInputHandler.getSlotsDisplayTicks() - partialTick) / 5.0F)));
            } else {
                return;
            }
        }

        this.renderSlotBackgrounds(guiGraphics, posX, posY, !forwardStack.isEmpty(), !backwardStack.isEmpty(), renderToRight);
        this.renderSlotItems(guiGraphics, posX, posY - (16 + 3), partialTick, font, player, selectedStack, forwardStack, backwardStack, renderToRight);
    }

    @Override
    public boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
        return !backwardStack.isEmpty() && !selectedStack.isEmpty() && !forwardStack.isEmpty();
    }

    @Override
    public void renderSlotBackgrounds(GuiGraphics guiGraphics, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (renderToRight) {

            // left slot cycling slot, use right offhand sprite as it lines up
            guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, posX, posY - 23, 29, 24);
            if (renderForwardStack) {

                // right slot cycling slot, use left offhand sprite for better resource pack support in case something is intentionally different?
                guiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, posX + 40 + 7, posY - 23, 29, 24);
            }

            // center slot cycling slot, we use the second hotbar slot for that
            // the hotbar sprite can only be rendered as a whole, so enable scissor to only get what we want
            // alternative to scissor without providing a dedicated texture would be to dynamically copy part of the vanilla hotbar texture using a runtime resource pack
            guiGraphics.enableScissor(posX + 28, posY - 22, posX + 28 + 20, posY - 22 + 22);
            guiGraphics.blitSprite(HOTBAR_SPRITE, posX + 28 - 21, posY - 22, 182, 22);
            guiGraphics.disableScissor();
            // selected hotbar slot overlay
            guiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, posX + 26, posY - 22 - 1, 24, 24);
        } else {

            if (renderBackwardStack) {

                // left slot cycling slot, use right offhand sprite as it lines up
                guiGraphics.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, posX - 29 - 40 - 7, posY - 23, 29, 24);
            }

            // right slot cycling slot, use left offhand sprite for better resource pack support in case something is intentionally different?
            guiGraphics.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, posX - 29, posY - 23, 29, 24);
            // center slot cycling slot, we use the second hotbar slot for that
            // the hotbar sprite can only be rendered as a whole, so enable scissor to only get what we want
            // alternative to scissor without providing a dedicated texture would be to dynamically copy part of the vanilla hotbar texture using a runtime resource pack
            guiGraphics.enableScissor(posX - 29 - 19, posY - 22, posX - 29 - 19 + 20, posY - 22 + 22);
            guiGraphics.blitSprite(HOTBAR_SPRITE, posX - 29 - 19 - 21, posY - 22, 182, 22);
            guiGraphics.disableScissor();
            // selected hotbar slot overlay
            guiGraphics.blitSprite(HOTBAR_SELECTION_SPRITE, posX - 29 - 21, posY - 22 - 1, 24, 24);
        }
    }

    @Override
    public void renderSlotItems(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight) {

        if (renderToRight) {

            this.renderItemInSlot(guiGraphics, posX + 10, posY, partialTick, font, player, backwardStack);
            this.renderItemInSlot(guiGraphics, posX + 10 + 20, posY, partialTick, font, player, selectedStack);
            this.renderItemInSlot(guiGraphics, posX + 10 + 20 + 20, posY, partialTick, font, player, forwardStack);
        } else {

            this.renderItemInSlot(guiGraphics, posX - 26, posY, partialTick, font, player, forwardStack);
            this.renderItemInSlot(guiGraphics, posX - 26 - 20, posY, partialTick, font, player, selectedStack);
            this.renderItemInSlot(guiGraphics, posX - 26 - 20 - 20, posY, partialTick, font, player, backwardStack);
        }
    }

    @Override
    public void renderItemInSlot(GuiGraphics guiGraphics, int posX, int posY, float partialTick, Font font, Player player, ItemStack stack) {

        if (!stack.isEmpty()) {

            float popTime = CyclingInputHandler.getGlobalPopTime() - partialTick;
            if (popTime > 0.0F) {

                float f1 = 1.0F + popTime / 5.0F;
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(posX + 8, posY + 12, 0.0D);
                guiGraphics.pose().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                guiGraphics.pose().translate(-(posX + 8), -(posY + 12), 0.0D);
                RenderSystem.applyModelViewMatrix();
            }

            guiGraphics.renderItem(player, stack, posX, posY, 0);
            if (popTime > 0.0F) {

                guiGraphics.pose().popPose();
            }

            guiGraphics.renderItemDecorations(font, stack, posX, posY);
        }
    }
}
