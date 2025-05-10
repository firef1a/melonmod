package dev.melonmod.config;

import com.google.gson.*;
import dev.melonmod.FileManager;
import dev.melonmod.Mod;
import dev.melonmod.features.Feature;
import dev.melonmod.features.Features;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;

import java.util.HashMap;

public class Config {
    public static JsonObject configJSON = new JsonObject();
    public static HashMap<String, Object> config;

    public static void loadConfig() {
        try {
            config = new HashMap<>();
            //Gson gson = new GsonBuilder().setPrettyPrinting().create();
            configJSON = new JsonParser().parse(FileManager.readConfig(FileManager.getConfigFile())).getAsJsonObject();

        } catch (Exception exception) {
            Mod.LOGGER.info("Config didn't load: " + exception);
        }
    }

    public static void save() {
        try {
            JsonObject object = new JsonObject();

            for (Feature feature : Features.featureMap.values()) {
                feature.saveConfig(object);
                object.addProperty(feature.getFeatureID() + ".enabled", feature.isEnabled());
            }

            FileManager.writeFile(FileManager.getConfigFile(), object.toString());
            Mod.LOGGER.info("Saved config: " + FileManager.getConfigFile().getName());
        } catch (Exception e) {
            Mod.LOGGER.info("Couldn't save config: " + e);
        }
    }

    public static YetAnotherConfigLib getLibConfig() {
        YetAnotherConfigLib.Builder yacl =
                YetAnotherConfigLib.createBuilder()
                        .title(Text.literal("Used for narration. Could be used to render a title in the future."))
                        .category(displayCategory().build());

        return yacl.save(Config::save).build();
    }


    private static ConfigCategory.Builder displayCategory() {
        ConfigCategory.Builder configBuilder = ConfigCategory.createBuilder()
                .name(Text.literal("HUD Displays"))
                .tooltip(Text.literal("Customize your HUD!"));


        for (String key : Features.featureMap.keySet()) {
            OptionGroup.Builder hudList = OptionGroup.createBuilder();
            Feature feature = Features.featureMap.get(key);
            hudList.name(Text.literal(feature.getFeatureName()))
                    .description(OptionDescription.of(Text.literal(feature.getDescription())))
                    .option(Option.createBuilder(boolean.class)
                            .name(Text.literal(feature.getFeatureName() + " Enabled"))
                            .description(OptionDescription.createBuilder()
                                    .text(Text.literal("Enable " + feature.getFeatureName()))
                                    .build())
                            .binding(
                                    feature.isEnabled(),
                                    feature::isEnabled,
                                    feature::setIsEnabled
                            )
                            .controller(TickBoxControllerBuilder::create)
                            .build());

            OptionGroup returnList = hudList.build();
            configBuilder.group(returnList);
        }



        // define group builders


        // return built category
        return configBuilder;
    }



}
