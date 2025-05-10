package dev.melonmod.render.hudElements;

import dev.melonmod.render.ARGB;
import dev.melonmod.render.Alignment;
import dev.melonmod.render.Point2i;
import dev.melonmod.render.Scaler;

public class ColorRectContainer extends ColorRect {
    private final int margin;
    public ColorRectContainer(Scaler position, int margin, ARGB color, double zIndex, Alignment alignment, Alignment parentAlignment, boolean enabled) {
        super(position, new Scaler(0,0), color, zIndex, alignment, parentAlignment, enabled);
        this.margin = margin;
    }

    @Override
    public Point2i getScreenPosition() {
        return super.getScreenPosition().add(-margin, -margin);
    }

    @Override
    public Scaler getScalerSize() {
        double maxWidth = 0;
        double maxHeight = 0;
        for (RenderObject object : siblings) {
            Point2i size = object.getScalerSize().getScreenPosition();
            if (size.x > maxWidth) { maxWidth = size.x; }
            if (size.y > maxHeight) { maxHeight = size.y; }
        }
        return Scaler.fromPosition(maxWidth+margin*2, maxHeight+margin*2);
    }
}
