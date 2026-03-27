# AutoBuy Fabric 1.16.5

Source code of a client-side Fabric mod for Minecraft `1.16.5` that automates auction scanning and item buying using configured price filters.

## Features

- Opens the in-game settings menu with the `M` key.
- Stores per-item price limits.
- Scans auction listings and matches items by type, enchantments, attributes, effects, lore and NBT fragments.
- Automatically confirms valid purchases.
- Supports multiple config profiles.
- Supports a dedicated mob filter for spawners.

## Tech Stack

- Minecraft `1.16.5`
- Fabric Loader `0.14.25`
- Yarn mappings `1.16.5+build.10`
- Java `8`
- Gradle with Fabric Loom

## Project Layout

```text
src/main/java       Java source files
src/main/resources  Fabric metadata, mixins and assets
build.gradle        Build configuration
gradle.properties   Versions and project properties
settings.gradle     Gradle project name and repositories
LICENSE             Source code license
```

## Build

This repository contains source code only. Build artifacts, Gradle caches and unrelated local files are intentionally excluded.

Requirements:

- Java `8`
- Gradle installed globally

Build command:

```bash
gradle build
```

The compiled jar will be generated in `build/libs`.

## Configs

The mod stores runtime configs in:

```text
mods/AutoBuy/config
```

Profiles can be saved, loaded and deleted from the in-game GUI.

## Repository Notes

- Repository name recommendation: `autobuy-fabric-1.16.5`
- GitHub description recommendation: `Client-side Fabric mod for Minecraft 1.16.5 that automates auction scanning and item buying by configured price filters.`
- Suggested topics: `minecraft`, `fabric`, `fabric-mod`, `minecraft-mod`, `autobuy`, `auction-bot`, `java`, `minecraft-1-16-5`
