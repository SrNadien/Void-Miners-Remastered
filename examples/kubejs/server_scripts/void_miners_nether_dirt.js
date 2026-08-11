// Void Miners Remastered - ejemplo de KubeJS
//
// Copia este archivo a la carpeta kubejs/server_scripts/ de tu instancia.
// Tras editarlo, aplica los cambios en el juego con /reload (no hace falta reiniciar).
//
// Esquema de la receta:  miner(item, dimension, minTier)
//   item        -> lo que suelta el minero
//   dimension   -> id de la dimension donde aplica ("minecraft:the_nether")
//   minTier     -> tier minimo de minero que necesita la receta (1-9)
//
// Opcionales encadenables:
//   .weight(n)              peso relativo dentro de esa dimension+tier. Mas alto = sale mas
//                           veces. Como referencia, en el Nether el cuarzo va a 10 y el
//                           ancient debris a 0.1. Por defecto 1.0
//   .count(n)               cuantos items suelta por vez. Por defecto 1
//   .allowHigherTiers(bool) si los mineros de tier superior tambien pueden sacarlo.
//                           Por defecto true; ponlo en false para material exclusivo de un tier

ServerEvents.recipes(event => {

    // --- Lo minimo: tierra en el Nether desde el minero de tier 1 ---
    event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:the_nether', 1)

    // --- Lo mismo pero afinado: muy comun y en pilas de 4 ---
    // Peso 30 la vuelve mas frecuente que el cuarzo (10).
    event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:the_nether', 1)
        .weight(30.0)
        .count(4)

    // --- Exclusiva de un tier ---
    // Solo el minero de tier 3 saca esto; los de tier 4+ no lo heredan.
    event.recipes.voidminersremastered.miner('minecraft:soul_sand', 'minecraft:the_nether', 3)
        .weight(8.0)
        .allowHigherTiers(false)

    // --- Item de otro mod ---
    // Mismo formato, solo cambia el id.
    // event.recipes.voidminersremastered.miner('otromod:su_item', 'minecraft:the_nether', 5)
    //     .weight(2.0)

    // --- Otras dimensiones ---
    // event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:overworld', 1).weight(20.0)
    // event.recipes.voidminersremastered.miner('minecraft:dirt', 'minecraft:the_end', 8).weight(20.0)


    // --- Quitar recetas que ya trae el mod ---

    // Quitar una salida concreta en cualquier dimension:
    // event.remove({ type: 'voidminersremastered:miner', output: 'minecraft:ancient_debris' })

    // Quitar TODAS las recetas de minero (para rehacer la tabla de botines desde cero):
    // event.remove({ type: 'voidminersremastered:miner' })
})
