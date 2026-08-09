package dev.worldgen.lithostitched.mixin.common.template.mansion.floor;

import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.impl.duck.MansionRoomDuck;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WoodlandMansionPieces.FirstFloorRoomCollection.class)
public abstract class FirstFloorMixin implements MansionRoomDuck {
    @Inject(method = "get1x1", at = @At("HEAD"), cancellable = true)
    public void use1x1TemplateList(RandomSource random, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity()) {
            cir.setReturnValue(lithostitched$getRandom("1x1", random));
        }
    }

    @Inject(method = "get1x1Secret", at = @At("HEAD"), cancellable = true)
    public void use1x1SecretTemplateList(RandomSource random, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity()) {
            cir.setReturnValue(lithostitched$getRandom("1x1_secret", random));
        }
    }

    @Inject(method = "get1x2SideEntrance", at = @At("HEAD"), cancellable = true)
    public void use1x2SideTemplateList(RandomSource random, boolean bl, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity() && !bl) {
            cir.setReturnValue(lithostitched$getRandom("1x2_side", random));
        }
    }

    @Inject(method = "get1x2FrontEntrance", at = @At("HEAD"), cancellable = true)
    public void use1x2FrontTemplateList(RandomSource random, boolean bl, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity() && !bl) {
            cir.setReturnValue(lithostitched$getRandom("1x2_front", random));
        }
    }

    @Inject(method = "get1x2Secret", at = @At("HEAD"), cancellable = true)
    public void use1x2SecretTemplateList(RandomSource random, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity()) {
            cir.setReturnValue(lithostitched$getRandom("1x2_secret", random));
        }
    }

    @Inject(method = "get2x2", at = @At("HEAD"), cancellable = true)
    public void use2x2TemplateList(RandomSource random, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity()) {
            cir.setReturnValue(lithostitched$getRandom("2x2", random));
        }
    }

    @Inject(method = "get2x2Secret", at = @At("HEAD"), cancellable = true)
    public void use2x2SecretTemplateList(RandomSource random, CallbackInfoReturnable<String> cir) {
        if (Lithostitched.breaksSeedParity()) {
            cir.setReturnValue(lithostitched$getRandom("2x2_secret", random));
        }
    }

    @Override
    public int lithostitched$floorNumber() {
        return 1;
    }
}
