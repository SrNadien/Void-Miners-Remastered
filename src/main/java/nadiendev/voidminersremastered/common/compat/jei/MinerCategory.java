package nadiendev.voidminersremastered.common.compat.jei;

import net.minecraft.ChatFormatting;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.BlockRequirement;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
    public final ResourceLocation UID;
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/jei_background.png");

    public RecipeType<MinerRecipe> RECIPE_TYPE;

    private final IDrawable background;
    private final IDrawable icon;
    public final int tier;

    private final int recipeWidth = 152;
    private final int recipeHeight = 15;

    private final int recipeOutputX = 1;
    private final int recipeOutputY = -1;

    private final int blockUnderneathX = recipeOutputX + 16 + 1;
    private final int blockUnderneathY = -1;

    private final int textX = blockUnderneathX + 16 + 2;
    private final int textY = 4;

    private final int dimensionImageX = recipeWidth - 16 - 1;
    private final int dimensionImageY = -1;

    private Map<ResourceKey<Level>, Double> totalWeightByDimension = Map.of();
    private Map<String, Double> totalWeightByBlockUnderneath = Map.of();
    private Map<ResourceKey<Level>, Integer> countByDimension = Map.of();
    private Map<String, Integer> countByBlockUnderneath = Map.of();

    public void updateWeights(List<MinerRecipe> recipes) {
        Map<ResourceKey<Level>, Double> dimensionTotals = new HashMap<>();
        Map<String, Double> blockTotals = new HashMap<>();
        Map<ResourceKey<Level>, Integer> dimensionCounts = new HashMap<>();
        Map<String, Integer> blockCounts = new HashMap<>();

        for (MinerRecipe recipe : recipes) {
            BlockRequirement blockUnderneath = recipe.blockUnderneath();
            if (blockUnderneath != null) {
                String key = (blockUnderneath.isTag() ? "tag:" : "block:") + blockUnderneath.raw();
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

    public MinerCategory(IGuiHelper guiHelper, Block blockIcon, int tier) {
        UID = ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner/tier" + tier + "_miner");
        RECIPE_TYPE = new RecipeType<>(UID, MinerRecipe.class);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, recipeWidth, recipeHeight);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, blockIcon.asItem().getDefaultInstance());
        this.tier = tier;
    }

    @Override
    public RecipeType<MinerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.voidminers.miner", tier);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MinerRecipe minerRecipe, IFocusGroup iFocusGroup) {
        builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                recipeOutputX,
                recipeOutputY
        ).addItemStack(minerRecipe.output().stack);

        BlockRequirement blockUnderneath = minerRecipe.blockUnderneath();
        if (blockUnderneath != null) {
            List<ItemStack> blockStacks = blockUnderneath.resolveBlocks().stream()
                    .filter(block -> block != Blocks.AIR)
                    .map(block -> new ItemStack(block.asItem()))
                    .filter(stack -> !stack.isEmpty())
                    .toList();

            if (!blockStacks.isEmpty()) {
                IRecipeSlotBuilder blockSlot = builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        blockUnderneathX,
                        blockUnderneathY
                );
                blockSlot.addItemStacks(blockStacks);

                blockSlot.addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.translatable("gui.voidminers.block_underneath").withStyle(ChatFormatting.WHITE));
                    if (blockUnderneath.isTag()) {
                        tooltip.add(Component.translatable("gui.voidminers.accepts_any",blockUnderneath.raw()).withStyle(ChatFormatting.WHITE));
                    }
                });
            }
        }
    }

    @Override
    public void draw(MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        Component weightOrChance;
        double percent;
        boolean onlyRecipeInGroup;

        BlockRequirement blockUnderneath = recipe.blockUnderneath();
        if (blockUnderneath != null) {
            String key = (blockUnderneath.isTag() ? "tag:" : "block:") + blockUnderneath.raw();
            double total = totalWeightByBlockUnderneath.getOrDefault(key, 0.0);
            percent = total > 0.0 ? ((double) recipe.output().weight / total) * 100.0 : 0.0;
            onlyRecipeInGroup = countByBlockUnderneath.getOrDefault(key, 0) == 1;
        } else {
            double total = totalWeightByDimension.getOrDefault(recipe.dimension(), 0.0);
            percent = total > 0.0 ? ((double) recipe.output().weight / total) * 100.0 : 0.0;
            onlyRecipeInGroup = countByDimension.getOrDefault(recipe.dimension(), 0) == 1;
        }

        if (Screen.hasShiftDown()) {
            String percentText;
            if (onlyRecipeInGroup) {
                percentText = "100";
            } else if (percent > 99.999) {
                percentText = ">99.999";
            } else if (percent < 0.0001) {
                percentText = "<0.0001";
            } else {
                String formatted = String.format("%.4f", percent);
                formatted = formatted.replaceAll("0+$", "");
                formatted = formatted.replaceAll("[.,]$", "");
                percentText = formatted;
            }

            weightOrChance = Component.translatable("gui.voidminers.chance", percentText);
        } else {
            if (onlyRecipeInGroup) {
                weightOrChance = Component.translatable("gui.voidminers.only_one_recipe");
            } else {
                weightOrChance = Component.translatable("gui.voidminers.weight", customFormat(recipe.output().weight));
            }
        }

        guiGraphics.drawString(font, weightOrChance, textX, textY, 0xFFFFFFFF);

        String dimensionName = recipe.dimension().location().toLanguageKey();

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/icon/" + getDimensionIcon(recipe.dimension()) + ".png");
        if (Minecraft.getInstance().getResourceManager().getResource(texture).isEmpty()) {
            texture = ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/icon/unknown.png");
        }

        guiGraphics.blit(
                texture,
                dimensionImageX,
                dimensionImageY,
                0,
                0,
                16,
                16,
                16,
                16
        );

        if (isHovering(mouseX, mouseY, 0, 0, recipeWidth, recipeHeight)) {
            if ( Screen.hasControlDown()) {
                if (Screen.hasShiftDown()) {
                    guiGraphics.renderTooltip(font, Component.translatable("gui.voidminers.chance",BigDecimal.valueOf(percent).toPlainString()), (int) mouseX, (int) mouseY + 10);
                } else {
                    guiGraphics.renderTooltip(font, Component.translatable("gui.voidminers.weight", BigDecimal.valueOf(recipe.output().weight).toPlainString()), (int) mouseX, (int) mouseY + 10);
                }
            }
        }

        if (isHovering(mouseX, mouseY, dimensionImageX, dimensionImageY, dimensionImageX + 16, dimensionImageY + 16)) {
            guiGraphics.renderTooltip(font, Component.translatable(dimensionName), (int) mouseX, (int) mouseY + 10);
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

        String formatted;

        if (number < 0.000001 || number > 10000000) {
            // Use scientific notation for very small and very big numbers
            return String.format("%.1E", number);
        } else {
            formatted = String.format("%.6f", number);
        }

        // Remove trailing zeros and unnecessary decimal point/comma
        formatted = formatted.replaceAll("0+$", "");
        formatted = formatted.replaceAll("[.,]$", "");

        return formatted;
    }

    public static String getDimensionIcon(ResourceKey<Level> dimension) {
        return dimension.location().toString().replace(':', '.');
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(MinerRecipe recipe) {
        return recipe.getId();
    }
}