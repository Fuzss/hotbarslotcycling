package fuzs.hotbarslotcycling.forge.impl;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(HotbarSlotCycling.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class HotbarSlotCyclingForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCycling::new);
    }
}
