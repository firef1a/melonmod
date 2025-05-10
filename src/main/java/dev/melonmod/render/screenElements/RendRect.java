package dev.melonmod.render.screenElements;

import dev.melonmod.render.Point2i;
import net.minecraft.client.gui.DrawContext;

public interface RendRect {
    void render(DrawContext drawContext, Point2i mouse);
}
