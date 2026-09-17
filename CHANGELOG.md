# Void Miners Remastered 2.0.1 — Minecraft 1.21.1

## Added

- **Ores from other mods in the miners**, loaded only when the mod is installed (`neoforge:mod_loaded`):
  Mekanism, AllTheModium, AllTheOres, Oritech, Create, Immersive Engineering, Actually Additions,
  Ad Astra (per planet), Bigger Reactors Revived, Create: Better Motors, Create Nuclear, Draconic Evolution,
  Ender IO Evolution, Energized Power, Malum, Mekanism Extras, Mekanism More Machine, Modern Foundry,
  Mystical Agriculture / Agradditions, Occultism, Powah, Small Progressions, UltimateFoods and XyCraft World.
  Each ore is available in the dimension where it generates.
- **Deepslate ores** (vanilla and modded) in the Overworld.
- **AllTheModium Mining dimension support**: every ore and every Void Miners gem can be mined there.
  AllTheModium's The Other gets its own ores too.
- **Gem output per miner tier**: gems now drop 8 / 9 / 12 / 15 / 19 / 34 / 40 / 55 / 64 per operation
  depending on the miner tier, and JEI shows the amount.
- **Configurable output side**: sneak + right-click a face of a formed Miner Controller to push the
  mined items into the inventory on that side. Same on the Solar Controller to choose which side
  outputs energy (all sides by default). Interval configurable with `EXPORT_INTERVAL_TICKS`.
- **Fusion connected textures** (optional): frames, glass and modifiers connect across the multiblock
  when Fusion is installed.
- JEI: hold SHIFT for percentages, CTRL for the full weight/chance value; recipe ID shown.
- `c:gems/*` and `c:storage_blocks/*` tags.

