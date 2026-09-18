# Void Miners Remastered 2.0.2 — Minecraft 1.21.1

## Added

- **Face Configurator**: right-click a face of a Miner or Solar Controller with it to choose the side
  it outputs to. The Miner pushes its mined items into the inventory on that side, the Solar Controller
  sends its energy there. Right-click the same face again to turn it off. Sneak + right-click on the
  controller does the same without the item.
- **Gem output per miner tier**, off by default: turn on `GEM_OUTPUT_PER_TIER_ENABLED` in
  `voidminers-miners.json5` and each operation gives 8 / 9 / 12 / 15 / 19 / 34 / 40 / 55 / 64 gems
  depending on the miner tier. The amounts are configurable and JEI shows them.

## Changed

- Max Storage Upgrades are now **Storage Upgrade MK1 / MK2 / MK3**, with new textures. Existing
  upgrades in inventories and installed in miners are migrated automatically and are not lost.
- New texture for the Ultimate Stellar Core.
- Requires Mango's Multiblock Library Revived **3.0.1** or newer.

## Fixed

- Miners now tell their neighbours when a Storage Upgrade changes their inventory, so pipes and
  ME Storage / Export Buses no longer keep talking to the old one.
- The required library version range was written as a CurseForge file id instead of a version.
