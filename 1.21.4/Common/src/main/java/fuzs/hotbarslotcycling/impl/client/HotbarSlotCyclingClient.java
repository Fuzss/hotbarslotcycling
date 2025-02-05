package fuzs.hotbarslotcycling.impl.client;

import fuzs.hotbarslotcycling.api.v1.client.HotbarCyclingProvider;
import fuzs.hotbarslotcycling.api.v1.client.SlotCyclingProvider;
import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.handler.CyclingInputHandler;
import fuzs.hotbarslotcycling.impl.client.handler.SlotsRendererHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.world.entity.player.Player;

public class HotbarSlotCyclingClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.START.register(CyclingInputHandler::onStartClientTick);
        InputEvents.MOUSE_SCROLL.register(CyclingInputHandler::onMouseScroll);
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) {
            RenderGuiLayerEvents.after(RenderGuiLayerEvents.HOTBAR)
                    .register(SlotsRendererHandler::onAfterRenderGuiLayer);
        }
    }

    @Override
    public void onClientSetup() {
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(HotbarSlotCycling.MOD_ID)) {
            SlotCyclingProvider.registerProvider((Player player) -> {
                return new HotbarCyclingProvider(player.getInventory());
            });
        }
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(CyclingInputHandler.CYCLE_LEFT_KEY_MAPPING, KeyActivationContext.GAME);
        context.registerKeyMapping(CyclingInputHandler.CYCLE_RIGHT_KEY_MAPPING, KeyActivationContext.GAME);
    }
}
