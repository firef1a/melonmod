package dev.melonmod.render.hudElements;

import dev.melonmod.features.Feature;
import dev.melonmod.render.ARGB;
import dev.melonmod.render.Alignment;
import dev.melonmod.render.Scaler;

public class ColorRectFeature extends ColorRect {
    public Feature feature;
    public ColorRectFeature(Scaler position, Scaler size, ARGB color, double zIndex, Alignment alignment, Alignment parentAlignment, Feature feature) {
        super(position, size, color, zIndex, alignment, parentAlignment, true);
        this.feature = feature;
    }

    @Override
    public boolean isEnabled() { return feature.isEnabled(); }

    @Override
    public void setEnabled(boolean e) { feature.setIsEnabled(e); }
}
