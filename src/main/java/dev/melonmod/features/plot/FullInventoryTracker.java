package dev.melonmod.features.plot;

import dev.melonmod.Mod;
import dev.melonmod.features.Feature;
import dev.melonmod.plot.PlotTracker;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.MultipliedFloatSupplier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

import javax.swing.text.Style;

public class FullInventoryTracker extends Feature {
    private static boolean lastFull = false;
    public FullInventoryTracker() {
        init("inventoryTracker", "Full Inventory Notifier", "Notifies you when your inventory is full.");
    }

    public void tick() {
        if (Mod.MC.player == null) return;
        if (Mod.MC.player.getPos() == null) return;
        if (!PlotTracker.isOnMelonKing()) return;

        boolean isFull = true;
        for (ItemStack itemStack : Mod.MC.player.getInventory().main) {
            if (itemStack.isEmpty()) {
                isFull = false;
                lastFull = false;
                break;
            }
        }
        if (isFull && !lastFull) {
            lastFull = true;
            Mod.MC.inGameHud.setTitle(Text.empty());
            Mod.MC.inGameHud.setSubtitle(Text.literal("Inventory Full!").withColor(Colors.LIGHT_RED).styled((style -> style.withItalic(true))));
            Mod.MC.getSoundManager().play(new EntityTrackingSoundInstance(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.BLOCKS, 1f, 1f, Mod.MC.player, (long) (1000*Math.random()) + 10000L));
            Mod.MC.inGameHud.setTitleTicks(0, 60, 5);
        }
    }
}
