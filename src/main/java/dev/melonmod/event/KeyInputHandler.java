package dev.melonmod.event;

import dev.melonmod.Mod;
import dev.melonmod.helper.CommandQueue;
import dev.melonmod.helper.CommandQueueHelper;
import dev.melonmod.plot.PlotTracker;
import dev.melonmod.utils.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = Mod.MOD_NAME;
    public static KeyBinding bankKeybind;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Screen cScreen = Mod.getCurrentScreen();

            if (bankKeybind.wasPressed()) {
                if (PlotTracker.isOnMelonKing()) {
                    ChatUtils.sendMessage("@bank");
                } else {
                    ChatUtils.displayMessage(Text.literal("You must be on Melon King to use that."));
                }
            }
        });
    }

    public static void register() {
        bankKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "/bank macro", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_V, // The keycode of the key
                KEY_CATEGORY // The translation key of the keybinding's category.
        ));


        registerKeyInputs();

    }
}