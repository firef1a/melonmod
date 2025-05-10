package dev.melonmod.features;

import dev.melonmod.features.commands.CommandAliaser;
import dev.melonmod.features.commands.CommandHider;
import dev.melonmod.features.item.ItemLoreViewer;
import dev.melonmod.features.item.ItemTagViewer;
import dev.melonmod.features.plot.FullInventoryTracker;
import dev.melonmod.features.plot.UpgradeTracker;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Features {
    public static Map<String, Feature> featureMap = new HashMap<>();

    public static void init() throws IOException {
        add(new FeatureHudObjects());
        //add(new ItemTagViewer());

        //add(new ItemLoreViewer());
        add(new CommandHider());
        add(new CommandAliaser());
        add(new UpgradeTracker());
        add(new FullInventoryTracker());
    }

    private static void add(Feature feature) { featureMap.put(feature.getFeatureID(), feature); }
    public static void implement(Consumer<FeatureImpl> consumer) { featureMap.values().forEach((feature -> {if (feature.isEnabled()) consumer.accept(feature);})); }
    public static Text editChatMessage(Text base) {
        Text modified = base;
        for (Feature feature : featureMap.values()) {
            if (feature.isEnabled()) {
                modified = feature.modifyChatMessage(base, modified);
            }
        }
        return modified;
    }
}
