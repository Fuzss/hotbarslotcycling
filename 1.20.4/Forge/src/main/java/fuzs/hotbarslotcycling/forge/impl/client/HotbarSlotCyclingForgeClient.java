package fuzs.hotbarslotcycling.forge.impl.client;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.HotbarSlotCyclingClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = HotbarSlotCycling.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HotbarSlotCyclingForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCyclingClient::new);
    }
}
