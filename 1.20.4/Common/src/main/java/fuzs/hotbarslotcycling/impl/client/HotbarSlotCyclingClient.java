package fuzs.hotbarslotcycling.impl.client;

import fuzs.hotbarslotcycling.api.v1.client.HotbarCyclingProvider;
import fuzs.hotbarslotcycling.api.v1.client.SlotCyclingProvider;
import fuzs.hotbarslotcycling.impl.client.handler.CyclingInputHandler;
import fuzs.hotbarslotcycling.impl.client.handler.SlotsRendererHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderGuiCallback;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;

public class HotbarSlotCyclingClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ClientTickEvents.START.register(CyclingInputHandler::onClientTick$Start);
        InputEvents.BEFORE_MOUSE_SCROLL.register(CyclingInputHandler::onBeforeMouseScroll);
        RenderGuiCallback.EVENT.register(SlotsRendererHandler::onRenderGui);
    }

    @Override
    public void onClientSetup() {
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) {
            SlotCyclingProvider.registerProvider(player -> new HotbarCyclingProvider(player.getInventory()));
        }
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(CyclingInputHandler.CYCLE_LEFT_KEY_MAPPING, KeyActivationContext.GAME);
        context.registerKeyMapping(CyclingInputHandler.CYCLE_RIGHT_KEY_MAPPING, KeyActivationContext.GAME);
    }
}
