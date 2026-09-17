# Void Miners Remastered 2.0.1 — Minecraft 1.21.1

## Added

- **Ores from other mods in the miners**, loaded only when the mod is installed (`neoforge:mod_loaded`):
  Mekanism, AllTheModium, AllTheOres, Oritech, Create, Immersive Engineering, Actually Additions,
  Ad Astra (per planet), Bigger Reactors, Create: Better Motors, Create Nuclear, Draconic Evolution,
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
- Recipes can require a block anywhere under the Miner Controller (`blockUnderneath`), also from KubeJS.
- Miners stop when a block sits between the controller and the glass panels.
- The miner laser stops at the block it hits.
- JEI: hold SHIFT for percentages, CTRL for the full weight/chance value; recipe ID shown.
- `c:gems/*` and `c:storage_blocks/*` tags.
- Controller item tooltips with energy usage, duration and capacity.
- Structures are also accepted rotated 90°.

## Changed

- Gems appear more often in the miners.
- Better performance with tick acceleration; item multipliers now use their decimal part.
- Glass Panel is breakable by hand and no longer suffocates or blocks mob spawning rules.

## Fixed

- Max Storage Upgrade T2 and T3 recipes required themselves.
- Translations other than English were registered under the wrong key and never showed.
