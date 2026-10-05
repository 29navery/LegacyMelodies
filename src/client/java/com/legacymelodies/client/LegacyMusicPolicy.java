/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.Music
 *  net.minecraft.sounds.Musics
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.Level
 */
package com.legacymelodies.client;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public final class LegacyMusicPolicy {
    private static final Set<Identifier> C418_BACKGROUND_MUSIC_EVENTS = Set.of(Identifier.withDefaultNamespace((String)"music.menu"), Identifier.withDefaultNamespace((String)"music.game"), Identifier.withDefaultNamespace((String)"music.creative"), Identifier.withDefaultNamespace((String)"music.credits"), Identifier.withDefaultNamespace((String)"music.dragon"), Identifier.withDefaultNamespace((String)"music.end"), Identifier.withDefaultNamespace((String)"music.nether.nether_wastes"), Identifier.withDefaultNamespace((String)"music.under_water"));

    private LegacyMusicPolicy() {
    }

    public static Music rewriteSituationalMusic(Minecraft client, Music selected) {
        if (selected == null) {
            return null;
        }
        if (client.player == null) {
            return LegacyMusicPolicy.isAllowed(selected) ? selected : Musics.MENU;
        }
        if (client.player.level().dimension() == Level.OVERWORLD) {
            return LegacyMusicPolicy.legacyOverworldMusic(client);
        }
        if (client.player.level().dimension() == Level.NETHER) {
            return LegacyMusicPolicy.isAllowed(selected) ? selected : null;
        }
        return LegacyMusicPolicy.isAllowed(selected) ? selected : null;
    }

    public static boolean isAllowed(Music music) {
        return C418_BACKGROUND_MUSIC_EVENTS.contains(((SoundEvent)music.sound().value()).location());
    }

    private static Music legacyOverworldMusic(Minecraft client) {
        if (client.player.isUnderWater()) {
            return Musics.UNDER_WATER;
        }
        if (client.player.getAbilities().instabuild && client.player.getAbilities().mayfly) {
            return Musics.CREATIVE;
        }
        return Musics.GAME;
    }
}

