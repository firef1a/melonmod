package dev.melonmod.render.hudElements;

import dev.melonmod.Mod;
import dev.melonmod.render.Scaler;

public class RectScreenObject extends RenderObject {
    public RectScreenObject() {
        super();
    }

    @Override
    public Scaler getScalerSize() {
        return Scaler.fromPosition(Mod.getWindowWidth(), Mod.getWindowHeight());
    }
}
