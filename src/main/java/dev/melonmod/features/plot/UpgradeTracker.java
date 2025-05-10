package dev.melonmod.features.plot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.melonmod.FileManager;
import dev.melonmod.Mod;
import dev.melonmod.features.Feature;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpgradeTracker extends Feature {
    private static ArrayList<Text> signTooltip;
    private static Map<String, String> upgradeDescriptionMap;
    private static final String upgradeFileName = "melonmod_upgrades.json";
    public UpgradeTracker() throws IOException {
        init("upgradetracker", "Upgrade Tracker", "Tracks upgrades.");
        signTooltip = new ArrayList<>();
        upgradeDescriptionMap = new HashMap<>();
        JsonObject configJSON = new JsonObject();
        try {
            configJSON = new JsonParser().parse(FileManager.readFile(FileManager.getPath(upgradeFileName))).getAsJsonObject();
        } catch (NoSuchFileException e) {
            Mod.log(e.toString());
        }


        for (String key : configJSON.asMap().keySet()) {
            JsonElement element = configJSON.asMap().get(key);
            upgradeDescriptionMap.put(key, element.getAsString());
        }
    }

    public void clientStop(MinecraftClient minecraftClient) {
        save();
    }

    private void save() {
        try {
            JsonObject object = new JsonObject();

            for (String key : upgradeDescriptionMap.keySet()) {
                String data = upgradeDescriptionMap.get(key);
                object.addProperty(key, data);
            }

            FileManager.writeFile(FileManager.getPath(upgradeFileName), object.toString());
            Mod.LOGGER.info("Saved config: " + FileManager.getConfigFile().getName());
        } catch (Exception e) {
            Mod.LOGGER.info("Couldn't save config: " + e);
        }
    }

    public void onChatMessage(Text message, CallbackInfo ci) {
        String text = message.getString();
        Matcher matcher;

        matcher = Pattern.compile("> The (?:king|city|monarch|queen) has purchased the (.*) (?:upgrade|renovation|major upgrade|minor upgrade) for (\\d*) (?:Coins|Bank Gold)\\.", Pattern.CASE_INSENSITIVE).matcher(text);
        if (matcher.find()) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            Text hoverValue = (Text) hover.getValue(hover.getAction());
            Text data = hoverValue;
            Text upgrade_name = message.getSiblings().get(1).getSiblings().get(0);

            upgradeDescriptionMap.put(upgrade_name.getString(), data.getString());
            save();
        }

        matcher = Pattern.compile("✔ .{3,16} has unlocked (.*)\\.", Pattern.CASE_INSENSITIVE).matcher(text);
        if (matcher.find()) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            Text hoverValue = (Text) hover.getValue(hover.getAction());
            Text data = hoverValue;
            Text upgrade_name = message.getSiblings().get(3);

            upgradeDescriptionMap.put(upgrade_name.getString(), data.getString());
            save();
        }


        matcher = Pattern.compile("> The board has enacted the (.*) policy\\.", Pattern.CASE_INSENSITIVE).matcher(text);
        if (matcher.find()) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            Text hoverValue = (Text) hover.getValue(hover.getAction());
            Text data = hoverValue;
            Text upgrade_name = message.getSiblings().get(1).getSiblings().getFirst();

            upgradeDescriptionMap.put(upgrade_name.getString(), data.getString());
            save();
        }
        Mod.log(String.valueOf(message));
    }

    public void renderHUD(DrawContext context, RenderTickCounter tickCounter) {
        if (!signTooltip.isEmpty()) {
            context.drawTooltip(Mod.MC.textRenderer, signTooltip, Mod.getWindowWidth()/2, Mod.getWindowHeight()/2);
        }
    }

    public void renderWorld(WorldRenderContext worldRenderContext) {
        Vec3d hit = Mod.MC.player.raycast(5, 0, false).getPos();

        assert Mod.MC.world != null;
        BlockEntity state = Mod.MC.world.getBlockEntity(new BlockPos((int) hit.x-1, (int) hit.y, (int) hit.z-1));
        signTooltip = new ArrayList<>();
        if (state instanceof SignBlockEntity signBlock) {
            for (Text[] textList : new Text[][]{signBlock.getFrontText().getMessages(true), signBlock.getBackText().getMessages(true)}) {
                if (textList.length == 4 && !textList[0].getString().trim().isEmpty()) {
                    String top = textList[0].getString().trim();
                    String m = textList[1].getString().trim();
                    String m2 = textList[2].getString().trim();
                    if (!m2.isEmpty()) m += " " + m2;
                    if (m.contains("City Improvement:")) m = m2;
                    Mod.log("upgrade: " + m);

                    int color = 0xFFFFFF;

                    if (top.equals("[Right Click]")) color = 0xfcdb6d;
                    if (top.equals("Bought!")) color = Colors.GREEN;
                    if (top.equals("Can't Buy") || top.equals("Locked")) color = Colors.RED;

                    signTooltip = new ArrayList<>(List.of(
                            Text.literal(m).withColor(color),
                            Text.literal(upgradeDescriptionMap.getOrDefault(m, "<no description>")).withColor(Colors.LIGHT_GRAY)
                    ));
                }
            }
        }

    }
}
