package com.legacymelodies.client.mixin;

import com.legacymelodies.client.LegacyMusicPolicy;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Minecraft.class})
public abstract class MinecraftMixin {
    @Inject(method={"getSituationalMusic"}, at={@At(value="RETURN")}, cancellable=true)
    private void legacy_melodies$restoreLegacyMusicSelection(CallbackInfoReturnable<Music> cir) {
        cir.setReturnValue(LegacyMusicPolicy.rewriteSituationalMusic((Minecraft) (Object) this, cir.getReturnValue()));
    }
}
