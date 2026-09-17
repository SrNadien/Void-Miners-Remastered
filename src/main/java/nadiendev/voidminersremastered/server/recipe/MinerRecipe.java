package nadiendev.voidminersremastered.server.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class MinerRecipe implements Recipe<RecipeInput> {
    /**
     * The recipe type instance. It is registered (as {@code voidminersremastered:miner}) through
     * {@code ModRecipes.MINER_TYPE} -- since 26.1.2 recipe types are synced to the client through
     * {@code ByteBufCodecs.registry(Registries.RECIPE_TYPE)}, so an unregistered singleton would crash
     * while encoding.
     */
    public static final RecipeType<MinerRecipe> TYPE =
            RecipeType.simple(Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner"));

    private final WeightedStack output;
    private final int minTier;
    private final boolean allowHigherTiers;
    private final ResourceKey<Level> dimension;
    @Nullable
    private final BlockRequirement blockUnderneath;
    private Identifier id;

    public MinerRecipe(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension) {
        this(output, minTier, allowHigherTiers, dimension, null);
    }

    public MinerRecipe(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension, @Nullable BlockRequirement blockUnderneath) {
        this.output = output;
        this.minTier = minTier;
        this.allowHigherTiers = allowHigherTiers;
        this.dimension = dimension;
        this.blockUnderneath = blockUnderneath;
    }

    @Nullable
    public BlockRequirement blockUnderneath() {
        return blockUnderneath;
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    /**
     * Convenience overload: since 26.1.2 {@code RecipeHolder#id()} returns a
     * {@code ResourceKey<Recipe<?>>} instead of an {@code Identifier}.
     */
    public void setId(ResourceKey<Recipe<?>> id) {
        this.id = id.identifier();
    }

    @Nullable
    public Identifier getId() {
        return id;
    }

    public boolean allowHigherTiers() {
        return allowHigherTiers;
    }

    public WeightedStack output() {
        return output.copy();
    }

    public ResourceKey<Level> dimension() {
        return dimension;
    }

    public int minTier() {
        return minTier;
    }

    /**
     * Replacement for the removed {@code getResultItem(HolderLookup.Provider)}.
     */
    public ItemStack resultItem() {
        return output.stack();
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        return !pLevel.isClientSide();
    }

    @Override
    public ItemStack assemble(RecipeInput pInput) {
        return ItemStack.EMPTY;
    }

    /**
     * MUST stay {@code true}. {@code RecipeManager#finalizeRecipeLoading} silently drops every recipe
     * whose {@link #placementInfo()} is impossible to place (i.e. ingredient-less recipes such as this
     * one), only logging a warning. Returning {@code true} here short-circuits that check.
     */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<MinerRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<MinerRecipe> getType() {
        return TYPE;
    }

    /**
     * @deprecated kept for source compatibility with the 1.21.1 code; {@code RecipeType} can no longer be
     *             implemented as a dedicated class because it must be a registered instance. Prefer
     *             {@link MinerRecipe#TYPE} / {@code ModRecipes.MINER_TYPE}.
     */
    @Deprecated
    public static final class Type {
        public static final RecipeType<MinerRecipe> INSTANCE = TYPE;
        public static final String ID = "miner";

        private Type() {}
    }

    private static final String NO_BLOCK_UNDERNEATH = "none";

    private static final Codec<BlockRequirement> BLOCK_REQUIREMENT_CODEC =
            Codec.STRING.xmap(BlockRequirement::parse, BlockRequirement::raw);

    private static final MapCodec<MinerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    WeightedStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
                    Codec.INT.fieldOf("minTier").forGetter(recipe -> recipe.minTier),
                    Codec.BOOL.optionalFieldOf("allowHigherTiers", true).forGetter(recipe -> recipe.allowHigherTiers),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(recipe -> recipe.dimension),
                    BLOCK_REQUIREMENT_CODEC.optionalFieldOf("blockUnderneath")
                            .forGetter(recipe -> Optional.ofNullable(recipe.blockUnderneath))
            ).apply(instance, (output, minTier, allowHigherTiers, dimension, blockUnderneath) ->
                    new MinerRecipe(output, minTier, allowHigherTiers, dimension, blockUnderneath.orElse(null)))
    );

    private static final MapCodec<MinerRecipe> KUBEJS_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("item").forGetter(recipe -> recipe.output.stack()),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(recipe -> recipe.output.stack().getCount()),
                    Codec.DOUBLE.optionalFieldOf("weight", 1.0).forGetter(recipe -> (double) recipe.output.weight),
                    Codec.INT.fieldOf("minTier").forGetter(recipe -> recipe.minTier),
                    Codec.BOOL.optionalFieldOf("allowHigherTiers", true).forGetter(recipe -> recipe.allowHigherTiers),
                    ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension", Level.OVERWORLD).forGetter(recipe -> recipe.dimension),
                    Codec.STRING.optionalFieldOf("blockUnderneath", NO_BLOCK_UNDERNEATH)
                            .forGetter(recipe -> recipe.blockUnderneath == null ? NO_BLOCK_UNDERNEATH : recipe.blockUnderneath.raw())
            ).apply(instance, (item, count, weight, minTier, allowHigher, dim, blockUnderneathRaw) -> {
                ItemStack stack = item.copy();
                stack.setCount(count);
                BlockRequirement blockUnderneath = blockUnderneathRaw == null || blockUnderneathRaw.isEmpty() || blockUnderneathRaw.equals(NO_BLOCK_UNDERNEATH)
                        ? null
                        : BlockRequirement.parse(blockUnderneathRaw);
                return new MinerRecipe(new WeightedStack(stack, weight.floatValue()), minTier, allowHigher, dim, blockUnderneath);
            })
    );

    public static final MapCodec<MinerRecipe> COMBINED_CODEC = new MapCodec<MinerRecipe>() {
        @Override
        public <T> RecordBuilder<T> encode(MinerRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return CODEC.encode(input, ops, prefix);
        }

        @Override
        public <T> DataResult<MinerRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (input.get("output") != null) {
                return CODEC.decode(ops, input);
            } else if (input.get("item") != null) {
                return KUBEJS_CODEC.decode(ops, input);
            }
            return DataResult.error(() -> "Recipe must have either 'output' or 'item' field");
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.concat(CODEC.keys(ops), KUBEJS_CODEC.keys(ops));
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, MinerRecipe> STREAM_CODEC = StreamCodec.of(
            MinerRecipe::toNetwork,
            MinerRecipe::fromNetwork
    );

    /**
     * {@code RecipeSerializer} became a {@code record} in 26.1.2 -- it can no longer be implemented,
     * only instantiated with the (map codec, stream codec) pair.
     */
    public static final RecipeSerializer<MinerRecipe> SERIALIZER = new RecipeSerializer<>(COMBINED_CODEC, STREAM_CODEC);

    private static MinerRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        WeightedStack output = WeightedStack.STREAM_CODEC.decode(buffer);
        int minTier = buffer.readInt();
        boolean allowHigherTiers = buffer.readBoolean();
        ResourceKey<Level> dimension = buffer.readResourceKey(Registries.DIMENSION);
        BlockRequirement blockUnderneath = buffer.readBoolean() ? BlockRequirement.parse(buffer.readUtf()) : null;

        return new MinerRecipe(output, minTier, allowHigherTiers, dimension, blockUnderneath);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, MinerRecipe recipe) {
        WeightedStack.STREAM_CODEC.encode(buffer, recipe.output);
        buffer.writeInt(recipe.minTier);
        buffer.writeBoolean(recipe.allowHigherTiers);
        buffer.writeResourceKey(recipe.dimension);
        buffer.writeBoolean(recipe.blockUnderneath != null);
        if (recipe.blockUnderneath != null) {
            buffer.writeUtf(recipe.blockUnderneath.raw());
        }
    }

    public static class Builder implements RecipeBuilder {
        private final WeightedStack output;
        private final int minTier;
        private final boolean allowHigherTiers;
        private final Identifier id;
        private final ResourceKey<Level> dimension;
        @Nullable
        private BlockRequirement blockUnderneath;

        private Builder(WeightedStack output, int minTier, boolean allowHigherTiers, Identifier id, ResourceKey<Level> dimension) {
            this.output = output;
            this.minTier = minTier;
            this.id = id;
            this.allowHigherTiers = allowHigherTiers;
            this.dimension = dimension;
        }

        public static Builder builder(WeightedStack output, int minTier, ResourceKey<Level> dimension) {
            return Builder.builder(output, minTier, true, dimension);
        }

        public static Builder builder(WeightedStack output, int minTier, boolean allowHigherTiers, ResourceKey<Level> dimension) {
            // Derived from the item holder, NOT from an ItemStack: building one here would force
            // Holder.Reference#components(), which is still unbound during datagen.
            Identifier itemId = java.util.Objects.requireNonNull(output.template(), "Miner recipe output must not be empty")
                    .item()
                    .unwrapKey()
                    .orElseThrow(() -> new IllegalStateException("Miner recipe output item is not registered"))
                    .identifier();
            Identifier recipeId = Identifier.fromNamespaceAndPath(
                    VoidMinersRemastered.MODID,
                    dimension.identifier().getPath() + "/tier" + minTier + "_miner/" + recipePath(itemId)
            );
            return new Builder(output, minTier, allowHigherTiers, recipeId, dimension);
        }

        private static String recipePath(Identifier itemId) {
            String namespace = itemId.getNamespace();
            if (namespace.equals("minecraft") || namespace.equals(VoidMinersRemastered.MODID)) {
                return itemId.getPath();
            }
            return namespace + "/" + itemId.getPath();
        }

        public Builder blockUnderneath(String blockIdOrTag) {
            this.blockUnderneath = BlockRequirement.parse(blockIdOrTag);
            return this;
        }

        @Override
        public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String pGroupName) {
            return this;
        }

        /**
         * Replaces the removed {@code getResult()}; {@code RecipeBuilder#save(RecipeOutput)} now defers
         * to this for the recipe id.
         */
        @Override
        public ResourceKey<Recipe<?>> defaultId() {
            return ResourceKey.create(Registries.RECIPE, this.id);
        }

        @Override
        public void save(RecipeOutput pRecipeOutput, ResourceKey<Recipe<?>> pId) {
            MinerRecipe recipe = new MinerRecipe(this.output, this.minTier, this.allowHigherTiers, this.dimension, this.blockUnderneath);
            pRecipeOutput.accept(pId, recipe, null);
        }
    }
}
