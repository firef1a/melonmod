package dev.melonmod.render.hudElements;

import dev.melonmod.render.ARGB;
import dev.melonmod.render.Alignment;
import dev.melonmod.render.Scaler;
import net.minecraft.client.gui.DrawContext;

public class ColorRect extends RenderObject{
    public ARGB color;

    public ColorRect(Scaler position, Scaler size, ARGB color, double zIndex, Alignment alignment, Alignment parentAlignment, boolean enabled) {
        super(position, size, zIndex, alignment, parentAlignment, enabled);
        this.color = color;
    }

    @Override
    public void internalRender(DrawContext context) { context.fill(getX1(), getY1(), getX2(), getY2(), color.getColor()); }
}
