package nadiendev.voidminersremastered.common.compat.jei;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.server.recipe.MinerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class MinerCategory implements IRecipeCategory<MinerRecipe> {
    public final Identifier UID;
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/jei_background.png");

    /** Size of {@link #TEXTURE}; used to be carried by the (now removed) {@code getBackground()} drawable. */
    public static final int WIDTH = 125;
    public static final int HEIGHT = 15;

    public RecipeType<MinerRecipe> RECIPE_TYPE;

    private final IDrawable background;
    private final IDrawable icon;
    public final Block blockIcon;
    public final int tier;

    public MinerCategory(IGuiHelper guiHelper, Block blockIcon, int tier) {
        UID = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "miner/tier" + tier + "_miner");
        RECIPE_TYPE = new RecipeType<>(UID, MinerRecipe.class);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, blockIcon.asItem().getDefaultInstance());
        this.blockIcon = blockIcon;
        this.tier = tier;
    }

    @Override
    public RecipeType<MinerRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.voidminersremastered.miner", tier);
    }

    /**
     * {@code IRecipeCategory#getBackground()} no longer exists in the 26.1.2 JEI API; the category now
     * declares its size and paints whatever background it wants inside {@link #draw}.
     */
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
        builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                4,
                -1
        ).addItemStack(minerRecipe.output().stack());
    }

    @Override
    public void draw(MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);

        Component weight = Component.translatable("tooltip.voidminersremastered.structure.weight", customFormat(recipe.output().weight));

        Identifier texture = Identifier.fromNamespaceAndPath(VoidMinersRemastered.MODID, "textures/gui/icon/" + getDimensionIcon(recipe.dimension()) + ".png");

        Font font = Minecraft.getInstance().font;

        // GuiGraphics is gone; JEI hands out net.minecraft.client.gui.GuiGraphicsExtractor.
        // drawString(..) -> text(..), and blit(..) now takes the RenderPipeline up front.
        guiGraphics.text(font, weight, 24, 4, 0xFFFFFFFF);
        guiGraphics.blit(
            RenderPipelines.GUI_TEXTURED,
            texture,
            105,
            -1,
            0.0F,
            0.0F,
            16,
            16,
            16,
            16
        );
    }

    /**
     * The dimension name used to be drawn with {@code GuiGraphics#renderTooltip} from inside {@code draw};
     * tooltips are now collected by JEI through this hook instead, which keeps the same visible behaviour
     * (hovering the dimension icon shows the dimension name).
     */
    @Override
    public void getTooltip(ITooltipBuilder tooltip, MinerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!isHovering(mouseX, mouseY, 105, 0, 121, 16)) {
            return;
        }
        tooltip.add(Component.translatable(recipe.dimension().identifier().toLanguageKey()));
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

        if (Math.abs(number) < 0.000001) {
            // Use scientific notation for very small numbers
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
        return dimension.identifier().toString().replace(':', '.');
    }

    @Override
    public @Nullable Identifier getRegistryName(MinerRecipe recipe) {
        return recipe.getId();
    }
}
