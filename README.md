# Legacy Melodies

A client-side Fabric mod for **Minecraft 26.3** that restores classic music selection and restricts background music to C418's tracks already included with Minecraft.

## Install

Requires **Java 25**, **Fabric Loader 0.19.5 or newer**, and **Fabric API for Minecraft 26.3**. Place `legacy-melodies-0.2.1.jar` in your instance's `mods` folder. Use the regular JAR, not the `-sources.jar`.

## Behavior

- Overworld: classic survival music, creative music while in creative flight mode, and underwater music while submerged. Underwater music takes priority.
- Menus and credits: the original C418 selections.
- Nether: C418's Nether Wastes pool is allowed; newer biome-specific events are silenced, matching the recovered mod's policy.
- End: C418's End and dragon-fight music is preserved.

The vanilla menu, survival, and Nether Wastes pools now contain music by newer composers. This mod replaces those pools with their C418 selections; merely filtering event names would still let newer tracks through. All recordings are referenced from Minecraft's own assets, with no audio files bundled. Music discs are unchanged. User resource packs can override the sound definitions.

## Build

With a JDK 25 installation, run:

```sh
./gradlew clean build
```

On Windows, use `gradlew.bat clean build`. Output is in `build/libs/`. GitHub Actions builds pushes and uploads the JARs under **Artifacts** on the workflow run.
