package dev.melonmod.features.plot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.melonmod.FileManager;
import dev.melonmod.Mod;
import dev.melonmod.features.Feature;
import dev.melonmod.plot.PlotTracker;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.*;
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
    private static ArrayList<ArrayList<Vec3d>> playerListHitLocations;
    private static final String upgradeFileName = "melonmod_upgrades.json";
    public UpgradeTracker() throws IOException {
        init("upgradetracker", "Upgrade Tracker", "Tracks upgrades.");
        signTooltip = new ArrayList<>();
        playerListHitLocations = new ArrayList<>();
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

    private static String serializeVec(Vec3d vec) {
        return "<" + (int) vec.x + ", " + (int) vec.y + ", " + (int) vec.z + ">";
    }

    public void tick() {
        if (!PlotTracker.isOnMelonKing()) return;
        if (Mod.MC.player == null) return;
        if (Mod.MC.world == null) return;
        ArrayList<Vec3d> appendList = new ArrayList<>();
        for (AbstractClientPlayerEntity playerEntity : Mod.MC.world.getPlayers()) {
            if (!playerEntity.getPos().isWithinRangeOf(Mod.MC.player.getPos(), 600,600)) continue;
            Vec3d hit = playerEntity.raycast(4.5, 0, false).getPos().subtract(1, 0, 1);
            appendList.add(hit);
        }
        playerListHitLocations.addFirst(appendList);
        if (playerListHitLocations.size() > 3) {
            playerListHitLocations.removeLast();
        }
    }

    public void onChatMessage(Text message, CallbackInfo ci) {
        if (!PlotTracker.isOnMelonKing()) return;
        if (Mod.MC.player == null) return;
        String text = message.getString();
        Matcher matcher;

        matcher = Pattern.compile("> The (king|city|monarch|queen) has purchased the (.*) (?:upgrade|renovation|major upgrade|minor upgrade) for (\\d*) (?:Coins|Bank Gold|Research|Favors)\\.", Pattern.CASE_INSENSITIVE).matcher(text);
        if (matcher.find()) {
            HoverEvent hover = message.getStyle().getHoverEvent();
            Text hoverValue = (Text) hover.getValue(hover.getAction());
            String data = hoverValue.getString();
            String upgrade_name = message.getSiblings().get(1).getSiblings().get(0).getString();

            if (upgrade_name.equals("Upgrade Town") && Mod.MC.world != null) {
                String positionKey = null;
                for (ArrayList<Vec3d> hitList : playerListHitLocations) {
                    for (Vec3d hit : hitList) {
                        String vecKey = serializeVec(hit);
                        BlockEntity state = Mod.MC.world.getBlockEntity(new BlockPos((int) hit.x, (int) hit.y, (int) hit.z));

                        if (state instanceof SignBlockEntity signBlock) {
                            for (Text[] textList : new Text[][]{signBlock.getFrontText().getMessages(true), signBlock.getBackText().getMessages(true)}) {
                                if (textList.length == 4 && !textList[0].getString().trim().isEmpty()) {
                                    String top = textList[0].getString().trim();
                                    String m = textList[1].getString().trim();
                                    String m2 = textList[2].getString().trim();
                                    if (!m2.isEmpty()) m += " " + m2;
                                    if (m.contains("Upgrade Town")) {
                                        positionKey = vecKey;
                                        break;
                                    }
                                }
                            }
                        }
                        if (positionKey != null) break;
                    }
                    if (positionKey != null) break;
                }
                if (positionKey != null) {
                    upgrade_name = upgrade_name + positionKey;
                    Mod.log("LOGGED UPGRADE: " + upgrade_name + " desc: " + data);
                }
            }

            upgradeDescriptionMap.put(upgrade_name, data);
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
        if (!PlotTracker.isOnMelonKing()) return;
        if (Mod.MC.player == null) return;
        if (!signTooltip.isEmpty()) {
            context.drawTooltip(Mod.MC.textRenderer, signTooltip, Mod.getWindowWidth()/2, Mod.getWindowHeight()/2);
        }
    }

    public static Text convertOrderedTextToTextWithStyle(OrderedText orderedText) {
        List<Text> components = new ArrayList<>();
        StringBuilder currentText = new StringBuilder();
        final Style[] currentStyle = {Style.EMPTY};

        orderedText.accept((index, style, codePoint) -> {
            if (!style.equals(currentStyle[0])) {
                if (!currentText.isEmpty()) {
                    components.add(Text.literal(currentText.toString()).setStyle(currentStyle[0]));
                    currentText.setLength(0);
                }
                currentStyle[0] = style;
            }
            currentText.appendCodePoint(codePoint);
            return true;
        });

        if (!currentText.isEmpty()) {
            components.add(Text.literal(currentText.toString()).setStyle(currentStyle[0]));
        }

        return Texts.join(components, Text.empty());
    }

    public void renderWorld(WorldRenderContext worldRenderContext) {
        if (!PlotTracker.isOnMelonKing()) return;
        if (Mod.MC.player == null) return;
        if (Mod.MC.player.getPos() == null) return;
        if (Mod.MC.world == null) return;

        Vec3d hit = Mod.MC.player.raycast(4.5, 0, false).getPos().subtract(1, 0, 1);
        String vecKey = serializeVec(hit);
        BlockEntity state = Mod.MC.world.getBlockEntity(new BlockPos((int) hit.x, (int) hit.y, (int) hit.z));

        signTooltip = new ArrayList<>();
        if (state instanceof SignBlockEntity signBlock) {
            for (Text[] textList : new Text[][]{signBlock.getFrontText().getMessages(true), signBlock.getBackText().getMessages(true)}) {
                if (textList.length == 4 && !textList[0].getString().trim().isEmpty()) {
                    String top = textList[0].getString().trim();
                    String m = textList[1].getString().trim();
                    String m2 = textList[2].getString().trim();
                    if (!m2.isEmpty()) m += " " + m2;
                    if (m.contains("City Improvement:")) m = m2;

                    int color = 0xFFFFFF;

                    if (top.equals("[Right Click]")) color = 0xfcdb6d;
                    if (top.equals("Bought!")) color = Colors.GREEN;
                    if (top.equals("Can't Buy") || top.equals("Locked") || top.equals("Path Locked!") || Pattern.compile(".\\d/.\\d").matcher(top).find()) color = Colors.RED;


                    String mkey = m;
                    if (m.equals("Upgrade Town")) {
                        mkey = m + vecKey;
                    }
                    if (!upgradeDescriptionMap.containsKey(mkey)) return;
                    String upgradeDesc = upgradeDescriptionMap.get(mkey);
                    Mod.log("n:" + mkey + " d: " + upgradeDesc);

                    List<OrderedText> otList = Mod.MC.textRenderer.wrapLines(StringVisitable.plain(upgradeDesc), Mod.getWindowWidth() / 3);

                    signTooltip = new ArrayList<>(List.of(Text.literal(m).withColor(color)));
                    for (OrderedText t : otList) {
                        Text nt = convertOrderedTextToTextWithStyle(t);
                        signTooltip.add(Text.literal(nt.getString()).withColor(Colors.LIGHT_GRAY));
                    }
                }
            }
        }

    }
}
