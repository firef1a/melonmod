package dev.melonmod.plot;

import dev.melonmod.Mod;
import net.minecraft.util.math.Vec3d;

public class PlotTracker {
    public static boolean isOnMelonKing() {
        if (Mod.MC.player == null) return false;
        Vec3d pos = Mod.MC.player.getPos();
        Vec3d start = new Vec3d(-975, 0, -4270);
        Vec3d end = new Vec3d(-975+301, 255, -4270+301);

        boolean ix = (start.x <= pos.x && pos.x <= end.x);
        boolean iy = (start.y <= pos.y && pos.y <= end.y);
        boolean iz = (start.z <= pos.z && pos.z <= end.z);
        return ix && iy && iz;
    }
}
