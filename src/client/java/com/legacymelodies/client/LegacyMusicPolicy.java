package com.legacymelodies.client;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.level.Level;

public final class LegacyMusicPolicy {
    private static final Set<Identifier> C418_BACKGROUND_MUSIC_EVENTS = Set.of(
        Identifier.withDefaultNamespace("music.menu"),
        Identifier.withDefaultNamespace("music.game"),
        Identifier.withDefaultNamespace("music.creative"),
        Identifier.withDefaultNamespace("music.credits"),
        Identifier.withDefaultNamespace("music.dragon"),
        Identifier.withDefaultNamespace("music.end"),
        Identifier.withDefaultNamespace("music.nether.nether_wastes"),
        Identifier.withDefaultNamespace("music.under_water")
    );

    private LegacyMusicPolicy() {
    }

    public static Music rewriteSituationalMusic(Minecraft client, Music selected) {
        if (client.player == null) {
            return selectMusic(selected, null, false, false);
        }
        return selectMusic(selected, client.player.level().dimension(), client.player.isUnderWater(),
            client.player.getAbilities().instabuild && client.player.getAbilities().mayfly);
    }

    static Music selectMusic(Music selected, ResourceKey<Level> dimension, boolean underwater, boolean creative) {
        // Preserve intentional silence, including screens that request no music.
        if (selected == null) {
            return null;
        }
        if (dimension == null) {
            return isAllowed(selected) ? selected : Musics.MENU;
        }
        if (Level.OVERWORLD.equals(dimension)) {
            if (underwater) {
                return Musics.UNDER_WATER;
            }
            return creative ? Musics.CREATIVE : Musics.GAME;
        }
        return isAllowed(selected) ? selected : null;
    }

    public static boolean isAllowed(Music music) {
        return music != null && C418_BACKGROUND_MUSIC_EVENTS.contains(music.sound().value().location());
    }
}
