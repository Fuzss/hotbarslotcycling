package fuzs.hotbarslotcycling.neoforge.impl.client;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.HotbarSlotCyclingClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = HotbarSlotCycling.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HotbarSlotCyclingNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCyclingClient::new);
    }
}
