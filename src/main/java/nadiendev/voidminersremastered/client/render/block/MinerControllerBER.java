package nadiendev.voidminersremastered.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.MinerControllerBE;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 26.1.2 rewrite of the miner controller renderer.
 *
 * <p>The renderer is split in the two phases the new {@link BlockEntityRenderer} contract mandates:
 * {@link #extractRenderState} reads everything it needs off the block entity into a
 * {@link MinerControllerRenderState}, and {@link #submit} only enqueues draw nodes.
 *
 * <p>{@code BlockRenderDispatcher} no longer exists, so the multiblock preview is built out of
 * {@link BlockModelRenderState}s resolved through the {@link BlockModelResolver} handed to us by the
 * renderer context. Resolving 125+ models per frame would be far more expensive than the old
 * {@code renderSingleBlock} path, so the resolved preview is cached on this renderer instance,
 * keyed by the structure id, and additionally de-duplicated per {@link BlockState}
 * (a 5x5x5 structure normally uses a handful of distinct states). The cache is shared by every
 * miner controller in the world and is implicitly dropped when the renderers are recreated on a
 * resource reload, which is exactly when the baked models become stale.
 */
public class MinerControllerBER implements BlockEntityRenderer<MinerControllerBE, MinerControllerBER.MinerControllerRenderState> {

    /** Display context used when resolving the preview models; equivalent to the vanilla entity-held-block one. */
    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private static final float BEAM_LENGTH = 320f;
    private static final float BEAM_WIDTH = 0.3f;
    private static final float BLOCK_SCALE = 0.5f;

    private final BlockModelResolver blockModelResolver;

    /** Structure id the {@link #cachedPreview} was built for, or null when nothing is cached yet. */
    private String cachedStructure;
    private List<PreviewBlock> cachedPreview = List.of();
    private int cachedOffset;

    public MinerControllerBER(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public MinerControllerRenderState createRenderState() {
        return new MinerControllerRenderState();
    }

    @Override
    public void extractRenderState(MinerControllerBE blockEntity,
                                   MinerControllerRenderState state,
                                   float partialTicks,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.renderBeam = false;
        state.preview = List.of();

        if (blockEntity.canSeeBedrockOrVoid && blockEntity.foundStructure) {
            long gameTime = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0L;
            state.renderBeam = true;
            state.animationTime = (float) Math.floorMod(gameTime, 40) + partialTicks;
            state.beamColor = blockEntity.getBeamColor();
            return;
        }

        if (!blockEntity.showStructure) return;

        Identifier structureId = blockEntity.getStructure();
        if (structureId == null) return;

        String structure = structureId.toString();
        if (!MiscUtil.structureMap.containsKey(structure)) return;

        Preview preview = resolvePreview(structure);
        state.preview = preview.blocks();
        state.previewOffset = preview.offset();
    }

    @Override
    public void submit(MinerControllerRenderState state,
                       PoseStack pose,
                       SubmitNodeCollector collector,
                       CameraRenderState camera) {
        if (state.renderBeam) {
            pose.pushPose();
            pose.translate(0.5f, 0, 0.5f);
            pose.mulPose(Axis.YP.rotationDegrees(state.animationTime * 2.25f - 45f));

            submitBeam(pose, collector, new Vector3f(0f, 0f, 0f), state.beamColor, BEAM_LENGTH, BEAM_WIDTH);

            pose.popPose();
            return;
        }

        if (state.preview.isEmpty()) return;

        int offset = state.previewOffset;

        pose.pushPose();
        pose.translate(-offset, 1, -offset);
        pose.pushPose();
        pose.mulPose(Axis.ZN.rotationDegrees(90));

        for (PreviewBlock block : state.preview) {
            pose.pushPose();
            pose.translate(block.x(), block.y(), block.z());
            pose.translate(0.5f, 0.5f, 0.5f);
            pose.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
            pose.translate(-0.5f, -0.5f, -0.5f);

            // submitMultiLayer keeps NeoForge's per-quad render types, so modded blocks in the
            // preview keep the layer their model asks for (the old code forced translucent).
            block.model().submitMultiLayer(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

            pose.popPose();
        }

        pose.popPose();
        pose.popPose();
    }

    // ------------------------------------------------------------------ preview cache

    /**
     * Resolves (and caches) the baked models of a multiblock pattern.
     * Called from the extraction phase only.
     */
    private synchronized Preview resolvePreview(String structure) {
        if (structure.equals(cachedStructure)) {
            return new Preview(cachedPreview, cachedOffset);
        }

        List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structure);
        if (blocks == null || blocks.isEmpty()) {
            cachedStructure = structure;
            cachedPreview = List.of();
            cachedOffset = 0;
            return new Preview(cachedPreview, cachedOffset);
        }

        Map<BlockState, BlockModelRenderState> resolved = new HashMap<>();
        List<PreviewBlock> preview = new ArrayList<>();

        for (int x = 0; x < blocks.size(); x++) {
            List<List<BlockState>> b2 = blocks.get(x);

            for (int y = 0; y < b2.size(); y++) {
                List<BlockState> b3 = b2.get(y);

                for (int z = 0; z < b3.size(); z++) {
                    BlockState block = b3.get(z);
                    if (block == null) continue;

                    BlockModelRenderState model = resolved.computeIfAbsent(block, state -> {
                        BlockModelRenderState renderState = new BlockModelRenderState();
                        blockModelResolver.update(renderState, state, BLOCK_DISPLAY_CONTEXT);
                        return renderState;
                    });

                    if (model.isEmpty()) continue;

                    preview.add(new PreviewBlock(x, y, z, model));
                }
            }
        }

        cachedStructure = structure;
        cachedPreview = List.copyOf(preview);
        cachedOffset = blocks.getFirst().size() / 2;
        return new Preview(cachedPreview, cachedOffset);
    }

    // ------------------------------------------------------------------ beam geometry

    private static void submitBeam(PoseStack pose, SubmitNodeCollector collector, Vector3f center, int color, float length, float width) {
        for (int i = 0; i < 4; i++) {
            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(90 * i));
            pose.pushPose();
            pose.translate(-width / 2, 0, -width / 2);

            // RenderType.gui() is gone (and was a GUI pipeline abused in world space).
            // debugQuads() is the world-space equivalent: POSITION_COLOR / QUADS / translucent blending.
            collector.submitCustomGeometry(pose, RenderTypes.debugQuads(),
                (p, buffer) -> renderQuad(buffer, p, center, color, length, width));

            pose.popPose();
            pose.popPose();
        }
    }

    private static void renderQuad(VertexConsumer vC, PoseStack.Pose pose, Vector3f pos, int color, float length, float width) {

        vC.addVertex(pose, pos.x, pos.y, pos.z)
            .setColor(color)
            .setUv(0, 0)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightCoordsUtil.FULL_BRIGHT)
            .setNormal(0, 0, 0);

        vC.addVertex(pose, pos.x + width, pos.y, pos.z)
            .setColor(color)
            .setUv(1, 0)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightCoordsUtil.FULL_BRIGHT)
            .setNormal(0, 0, 0);

        vC.addVertex(pose, pos.x + width, pos.y - length, pos.z)
            .setColor(color)
            .setUv(1, 1)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightCoordsUtil.FULL_BRIGHT)
            .setNormal(0, 0, 0);

        vC.addVertex(pose, pos.x, pos.y - length, pos.z)
            .setColor(color)
            .setUv(0, 1)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightCoordsUtil.FULL_BRIGHT)
            .setNormal(0, 0, 0);
    }

    // ------------------------------------------------------------------ contract

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(MinerControllerBE pBlockEntity, Vec3 pCameraPos) {
        return pBlockEntity.getBlockPos().getCenter().distanceTo(pCameraPos) <= 100;
    }

    /**
     * The beam is 320 blocks long and the preview extends around the controller, so the unit-cube
     * default would let the frustum cull both. This mirrors the old {@code shouldRenderOffScreen}
     * behaviour, which was the only culling opt-out available in 1.21.1.
     */
    @Override
    public AABB getRenderBoundingBox(MinerControllerBE blockEntity) {
        return AABB.INFINITE;
    }

    // ------------------------------------------------------------------ state types

    /** One preview block: its slot in the pattern plus the (shared, cached) resolved model. */
    private record PreviewBlock(int x, int y, int z, BlockModelRenderState model) {}

    private record Preview(List<PreviewBlock> blocks, int offset) {}

    public static class MinerControllerRenderState extends BlockEntityRenderState {
        public boolean renderBeam;
        public float animationTime;
        public int beamColor;
        public List<PreviewBlock> preview = List.of();
        public int previewOffset;
    }
}
