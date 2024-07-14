package fuzs.hotbarslotcycling.fabric.impl;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class HotbarSlotCyclingFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCycling::new);
    }
}
