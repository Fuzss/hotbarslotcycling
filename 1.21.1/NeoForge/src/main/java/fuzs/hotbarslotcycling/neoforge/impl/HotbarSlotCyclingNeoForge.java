package fuzs.hotbarslotcycling.neoforge.impl;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(HotbarSlotCycling.MOD_ID)
public class HotbarSlotCyclingNeoForge {

    public HotbarSlotCyclingNeoForge() {
        ModConstructor.construct(HotbarSlotCycling.MOD_ID, HotbarSlotCycling::new);
    }
}
