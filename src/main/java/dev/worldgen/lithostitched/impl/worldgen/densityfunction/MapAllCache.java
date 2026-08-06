package dev.worldgen.lithostitched.impl.worldgen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.IdentityHashMap;
import java.util.Map;

public class MapAllCache {
    private static final ThreadLocal<State> STATE = ThreadLocal.withInitial(State::new);

    public static DensityFunction get(DensityFunction.Visitor visitor, DensityFunction function) {
        State state = STATE.get();
        Map<DensityFunction, DensityFunction> vCache = state.cache.get(visitor);
        if (vCache != null) {
            return vCache.get(function);
        }
        return null;
    }

    public static void put(DensityFunction.Visitor visitor, DensityFunction function, DensityFunction result) {
        State state = STATE.get();
        state.cache.computeIfAbsent(visitor, k -> new IdentityHashMap<>()).put(function, result);
    }

    public static void push() {
        STATE.get().depth++;
    }

    public static void pop() {
        State state = STATE.get();
        state.depth--;
        if (state.depth <= 0) {
            state.depth = 0;
            state.cache.clear();
        }
    }

    private static class State {
        final Map<DensityFunction.Visitor, Map<DensityFunction, DensityFunction>> cache = new IdentityHashMap<>();
        int depth = 0;
    }
}