package dev.melonmod.features.plot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.random.Random;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;

public class ConstFloatProvider extends FloatProvider {
    public static final ConstFloatProvider ZERO = new ConstFloatProvider(0.0F);
    public static final MapCodec<net.minecraft.util.math.floatprovider.ConstantFloatProvider> CODEC;
    private final float value;

    public static ConstFloatProvider create(float value) {
        return value == 0.0F ? ZERO : new ConstFloatProvider(value);
    }

    public ConstFloatProvider(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

    public float get(Random random) {
        return this.value;
    }

    public float getMin() {
        return this.value;
    }

    public float getMax() {
        return this.value;
    }

    public FloatProviderType<?> getType() {
        return FloatProviderType.CONSTANT;
    }

    public String toString() {
        return Float.toString(this.value);
    }

    static {
        CODEC = Codec.FLOAT.fieldOf("value").xmap(net.minecraft.util.math.floatprovider.ConstantFloatProvider::create, net.minecraft.util.math.floatprovider.ConstantFloatProvider::getValue);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
