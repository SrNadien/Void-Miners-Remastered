# KubeJS Integration - Void Miners Remastered

**For Minecraft 26.1.2 / NeoForge.** Requires KubeJS `26.1.2-8.0.4` or newer (which itself needs
NeoForge `26.1.2.84`+).

> **Namespace notice:** recipes live under **`voidminersremastered`**, not `voidminers`.
> Older versions of this page used `voidminers`, which silently matches nothing.
> The translation keys are the only thing that uses the short `voidminers` prefix.

Scripts go in `kubejs/server_scripts/`. After editing, apply them in-game with `/reload` — no restart needed.

---

## Recipe Syntax

```javascript
event.recipes.voidminersremastered.miner(item, dimension, minTier)
    .weight(value)
    .count(value)
    .allowHigherTiers(boolean);
```

---

## Basic Example

```javascript
ServerEvents.recipes(event => {
    event.recipes.voidminersremastered.miner('minecraft:sand', 'minecraft:overworld', 1)
        .weight(10.0)
        .count(2)
        .allowHigherTiers(true);
});
```

Mines sand in the Overworld, requires a tier 1 miner, weight 10.0, drops 2 items, and lets higher
tier miners use the recipe too.

---

## Parameters

### Required (positional)

| Parameter | Type | Description |
|-----------|------|-------------|
| `item` | String | The item to be mined (format: `'mod:item_id'`) |
| `dimension` | String | The dimension where this recipe works (e.g. `'minecraft:overworld'`) |
| `minTier` | Integer | Minimum miner tier required (1-9) |

### Optional (chainable)

| Method | Type | Default | Description |
|--------|------|---------|-------------|
| `.weight(value)` | Double | `1.0` | Relative weight — higher values make the item more common |
| `.count(value)` | Integer | `1` | Items dropped per mining operation |
| `.allowHigherTiers(boolean)` | Boolean | `true` | Whether higher tier miners can also use this recipe |

### How weight actually works

Weight is **relative to the other recipes in the same dimension**, not a percentage. For scale, these
are real values from the mod's own Nether table:

| Item | Weight |
|------|--------|
| `minecraft:nether_quartz_ore` | 10.0 |
| `minecraft:nether_gold_ore` | 5.0 |
| `minecraft:ancient_debris` | 0.1 |

So a weight of `1.0` (the default) is already fairly rare. Use 20-40 for filler blocks.

---

## Examples

### Minimum viable recipe

```javascript
event.recipes.voidminersremastered.miner('minecraft:coal', 'minecraft:overworld', 1);
```

### Dirt in the Nether, common, in stacks of 4

```javascript
event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:the_nether', 1)
    .weight(30.0)
    .count(4);
```

### Common resource (high weight)

```javascript
event.recipes.voidminersremastered.miner('minecraft:cobblestone', 'minecraft:overworld', 1)
    .weight(50.0)
    .count(16);
```

### Rare resource (low weight)

```javascript
event.recipes.voidminersremastered.miner('minecraft:diamond', 'minecraft:overworld', 2)
    .weight(2.0)
    .count(1);
```

### Tier-exclusive recipe

```javascript
event.recipes.voidminersremastered.miner('minecraft:netherite_ingot', 'minecraft:the_nether', 4)
    .weight(1.0)
    .count(1)
    .allowHigherTiers(false);
```

Only tier 4 miners get this — tiers 5+ do not inherit it.

### Modded items

```javascript
event.recipes.voidminersremastered.miner('create:zinc_ingot', 'minecraft:overworld', 2)
    .weight(8.0)
    .count(3);
```

### Custom dimensions

```javascript
event.recipes.voidminersremastered.miner('minecraft:glowstone_dust', 'twilightforest:twilight_forest', 2)
    .weight(15.0)
    .count(4);
```

A wrong dimension id is **not** an error — the recipe is simply created for a dimension that never
matches, so nothing drops. Double-check the id if a recipe seems to do nothing.

---

## Removing Recipes

### Remove every Void Miner recipe

```javascript
ServerEvents.recipes(event => {
    event.remove({ type: 'voidminersremastered:miner' });
});
```

### Remove a specific output

```javascript
ServerEvents.recipes(event => {
    event.remove({
        type: 'voidminersremastered:miner',
        item: 'minecraft:diamond'
    });
});
```

### Remove by dimension

```javascript
ServerEvents.recipes(event => {
    event.remove({
        type: 'voidminersremastered:miner',
        dimension: 'minecraft:the_nether'
    });
});
```

---

## Complete Example Script

```javascript
// kubejs/server_scripts/void_miners.js

ServerEvents.recipes(event => {

    // Wipe the built-in table and rebuild it from scratch
    event.remove({ type: 'voidminersremastered:miner' });

    // Overworld - Tier 1
    event.recipes.voidminersremastered.miner('minecraft:cobblestone', 'minecraft:overworld', 1)
        .weight(40.0).count(16);
    event.recipes.voidminersremastered.miner('minecraft:coal', 'minecraft:overworld', 1)
        .weight(20.0).count(4);
    event.recipes.voidminersremastered.miner('minecraft:iron_ore', 'minecraft:overworld', 1)
        .weight(15.0).count(2);

    // Overworld - Tier 2
    event.recipes.voidminersremastered.miner('minecraft:gold_ore', 'minecraft:overworld', 2)
        .weight(10.0).count(2);
    event.recipes.voidminersremastered.miner('minecraft:redstone', 'minecraft:overworld', 2)
        .weight(12.0).count(8);

    // Overworld - Tier 3
    event.recipes.voidminersremastered.miner('minecraft:diamond', 'minecraft:overworld', 3)
        .weight(3.0).count(1);
    event.recipes.voidminersremastered.miner('minecraft:emerald', 'minecraft:overworld', 3)
        .weight(2.0).count(1);

    // Nether - Tier 2
    event.recipes.voidminersremastered.miner('minecraft:netherrack', 'minecraft:the_nether', 2)
        .weight(35.0).count(12);
    event.recipes.voidminersremastered.miner('minecraft:quartz', 'minecraft:the_nether', 2)
        .weight(15.0).count(4);

    // Nether - Tier 3
    event.recipes.voidminersremastered.miner('minecraft:ancient_debris', 'minecraft:the_nether', 3)
        .weight(0.8).count(1);

    // End - Tier 3
    event.recipes.voidminersremastered.miner('minecraft:end_stone', 'minecraft:the_end', 3)
        .weight(30.0).count(16);
    event.recipes.voidminersremastered.miner('minecraft:shulker_shell', 'minecraft:the_end', 3)
        .weight(1.0).count(1);
});
```

---

## Tips & Best Practices

1. **Weight balancing**: weights are relative within the same dimension. Compare against the mod's own
   values (quartz 10, ancient debris 0.1) rather than thinking in percentages.
2. **Tier progression**: put more valuable resources behind higher tiers.
3. **Count amounts**: high counts for filler blocks, low counts for valuables.
4. **Dimension-specific resources**: keep netherrack in the Nether and end stone in the End.
5. **allowHigherTiers**: set to `false` only when you deliberately want to lock a drop to one tier.

---

## Common Dimensions

- `'minecraft:overworld'`
- `'minecraft:the_nether'`
- `'minecraft:the_end'`
- Modded dimensions, e.g. `'twilightforest:twilight_forest'`, `'the_bumblezone:the_bumblezone'`

---

## Add Custom Max Storage Upgrades

Location: `kubejs/startup_scripts`

### Turn an existing item into an upgrade

```javascript
const $ModDataComponents = Java.loadClass("nadiendev.voidminersremastered.init.ModDataComponents");

ItemEvents.modification(event => {
  event.modify("minecraft:gold_ingot", item => {
    item.set($ModDataComponents.MAX_STORAGE_UPGRADE_SLOTS.get(), 5); // slots added
  });
});
```

### Register a brand new upgrade item

> **Changed in 26.1.2:** `Item.Properties` must now carry its registry id. A bare
> `new Item$Properties()` throws `Item id not set` when the item is constructed, so `setId(...)`
> is required. Note also that `ResourceLocation` was renamed to `Identifier` in 26.1.

```javascript
const $MaxStorageUpgradeItem = Java.loadClass("nadiendev.voidminersremastered.world.item.MaxStorageUpgradeItem");
const $IProperties = Java.loadClass("net.minecraft.world.item.Item$Properties");
const $Identifier  = Java.loadClass("net.minecraft.resources.Identifier");
const $ResourceKey = Java.loadClass("net.minecraft.resources.ResourceKey");
const $Registries  = Java.loadClass("net.minecraft.core.registries.Registries");

StartupEvents.registry("item", event => {
  const id = $Identifier.fromNamespaceAndPath("kubejs", "custom_storage_upgrade");

  event.createCustom("kubejs:custom_storage_upgrade", () =>
    new $MaxStorageUpgradeItem(5, new $IProperties().setId($ResourceKey.create($Registries.ITEM, id)))
  );
});
```

---

## Troubleshooting

| Symptom | Likely cause |
|---------|--------------|
| Recipes never appear | Namespace typo — it is `voidminersremastered`, not `voidminers` |
| A recipe exists but never drops | Wrong dimension id, or weight far too low next to the others |
| `Item id not set` on startup | A custom item built without `setId(...)` — see above |
| Nothing changes after editing a script | Run `/reload`; check the KubeJS log for parse errors |
