package fuzs.hotbarslotcycling.neoforge.impl;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.data.client.ModLanguageProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(HotbarSlotCycling.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HotbarSlotCyclingNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCycling::new);
        DataProviderHelper.registerDataProviders(HotbarSlotCycling.MOD_ID, ModLanguageProvider::new);
    }
}
