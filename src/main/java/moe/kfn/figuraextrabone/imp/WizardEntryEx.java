package moe.kfn.figuraextrabone.imp;

import org.figuramc.figura.wizards.WizardEntry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class WizardEntryEx {
    public static WizardEntry SUPPORT_BLEND;

    public static void init() {
        try {
            SUPPORT_BLEND = WizardEntry.register("BLEND_MODEL", WizardEntry.PLAYER_MODEL);

            Field lookupField = WizardEntry.class.getDeclaredField("ENTRY_LOOKUP");
            lookupField.setAccessible(true);
            LinkedHashMap<String, WizardEntry> lookup = (LinkedHashMap<String, WizardEntry>) lookupField.get(null);

            LinkedHashMap<String, WizardEntry> newLookup = new LinkedHashMap<>();

            for (Map.Entry<String, WizardEntry> entry : lookup.entrySet()) {
                newLookup.put(entry.getKey(), entry.getValue());

                if (entry.getKey().equals("PLAYER_MODEL")) {
                    newLookup.put("BLEND_MODEL", SUPPORT_BLEND);
                }
            }

            lookup.clear();
            lookup.putAll(newLookup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
