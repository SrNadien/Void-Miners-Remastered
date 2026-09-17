package nadiendev.voidminersremastered.common.compat.jei;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.BlockRequirement;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinerCategory implements IRecipeCategory<MinerRecipe> {
    public final Identifier UID;
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/jei_background.png");
    private static final Identifier UNKNOWN_DIMENSION_ICON = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/icon/unknown.png");

    public static final int WIDTH = 152;
    public static final int HEIGHT = 15;

    private static final int OUTPUT_X = 1;
    private static final int OUTPUT_Y = -1;
    private static final int BLOCK_UNDERNEATH_X = OUTPUT_X + 16 + 1;
    private static final int BLOCK_UNDERNEATH_Y = -1;
    private static final int TEXT_X = BLOCK_UNDERNEATH_X + 16 + 2;
    private static final int TEXT_Y = 4;
    private static final int DIMENSION_X = WIDTH - 16 - 1;
    private static final int DIMENSION_Y = -1;

    public RecipeType<MinerRecipe> RECIPE_TYPE;

    private final IDrawable background;
    private final IDrawable icon;
    public final Block blockIcon;
    public final int tier;

    private Map<ResourceKey<Level>, Double> totalWeightByDimension = Map.of();
    private Map<String, Double> totalWeightByBlockUnderneath = Map.of();
    private Map<ResourceKey<Level>, Integer> countByDimension = Map.of();
    private Map<String, Integer> countByBlockUnderneath = Map.of();

    public MinerCategory(IGuiHelper guiHelper, Block blockIcon, int tier) {
        UID = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner/tier" + tier + "_miner");
        RECIPE_TYPE = new RecipeType<>(UID, MinerRecipe.class);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, blockIcon.asItem().getDefaultInstance());
        this.blockIcon = blockIcon;
        this.tier = tier;
    }

    public void updateWeights(List<MinerRecipe> recipes) {
        Map<ResourceKey<Level>, Double> dimensionTotals = new HashMap<>();
        Map<String, Double> blockTotals = new HashMap<>();
        Map<ResourceKey<Level>, Integer> dimensionCounts = new HashMap<>();
        Map<String, Integer> blockCounts = new HashMap<>();

        for (MinerRecipe recipe : recipes) {
            BlockRequirement blockUnderneath = recipe.blockUnderneath();
            if (blockUnderneath != null) {
                String key = groupKey(blockUnderneath);
                blockTotals.merge(key, (double) recipe.output().weight, Double::sum);
                blockCounts.merge(key, 1, Integer::sum);
            } else {
                dimensionTotals.merge(recipe.dimension(), (double) recipe.output().weight, Double::sum);
                dimensionCounts.merge(recipe.dimension(), 1, Integer::sum);
            }
        }

        this.totalWeightByDimension = dimensionTotals;
        this.totalWeightByBlockUnderneath = blockTotals;
        this.countByDimension = dimensionCounts;
        this.countByBlockUnderneath = blockCounts;
    }

    private static String groupKey(BlockRequirement requirement) {
        return (requirement.isTag() ? "tag:" : "block:") + requirement.raw();
    }

    @Override
    public RecipeType<MinerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.voidminersremastered.miner", tier);
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MinerRecipe minerRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(minerRecipe.output().stack());

        BlockRequirement blockUnderneath = minerRecipe.blockUnderneath();
        if (blockUnderneath == null) {
            return;
        }

        List<ItemStack> blockStacks = blockUnderneath.resolveBlocks().stream()
                .filter(block -> block != Blocks.AIR)
                .map(block -> new ItemStack(block.asItem()))
                .filter(stack -> !stack.isEmpty())
                .toList();

        if (blockStacks.isEmpty()) {
            return;
        }

        IRecipeSlotBuilder blockSlot = builder.addSlot(RecipeIngredientRole.INPUT, BLOCK_UNDERNEATH_X, BLOCK_UNDERNEATH_Y);
        blockSlot.addItemStacks(blockStacks);
        blockSlot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
            tooltip.add(Component.translatable("gui.voidminersremastered.block_underneath").withStyle(ChatFormatting.WHITE));
            if (blockUnderneath.isTag()) {
                tooltip.add(Component.translatable("gui.voidminersremastered.accepts_any", blockUnderneath.raw()).withStyle(ChatFormatting.WHITE));
            }
        });
    }

    private double percentOf(MinerRecipe recipe) {
        BlockRequirement blockUnderneath = recipe.blockUnderneath();
        double total = blockUnderneath != null
                ? totalWeightByBlockUnderneath.getOrDefault(groupKey(blockUnderneath), 0.0)
                : totalWeightByDimension.getOrDefault(recipe.dimension(), 0.0);
        return total > 0.0 ? (recipe.output().weight / total) * 100.0 : 0.0;
    }

    private boolean isOnlyRecipeInGroup(MinerRecipe recipe) {
        BlockRequirement blockUnderneath = recipe.blockUnderneath();
        int count = blockUnderneath != null
                ? countByBlockUnderneath.getOrDefault(groupKey(blockUnderneath), 0)
                : countByDimension.getOrDefault(recipe.dimension(), 0);
        return count == 1;
    }

    @Override
    public void draw(MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);

        Font font = Minecraft.getInstance().font;
        boolean onlyRecipe = isOnlyRecipeInGroup(recipe);

        Component weightOrChance;
        if (Minecraft.getInstance().hasShiftDown()) {
            String percentText;
            double percent = percentOf(recipe);
            if (onlyRecipe) {
                percentText = "100";
            } else if (percent > 99.999) {
                percentText = ">99.999";
            } else if (percent < 0.0001) {
                percentText = "<0.0001";
            } else {
                percentText = String.format("%.4f", percent).replaceAll("0+$", "").replaceAll("[.,]$", "");
            }
            weightOrChance = Component.translatable("gui.voidminersremastered.chance", percentText);
        } else if (onlyRecipe) {
            weightOrChance = Component.translatable("gui.voidminersremastered.only_one_recipe");
        } else {
            weightOrChance = Component.translatable("gui.voidminersremastered.weight", customFormat(recipe.output().weight));
        }

        guiGraphics.text(font, weightOrChance, TEXT_X, TEXT_Y, 0xFFFFFFFF);

        Identifier texture = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/icon/" + getDimensionIcon(recipe.dimension()) + ".png");
        if (Minecraft.getInstance().getResourceManager().getResource(texture).isEmpty()) {
            texture = UNKNOWN_DIMENSION_ICON;
        }

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                DIMENSION_X,
                DIMENSION_Y,
                0.0F,
                0.0F,
                16,
                16,
                16,
                16
        );
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isHovering(mouseX, mouseY, DIMENSION_X, DIMENSION_Y, DIMENSION_X + 16, DIMENSION_Y + 16)) {
            tooltip.add(Component.translatable(recipe.dimension().identifier().toLanguageKey()));
            return;
        }

        if (Minecraft.getInstance().hasControlDown() && isHovering(mouseX, mouseY, 0, 0, WIDTH, HEIGHT)) {
            if (Minecraft.getInstance().hasShiftDown()) {
                tooltip.add(Component.translatable("gui.voidminersremastered.chance", BigDecimal.valueOf(percentOf(recipe)).toPlainString()));
            } else {
                tooltip.add(Component.translatable("gui.voidminersremastered.weight", BigDecimal.valueOf(recipe.output().weight).toPlainString()));
            }
        }
    }

    public static boolean isHovering(double mouseX, double mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1
                && mouseX <= x2
                && mouseY >= y1
                && mouseY <= y2;
    }

    public static String customFormat(float number) {
        if (number == 0.0) {
            return "0";
        }

        if (number < 0.000001 || number > 10000000) {
            return String.format("%.1E", number);
        }

        String formatted = String.format("%.6f", number);
        formatted = formatted.replaceAll("0+$", "");
        formatted = formatted.replaceAll("[.,]$", "");

        return formatted;
    }

    public static String getDimensionIcon(ResourceKey<Level> dimension) {
        return dimension.identifier().toString().replace(':', '.');
    }

    @Override
    public @Nullable Identifier getRegistryName(MinerRecipe recipe) {
        return recipe.getId();
    }
}
