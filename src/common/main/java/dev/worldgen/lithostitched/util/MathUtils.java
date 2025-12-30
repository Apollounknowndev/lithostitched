package dev.worldgen.lithostitched.util;

import net.minecraft.util.Util;

public class MathUtils {
    private static final float[] SIN = Util.make(new float[65536], ($$0x) -> {
        for(int $$1 = 0; $$1 < $$0x.length; ++$$1) {
            $$0x[$$1] = (float)Math.sin((double)$$1 * Math.PI * (double)2.0F / (double)65536.0F);
        }

    });

    public static float sin(float value) {
        return SIN[(int)(value * 10430.378F) & '\uffff'];
    }

    public static float cos(float value) {
        return SIN[(int)(value * 10430.378F + 16384.0F) & '\uffff'];
    }
}
