# Extended Drawers
[![Badge showing the amount of downloads on modrinth](https://img.shields.io/badge/dynamic/json?color=2d2d2d&colorA=5da545&label=&suffix=%20downloads%20&query=downloads&url=https://api.modrinth.com/v2/project/AhtxbnpG&style=flat&logo=modrinth&logoColor=2d2d2d)](https://modrinth.com/mod/extended-drawers)
[![Badge showing the amount of downloads on curseforge](https://img.shields.io/badge/dynamic/json?query=value&url=https://img.shields.io/curseforge/dt/616602.json&label=&logo=curseforge&color=2d2d2d&style=flat&labelColor=F16436&logoColor=2d2d2d&suffix=%20downloads)](https://www.curseforge.com/minecraft/mc-mods/extended-drawers)
[![Badge linking to issues on github](https://img.shields.io/badge/dynamic/json?query=value&url=https://img.shields.io/github/issues-raw/mattidragon/extendeddrawers.json&label=&logo=github&color=2d2d2d&style=flat&labelColor=6e5494&logoColor=2d2d2d&suffix=%20issues)](https://github.com/MattiDragon/ExtendedDrawers/issues)
[![Badge linking to support on discord](https://img.shields.io/discord/760524772189798431?label=&logo=discord&color=2d2d2d&style=flat&labelColor=5865f2&logoColor=2d2d2d)](https://discord.gg/26T5KK2PBv)

A mod inspired by storage drawers that aims to allow for easy creation of powerful yet not
overpowered storage systems using simple components.

## Features
### In-game Guide Book
Crafted by combining a book with and upgrade frame, the guide book will explain in more detail how the mod works.
**[Patchouli](https://modrinth.com/mod/patchouli) is required for the guide book to load ingame!**

### Drawers
Drawers are blocks that can store a lot of a few items and come in three variants: single-, double- and quad-slot. 
They can be interacted with in world or using hopper or pipes from other mods.
They will make up the bulk of your storage as they are cheap and effective.

### Access Points
Access points are blocks that can interact with multiple drawers that are connected to them. 
They only support insertion in world, but can be extracted from using pipes and hoppers.
You can use them to quickly dump your inventory into the system, or hook up digital storage.

### Shadow Drawers
Shadow drawers show the amount of a resource that is available in the whole network and allows
for insertion and expansion of that resource. 
They are especially useful for automation as you can keep storage centralized and still access it from far away.

### Compacting Drawers
While slightly more expensive to craft compacting drawers offer a lot of convenience over regular drawers.
They automatically compress and decompress your items using 2x2 and 3x3 crafting recipes. 
They also store more of items as the max capacity is based on the most compressed stored item.

### Upgrades
Upgrades are items that extend the capacity of a drawer slot. Their power can be tweaked in the config.
There is also a downgrade if you need to limit the capacity of a drawer slot. 
They apply to individual slots and not the whole drawer, unlike other mods.

### Locking
You can use a lock on drawers to make them keep their selected item even when empty.
This makes sure you don't lose your organisation when you remove items and allows you to block automation from filling up empty drawers.

### Voiding mode
If sneak clicked with a lava bucket, drawers will start voiding excess items. 
This is useful when you want to store items of low value as you don't need to worry about overflow.
Extended drawers will make sure drawers without voiding enabled fill up first.

### Hidden mode
Hide icons on drawers to reduce lag or hide your valuables, but remember, anyone can change it.
Applied by sneaking and using black dye or ink sacs on drawers.

### Duping mode
As for 1.20, you can use a by default unobtainable dupe wand to toggle duping mode on drawers.
They will continue to provide items even when empty. Voiding mode must still be applied separately.

## Customization
Extended drawers offers two main ways to customize the experience: the configs and datapacks.

The configs can be located under the `config` directory in your installation root, under `extended_drawers`. 
There are two files: one for client side setting and one for settings needed on both the server and client. 

All recipes and loot tables can be modified by datapacks like with most mods. 
You can find some of the defaults under `src/main/resources` and others under `src/main/generated`.

Extended drawers also provides the option to override what items the compacting drawers can compact and how.
This is done through files placed under `data/<namespace>/extended_drawers/compression_overrides` in json format.
The files should consist of a single object with items as keys and compression levels as values. 
Here are some examples:

```json5
{ 
  // Adds compacting support to clays. It isn't default as you can't craft it into balls
  "clay_ball": 1,
  "clay": 4
}
```
```json5
{ 
  // You should never need to do this, but here's an example of how a file for iron might look like
  // You can see that numbers are based off of the amount of first item
  "iron_nugget": 1,
  "iron_ingot": 9,
  "iron_block": 81
}
```
```json5
{ 
  // Disables compacting for gold nuggets. 
  // You have to do this for each tier or something might break.
  "gold_nugget": 1
}
```

## Other Info
### Modpack permission
You can use this mod in any modpack as long as you don't reupload the mod. 
You can get a direct download link from modrinth if you need, 
but I'd prefer you create your modpacks on modrinth or curseforge directly.

### Other websites
This mod is only officially available on [curseforge](https://www.curseforge.com/minecraft/mc-mods/extended-drawers), [modrinth](https://modrinth.com/mod/extended-drawers) and [github](https://github.com/mattidragon/extendeddrawers). 
Any other sites are third-party reuploads and should not be trusted.

### Porting and Forking
You can read my policy on [forking and porting mods](https://gist.github.com/MattiDragon/6b9e71e8516447f53f0d5fb296ab8868).

Current target: 1.20.1 and 1.20.2.

### Incompatibilities
I intend to try and stay compatible with as many mods as possible, but might abandon support for some if it becomes too hard.

* **Sodium** is only compatible if [indium](https://modrinth.com/mod/indium) is installed.
* **Optifine** will never be officially supported. Might work, might not.

## Licencing 
Versions 1.3.0 and older and their source code are licensed under the MIT license. Newer versions
are licensed under the Apache License, Version 2.0. I chose to switch because I want to retain 
ownership of my code while still allowing forks and addons to use any license they want. 

You are free to use the mods code in any way you want as long as you follow the license and 
credit me for the original (link is enough).
