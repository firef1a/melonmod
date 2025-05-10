package dev.melonmod.render;

import dev.melonmod.utils.ColorUtils;

public class ARGB {
    private final int rgb;
    private final double alpha;
    public ARGB(double alpha, int rgb) {
        this.alpha = alpha;
        this.rgb = rgb;
    }

    public int getColor() {
        return ColorUtils.getARGB(rgb, alpha);
    }
    public int getRGB() {
        return rgb;
    }
}



