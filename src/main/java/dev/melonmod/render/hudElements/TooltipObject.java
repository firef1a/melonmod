package dev.melonmod.render.hudElements;

import dev.melonmod.Mod;
import dev.melonmod.render.Alignment;
import dev.melonmod.render.Scaler;
import net.minecraft.client.gui.DrawContext;

public class TooltipObject extends TextList {
    public TooltipObject(Scaler position, double zIndex, Alignment alignment, Alignment parentAlignment, boolean enabled) {
        super(position, zIndex, alignment, parentAlignment, enabled);
    }

    @Override
    public void internalRender(DrawContext context) {
        int x = getX1();
        int y = getY1();
        context.drawTooltip(Mod.MC.textRenderer, textList, x, y);
    }
}
