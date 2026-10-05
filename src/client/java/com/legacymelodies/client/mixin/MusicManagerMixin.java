package com.legacymelodies.client.mixin;

import com.legacymelodies.client.LegacyMusicPolicy;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MusicManager.class})
public abstract class MusicManagerMixin {
    @Inject(method={"startPlaying"}, at={@At(value="HEAD")}, cancellable=true)
    private void legacy_melodies$blockNonLegacyMusic(Music music, CallbackInfo ci) {
        if (!LegacyMusicPolicy.isAllowed(music)) {
            ci.cancel();
        }
    }
}
