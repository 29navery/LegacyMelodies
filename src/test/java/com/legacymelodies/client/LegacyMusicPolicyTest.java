package com.legacymelodies.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LegacyMusicPolicyTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void menusReplaceModernMusicAndPreserveCredits() {
        assertSame(Musics.MENU, LegacyMusicPolicy.selectMusic(music("music.overworld.cherry_grove"), null, false, false));
        assertSame(Musics.MENU, LegacyMusicPolicy.selectMusic(Musics.MENU, null, false, false));
        assertSame(Musics.CREDITS, LegacyMusicPolicy.selectMusic(Musics.CREDITS, null, false, false));
    }

    @Test
    void overworldUsesClassicSurvivalCreativeAndUnderwaterPools() {
        var modern = music("music.overworld.lush_caves");
        assertSame(Musics.GAME, LegacyMusicPolicy.selectMusic(modern, Level.OVERWORLD, false, false));
        assertSame(Musics.CREATIVE, LegacyMusicPolicy.selectMusic(modern, Level.OVERWORLD, false, true));
        assertSame(Musics.UNDER_WATER, LegacyMusicPolicy.selectMusic(modern, Level.OVERWORLD, true, false));
        assertSame(Musics.UNDER_WATER, LegacyMusicPolicy.selectMusic(modern, Level.OVERWORLD, true, true));
    }

    @Test
    void netherRetainsClassicMusicAndBlocksModernBiomeEvents() {
        var classic = music("music.nether.nether_wastes");
        assertSame(classic, LegacyMusicPolicy.selectMusic(classic, Level.NETHER, false, false));
        assertNull(LegacyMusicPolicy.selectMusic(music("music.nether.crimson_forest"), Level.NETHER, false, false));
        assertNull(LegacyMusicPolicy.selectMusic(music("music.nether.basalt_deltas"), Level.NETHER, false, false));
    }

    @Test
    void endPreservesDragonAndEndMusic() {
        assertSame(Musics.END, LegacyMusicPolicy.selectMusic(Musics.END, Level.END, false, false));
        assertSame(Musics.END_BOSS, LegacyMusicPolicy.selectMusic(Musics.END_BOSS, Level.END, false, false));
        assertNull(LegacyMusicPolicy.selectMusic(music("other_mod:music.end"), Level.END, false, false));
    }

    @Test
    void intentionalSilenceAndNullInputsAreSafe() {
        assertNull(LegacyMusicPolicy.selectMusic(null, null, false, false));
        assertNull(LegacyMusicPolicy.selectMusic(null, Level.OVERWORLD, false, false));
        assertFalse(LegacyMusicPolicy.isAllowed(null));
        assertFalse(LegacyMusicPolicy.isAllowed(music("other_mod:music.game")));
        assertFalse(LegacyMusicPolicy.isAllowed(music("music_disc.pigstep")));
    }

    @Test
    void soundPoolsReplaceVanillaAndContainExactlyTheClassicTracks() throws Exception {
        JsonObject sounds;
        try (var input = getClass().getResourceAsStream("/assets/minecraft/sounds.json")) {
            assertNotNull(input);
            sounds = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        Set<String> expected = Set.of(
            "music/menu/beginning_2", "music/menu/floating_trees", "music/menu/moog_city_2", "music/menu/mutation",
            "music/game/clark", "music/game/danny", "music/game/dry_hands", "music/game/haggstrom", "music/game/key",
            "music/game/living_mice", "music/game/mice_on_venus", "music/game/minecraft", "music/game/oxygene",
            "music/game/subwoofer_lullaby", "music/game/sweden", "music/game/wet_hands",
            "music/game/creative/aria_math", "music/game/creative/biome_fest", "music/game/creative/blind_spots",
            "music/game/creative/dreiton", "music/game/creative/haunt_muskie", "music/game/creative/taswell",
            "music/game/end/alpha", "music/game/end/boss", "music/game/end/the_end",
            "music/game/nether/ballad_of_the_cats", "music/game/nether/concrete_halls", "music/game/nether/dead_voxel", "music/game/nether/warmth",
            "music/game/water/axolotl", "music/game/water/dragon_fish", "music/game/water/shuniji"
        );
        Set<String> recordings = new HashSet<>();
        assertEquals(8, sounds.size());
        for (var entry : sounds.entrySet()) {
            assertTrue(LegacyMusicPolicy.isAllowed(music(entry.getKey())));
            JsonObject pool = entry.getValue().getAsJsonObject();
            assertTrue(pool.get("replace").getAsBoolean());
            assertFalse(pool.getAsJsonArray("sounds").isEmpty());
            for (var sound : pool.getAsJsonArray("sounds")) {
                var object = sound.getAsJsonObject();
                String name = object.get("name").getAsString();
                if (object.has("type") && object.get("type").getAsString().equals("event")) {
                    assertEquals("music.game", name);
                    assertTrue(sounds.has(name));
                } else {
                    assertTrue(object.get("stream").getAsBoolean());
                    assertTrue(recordings.add(name), "duplicate recording: " + name);
                }
            }
        }
        assertEquals(expected, recordings);
    }

    @Test
    void mixinTargetsStillExistWithTheRequiredSignatures() throws Exception {
        assertEquals(Music.class, Minecraft.class.getDeclaredMethod("getSituationalMusic").getReturnType());
        assertEquals(void.class, MusicManager.class.getDeclaredMethod("startPlaying", Music.class).getReturnType());
    }

    private static Music music(String name) {
        return new Music(Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.parse(name))), 12000, 24000, false);
    }
}
