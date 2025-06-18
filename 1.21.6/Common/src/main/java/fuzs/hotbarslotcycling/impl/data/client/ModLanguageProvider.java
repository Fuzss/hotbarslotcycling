package fuzs.hotbarslotcycling.impl.data.client;

import fuzs.hotbarslotcycling.impl.HotbarSlotCycling;
import fuzs.hotbarslotcycling.impl.client.handler.CyclingInputHandler;
import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder builder) {
        builder.addKeyCategory(HotbarSlotCycling.MOD_ID, HotbarSlotCycling.MOD_NAME);
        builder.add(CyclingInputHandler.CYCLE_LEFT_KEY_MAPPING, "Cycle Hotbar Slot Left");
        builder.add(CyclingInputHandler.CYCLE_RIGHT_KEY_MAPPING, "Cycle Hotbar Slot Right");
    }
}
