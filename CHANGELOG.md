# Void Miners Remastered 3.0.1 — Minecraft 26.1.2

## Added

- **Ores from other mods in the miners**, loaded only when the mod is installed (`neoforge:mod_loaded`):
  AllTheModium, AllTheOres, Oritech, Bigger Reactors Revived, Energized Power, Modern Foundry,
  Mystical Agriculture / Agradditions, Occultism, Powah, UltimateFoods and XyCraft World.
  Each ore is available in the dimension where it generates.
- **Deepslate ores** (vanilla and modded) in the Overworld.
- **AllTheModium Mining dimension support**: every ore and every Void Miners gem can be mined there.
  AllTheModium's The Other gets its own ores too.
- **Gem output per miner tier**: gems now drop 8 / 9 / 12 / 15 / 19 / 34 / 40 / 55 / 64 per operation
  depending on the miner tier, and JEI shows the amount.
- **Configurable output side**: sneak + right-click a face of a formed Miner Controller to push the
  mined items into the inventory on that side. Same on the Solar Controller to choose which side
  outputs energy (all sides by default). Interval configurable with `EXPORT_INTERVAL_TICKS`.
- **Fusion connected textures** (optional): frames and modifiers connect across the multiblock
  when Fusion is installed.
- JEI: hold SHIFT for percentages, CTRL for the full weight/chance value; recipe ID shown.
- `c:gems/*` and `c:storage_blocks/*` tags.

---

# Void Miners Remastered 3.0.0 — Minecraft 26.1.2

Full port to Minecraft **26.1.2** / NeoForge. This is a large internal rewrite: Mojang renamed and
removed a lot of API between 1.21.1 and 26.1, and several subsystems (rendering, data generation,
block entity storage) had to be rebuilt rather than adapted.

## Requirements

| | |
|---|---|
| Minecraft | 26.1.2 |
| NeoForge | **26.1.2.84 or newer** |
| Java | 25 |
| Mango's Multiblock Library | **4.0.0** (required) |

Optional integrations: JEI `29.16.0.47`+, Jade `26.1.8`+, KubeJS `26.1.2-8.0.4`+.

> Older NeoForge builds (26.1.2.83 and below) will not work — KubeJS requires `.84`+.

---

## Fixed

- **Translations were broken for every language except English.** All 124 block and item names were
  registered under the wrong key prefix, so Spanish, Japanese, Russian and Chinese players saw the
  entire mod in English despite the translations existing and being complete. Every locale now shows
  correctly. *(This bug was present in 1.21.1 as well.)*
- **JEI category showed raw translation keys** instead of "Tier N Miner" and the weight line.
- **Glass Panel lost its transparency** and rendered on the wrong layer.
- Translation keys are now consistently namespaced under `voidminersremastered`.

## Changed

- **Miner and solar panel renderers rebuilt** for the new two-phase render pipeline. The multiblock
  preview now caches its resolved models per pattern and per block state instead of re-resolving
  125+ models every frame.
- **Light beam** is now drawn with custom geometry. Side effect: it is visible from every angle,
  where previously two of its four faces were culled.
- Block entity storage migrated to the new save format. **Existing miners keep their energy,
  inventory, progress and upgrades** — the NBT keys were deliberately left unchanged.
- Energy and item handling moved to NeoForge's new transfer API.
- Recipe type is now properly registered, and miner recipes are flagged so the game does not discard
  them while loading.
- Data generation rebuilt on the new model/recipe providers. Generated files are unchanged.
- Removed unused runtime dependencies: Athena, Pipez, Mekanism, CTM and Immersive Engineering.

## KubeJS

The recipe namespace is **`voidminersremastered`**, not `voidminers`:

```javascript
ServerEvents.recipes(event => {
    event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:the_nether', 1)
        .weight(30.0)
        .count(4);
});
```

The recipe JSON format is unchanged, so existing datapacks keep working. Older documentation used
the `voidminers` namespace, which silently matches nothing — the wiki has been corrected, and a
ready-to-use example script ships in `examples/kubejs/server_scripts/`.

Custom upgrade items need one extra step in 26.1.2: `Item.Properties` must carry its registry id via
`setId(...)`, or the item fails to construct. See the wiki for the updated snippet.

---

## Notes for pack and resource pack authors

- **Resource packs that translate this mod must be updated.** Translation keys moved to the
  `voidminersremastered` prefix. Keys for block and item names never worked under the old prefix
  anyway, so most packs are only affected in the tooltip and GUI strings.
- Datapacks and KubeJS scripts are **not** affected — the recipe format did not change.
