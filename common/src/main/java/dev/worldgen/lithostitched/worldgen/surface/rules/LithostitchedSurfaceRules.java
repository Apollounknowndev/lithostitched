package dev.worldgen.lithostitched.worldgen.surface.rules;

import com.google.common.collect.ImmutableList;
import dev.worldgen.lithostitched.worldgen.surface.technical.ContextExtension;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Surface rules, used for evaluating the surface rule stack.
 *
 * @author VoidsongDragonfly
 */
public class LithostitchedSurfaceRules {
    record NoiseThresholdSelectorRule(SurfaceRules.Context pContext, ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRules.SurfaceRule defaultRule, ImmutableList<SurfaceRules.SurfaceRule> ruleset, List<Double> lowerThresholds, boolean cascade) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the noise we're using for values, and grab our double value
            double d0 = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedNoise(noise);
            // Iterate through the rules to figure out which rule to provide, and return the rule for the noise bin we're in
            BlockState result = null;
            for(int i = 0; i < Math.min(lowerThresholds.size(), ruleset().size()); i++) {
                if(d0 > lowerThresholds.get(i)) {
                    result = ruleset.get(i).tryApply(x, y, z);
                    // Break the loop if we have gotten a value OR we're not cascading
                    if (result != null || !cascade) break;
                }
            }
            // Return the default rule if we're not in any noise bin or have a noise bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record NoiseThresholdRule(SurfaceRules.Context pContext, ResourceKey<NormalNoise.NoiseParameters> noise, SurfaceRules.SurfaceRule defaultRule, SurfaceRules.SurfaceRule rule, Double lowerThreshold) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the noise we're using for values, and grab our double value
            double d0 = ((ContextExtension)(Object)pContext).naturalphilosophy$getCachedNoise(noise);
            // Apply the rule and store the result, with null if we are not within the noise bin
            BlockState result = d0 > lowerThreshold ? rule.tryApply(x, y, z) : null;
            // Return the default rule if we're not in the noise bin or have a noise bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : null;
        }
    }

    record RandomThresholdSelectorRule(SurfaceRules.Context pContext, PositionalRandomFactory positionalRandomFactory, BlockState defaultState, List<BlockState> stateSet, List<Double> lowerThresholds) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the random we're using for values, and grab our double value
            RandomSource randomSource = positionalRandomFactory.at(pContext.blockX, pContext.blockY, pContext.blockZ);
            double d0 = randomSource.nextDouble();
            // Iterate through the rules to figure out which rule to provide, and return the rule for the noise bin we're in
            for(int i = 0; i < Math.min(lowerThresholds.size(), stateSet().size()); i++) {
                if(d0 > lowerThresholds.get(i)) return stateSet.get(i);
            }
            // Return the default rule if we're not in any random bin
            return defaultState;
        }
    }

    record RandomThresholdRule(SurfaceRules.Context pContext, PositionalRandomFactory positionalRandomFactory, BlockState defaultState, BlockState state, Double lowerThreshold) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check the random we're using for values, and grab our double value
            RandomSource randomSource = positionalRandomFactory.at(pContext.blockX, pContext.blockY, pContext.blockZ);
            // Return the state if we're in the random bin, because blockstates to place can't be null
            if(randomSource.nextDouble() > lowerThreshold) return state;
            // Return the default rule if we're not in the random bin
            return defaultState;
        }
    }

    record HeightThresholdSelectorRule(SurfaceRules.Context pContext, SurfaceRules.SurfaceRule defaultRule, ImmutableList<SurfaceRules.SurfaceRule> ruleset, List<Integer> lowerThresholds, int surfaceDepthMultiplier, boolean addStoneDepth, boolean cascade) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the height that we want to compare against
            int comparisonYValue = pContext.blockY + (addStoneDepth ? pContext.stoneDepthAbove : 0) - pContext.surfaceDepth * surfaceDepthMultiplier;
            // Iterate through the rules to figure out which rule to provide, and return the rule for the height bin we're in
            BlockState result = null;
            for(int i = 0; i < Math.min(lowerThresholds.size(), ruleset().size()); i++) {
                if(comparisonYValue > lowerThresholds.get(i)) {
                    result = ruleset.get(i).tryApply(x, y, z);
                    // Break the loop if we have gotten a value OR we're not cascading
                    if (result != null || !cascade) break;
                }
            }
            // Return the default rule if we're not in any height bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record HeightThresholdRule(SurfaceRules.Context pContext, SurfaceRules.SurfaceRule defaultRule, SurfaceRules.SurfaceRule rule, int lowerThreshold, int surfaceDepthMultiplier, boolean addStoneDepth) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the height that we want to compare against
            int comparisonYValue = pContext.blockY + (addStoneDepth ? pContext.stoneDepthAbove : 0) - pContext.surfaceDepth * surfaceDepthMultiplier;
            // Apply the rule and store the result, with null if we are not within the height bin
            BlockState result = comparisonYValue > lowerThreshold ? rule.tryApply(x, y, z) : null;
            // Return the default rule if we're not in the height bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : null;
        }
    }


    record StoneDepthThresholdSelectorRule(SurfaceRules.Context pContext, SurfaceRules.SurfaceRule defaultRule, ImmutableList<SurfaceRules.SurfaceRule> ruleset, int length) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Get the rule we want at the specified depth and evaluate it for this position
            BlockState result = pContext.stoneDepthAbove >= length ? null : ruleset.get(pContext.stoneDepthAbove - 1).tryApply(x, y, z);
            // Return the default rule if we're not in any depth bin or have a height bin that does not resolve
            return result == null ? defaultRule.tryApply(x, y, z) : result;
        }
    }

    record BilayerFillRule(SurfaceRules.Context pContext, boolean land, int surfaceOffset, int secondaryDepthRange, SurfaceRules.SurfaceRule topRule, SurfaceRules.SurfaceRule defaultRule) implements SurfaceRules.SurfaceRule {
        @Nullable
        @Override
        public BlockState tryApply(int x, int y, int z) {
            // Check to make sure we are in the correct land/water bin and return null if we fail
            if(land == (pContext.waterHeight != Integer.MIN_VALUE)) return null;
            // Check which bin we're in for surface rules
            if(pContext.stoneDepthAbove <= 1)
                return topRule.tryApply(x, y, z);
            // Calculate the secondary depth we need to check against, zero for no depth; this is after top check for performance
            int secondary = secondaryDepthRange == 0 ? 0 : (int) Mth.map(pContext.getSurfaceSecondary(), -1.0, 1.0, 0.0, secondaryDepthRange);
            // Second bin necessitates more checks to form the 'bottom' effectively
            if(pContext.stoneDepthAbove <= 1 + surfaceOffset + pContext.surfaceDepth + secondary)
                return defaultRule.tryApply(x, y, z);
                // Return a null BlockState in if we fail to be in either bin
            else return null;
        }
    }
}
