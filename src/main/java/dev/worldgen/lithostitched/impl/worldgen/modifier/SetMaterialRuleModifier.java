package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.util.InjectionType;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.HolderReferenceAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.SequenceRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SetMaterialRuleModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<MaterialRule> targetRules, Holder<MaterialRule> materialRule, InjectionType injectionType) implements WorldgenModifier {
	public static final MapCodec<SetMaterialRuleModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
		PRIORITY_DEFAULT_CODEC.forGetter(WorldgenModifier::priority),
		RegistryCodecs.holderSet(Registries.MATERIAL_RULE).fieldOf("target_rules").forGetter(SetMaterialRuleModifier::targetRules),
		RegistryCodecs.holder(Registries.MATERIAL_RULE).fieldOf("material_rule").forGetter(SetMaterialRuleModifier::materialRule),
		InjectionType.CODEC.fieldOf("injection_type").orElse(InjectionType.PREPEND).forGetter(SetMaterialRuleModifier::injectionType)
	).apply(instance, SetMaterialRuleModifier::new));
	
	@Override
	public void apply(RegistryAccess registries) {
		for (Holder<MaterialRule> targetRule : this.targetRules) {
			if (targetRule instanceof Holder.Reference<MaterialRule> reference) {
				HolderReferenceAccessor<MaterialRule> accessor = ((HolderReferenceAccessor<MaterialRule>)reference);
				accessor.setValue(this.wrap(reference.value()));
			}
		}
	}
	
	private MaterialRule wrap(MaterialRule target) {
		MaterialRule injected = this.materialRule.value();
		
		if (this.injectionType == InjectionType.REPLACE) return injected;
		
		if (target instanceof SequenceRule(List<MaterialRule> sequence)) {
			List<MaterialRule> mergedRules = new ArrayList<>(sequence);
			switch (this.injectionType) {
				case PREPEND -> mergedRules.addFirst(injected);
				case APPEND -> mergedRules.add(injected);
			}
			return MaterialRules.sequence(mergedRules);
		}
		
		return switch (this.injectionType) {
			case PREPEND -> MaterialRules.sequence(injected, target);
			case APPEND -> MaterialRules.sequence(target, injected);
			default -> injected;
		};
	}
	
	@Override
	public MapCodec<? extends WorldgenModifier> codec() {
		return CODEC;
	}
}
