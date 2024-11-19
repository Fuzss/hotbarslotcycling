package fuzs.hotbarslotcycling.neoforge.impl.client;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.HotbarSlotCyclingClient;
import fuzs.hotbarslotcycling.impl.client.handler.SlotsRendererHandler;
import fuzs.hotbarslotcycling.impl.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@Mod(value = HotbarSlotCycling.MOD_ID, dist = Dist.CLIENT)
public class HotbarSlotCyclingNeoForgeClient {

    public HotbarSlotCyclingNeoForgeClient(ModContainer modContainer) {
        ClientModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCyclingClient::new);
        registerLoadingHandlers(modContainer.getEventBus());
        DataProviderHelper.registerDataProviders(HotbarSlotCycling.MOD_ID, ModLanguageProvider::new);
    }

    private static void registerLoadingHandlers(IEventBus eventBus) {
        eventBus.addListener((final RegisterGuiLayersEvent evt) -> {
            evt.registerAbove(VanillaGuiLayers.HOTBAR, HotbarSlotCycling.id("cycling_slots"),
                    (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
                        SlotsRendererHandler.onAfterRenderGuiLayer(Minecraft.getInstance().gui, guiGraphics, deltaTracker);
                    }
            );
        });
    }
}
