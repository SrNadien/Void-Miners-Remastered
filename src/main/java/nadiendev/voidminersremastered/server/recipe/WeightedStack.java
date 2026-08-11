package nadiendev.voidminersremastered.server.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import javax.annotation.Nullable;

/**
 * Weighted miner output.
 *
 * <p>Since 26.1.2 an {@link ItemStack} can no longer be built eagerly outside of a loaded world:
 * {@code new ItemStack(Holder<Item>, int)} calls {@code new PatchedDataComponentMap(item.components())}
 * and {@code Holder.Reference#components()} throws {@code "Components not bound yet"} until
 * {@code DataComponentInitializers.PendingComponents#apply()} runs (only from
 * {@code ReloadableServerResources}, i.e. on datapack load — never during datagen).
 *
 * <p>The recipe therefore stores vanilla's {@link ItemStackTemplate} (the exact type vanilla's own
 * {@code ShapedRecipeBuilder}/{@code ShapelessRecipeBuilder} use as the recipe result during datagen)
 * and materialises the {@link ItemStack} lazily through {@link #stack()}.
 */
public class WeightedStack {
    /** {@code null} means "empty output". */
    @Nullable
    private final ItemStackTemplate template;

    public float weight;

    /** Lazily materialised from {@link #template}; never touched during datagen. */
    @Nullable
    private ItemStack resolved;

    public WeightedStack(@Nullable ItemStackTemplate template, float weight) {
        this.template = template;
        this.weight = weight;
    }

    public WeightedStack(ItemStack stack, float weight) {
        this(stack.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(stack), weight);
        if (!stack.isEmpty()) {
            this.resolved = stack;
        }
    }

    public WeightedStack(Item item, float weight) {
        this(new ItemStackTemplate(item), weight);
    }

    @Nullable
    public ItemStackTemplate template() {
        return template;
    }

    /**
     * Materialises the output stack. Only safe once item data components have been bound
     * (i.e. anywhere inside a running game); <b>never call this from datagen</b>.
     */
    public ItemStack stack() {
        if (template == null) {
            return ItemStack.EMPTY;
        }
        if (resolved == null) {
            resolved = template.create();
        }
        return resolved;
    }

    public boolean isEmpty() {
        return template == null;
    }

    public WeightedStack copy() {
        // ItemStackTemplate is an immutable record, so sharing it is safe; the resolved
        // ItemStack is deliberately not carried over so each copy owns its own stack.
        return new WeightedStack(template, weight);
    }

    /**
     * Mirror of {@code ItemStack.MAP_CODEC} ({@code <MC>\net\minecraft\world\item\ItemStack.java:111-121})
     * with the exact same field names, ordering and optionality — so the generated JSON is byte-identical —
     * but built on {@code Item.CODEC} instead of {@code Item.CODEC_WITH_BOUND_COMPONENTS}, which rejects
     * items whose components are not bound yet ({@code Item.java:103-107}).
     */
    public static final Codec<ItemStackTemplate> STACK_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Item.CODEC.fieldOf("id").forGetter(ItemStackTemplate::item),
                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(ItemStackTemplate::count),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(ItemStackTemplate::components)
            ).apply(instance, ItemStackTemplate::new)
    );

    public static final Codec<WeightedStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    STACK_CODEC.fieldOf("stack").forGetter(WeightedStack::template),
                    Codec.FLOAT.fieldOf("weight").forGetter(ws -> ws.weight)
            ).apply(instance, WeightedStack::new)
    );

    /**
     * {@link ItemStackTemplate#STREAM_CODEC} only writes the item holder id, the count and the component
     * patch, so — unlike {@code ItemStack.STREAM_CODEC} — decoding it never needs bound components either.
     * An empty output cannot be represented ({@link ItemStackTemplate} refuses air/count 0) and never
     * occurs in practice, since every {@code MinerRecipe} carries a real result.
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WeightedStack> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC,
            ws -> {
                if (ws.template == null) {
                    throw new IllegalStateException("Cannot sync a WeightedStack with an empty output");
                }
                return ws.template;
            },
            ByteBufCodecs.FLOAT,
            ws -> ws.weight,
            WeightedStack::new
    );
}
