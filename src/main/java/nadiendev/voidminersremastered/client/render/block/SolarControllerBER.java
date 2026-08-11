package nadiendev.voidminersremastered.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
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
 * 26.1.2 rewrite of the solar controller renderer. See {@link MinerControllerBER} for the details of
 * the extract/submit split, the {@code BlockRenderDispatcher} replacement and the preview cache;
 * the only differences here are the upwards beam and the extra Y offset of the preview.
 */
public class SolarControllerBER implements BlockEntityRenderer<SolarControllerBE, SolarControllerBER.SolarControllerRenderState> {

    private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    private static final float BEAM_LENGTH = 320f;
    private static final float BEAM_WIDTH = 0.3f;
    private static final float BLOCK_SCALE = 0.5f;

    private final BlockModelResolver blockModelResolver;

    private String cachedStructure;
    private List<PreviewBlock> cachedPreview = List.of();
    private int cachedXOffset;
    private int cachedYOffset;

    public SolarControllerBER(BlockEntityRendererProvider.Context context) {
        this.blockModelResolver = context.blockModelResolver();
    }

    @Override
    public SolarControllerRenderState createRenderState() {
        return new SolarControllerRenderState();
    }

    @Override
    public void extractRenderState(SolarControllerBE blockEntity,
                                   SolarControllerRenderState state,
                                   float partialTicks,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.renderBeam = false;
        state.preview = List.of();

        if (blockEntity.foundStructure && blockEntity.canSeeSky) {
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
        state.previewXOffset = preview.xOffset();
        state.previewYOffset = preview.yOffset();
    }

    @Override
    public void submit(SolarControllerRenderState state,
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

        pose.pushPose();
        pose.translate(-state.previewXOffset, state.previewYOffset, -state.previewXOffset);
        pose.pushPose();
        pose.mulPose(Axis.ZN.rotationDegrees(90));

        for (PreviewBlock block : state.preview) {
            pose.pushPose();
            pose.translate(block.x(), block.y(), block.z());
            pose.translate(0.5f, 0.5f, 0.5f);
            pose.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
            pose.translate(-0.5f, -0.5f, -0.5f);

            block.model().submitMultiLayer(pose, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

            pose.popPose();
        }

        pose.popPose();
        pose.popPose();
    }

    // ------------------------------------------------------------------ preview cache

    private synchronized Preview resolvePreview(String structure) {
        if (structure.equals(cachedStructure)) {
            return new Preview(cachedPreview, cachedXOffset, cachedYOffset);
        }

        List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structure);
        if (blocks == null || blocks.isEmpty()) {
            cachedStructure = structure;
            cachedPreview = List.of();
            cachedXOffset = 0;
            cachedYOffset = 0;
            return new Preview(cachedPreview, cachedXOffset, cachedYOffset);
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

                    BlockModelRenderState model = resolved.computeIfAbsent(block, blockState -> {
                        BlockModelRenderState renderState = new BlockModelRenderState();
                        blockModelResolver.update(renderState, blockState, BLOCK_DISPLAY_CONTEXT);
                        return renderState;
                    });

                    if (model.isEmpty()) continue;

                    preview.add(new PreviewBlock(x, y, z, model));
                }
            }
        }

        cachedStructure = structure;
        cachedPreview = List.copyOf(preview);
        cachedXOffset = blocks.getFirst().size() / 2;
        // yOffset otherwise it renders the multiblock structure under the controller as if it was a miner
        cachedYOffset = blocks.size();
        return new Preview(cachedPreview, cachedXOffset, cachedYOffset);
    }

    // ------------------------------------------------------------------ beam geometry

    private static void submitBeam(PoseStack pose, SubmitNodeCollector collector, Vector3f center, int color, float length, float width) {
        for (int i = 0; i < 4; i++) {
            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(90 * i));
            pose.pushPose();
            pose.translate(-width / 2, 0, -width / 2);

            collector.submitCustomGeometry(pose, RenderTypes.debugQuads(),
                (p, buffer) -> renderQuad(buffer, p, center, color, length, width));

            pose.popPose();
            pose.popPose();
        }
    }

    private static void renderQuad(VertexConsumer vC, PoseStack.Pose pose, Vector3f pos, int color, float length, float width) {

        vC.addVertex(pose, pos.x, pos.y + 1, pos.z)
                .setColor(color)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose, pos.x, pos.y + length, pos.z)
                .setColor(color)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose, pos.x + width, pos.y + length, pos.z)
                .setColor(color)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose, pos.x + width, pos.y + 1, pos.z)
                .setColor(color)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0, 0, 1);
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
    public boolean shouldRender(SolarControllerBE pBlockEntity, Vec3 pCameraPos) {
        return pBlockEntity.getBlockPos().getCenter().distanceTo(pCameraPos) <= 100;
    }

    @Override
    public AABB getRenderBoundingBox(SolarControllerBE blockEntity) {
        return AABB.INFINITE;
    }

    // ------------------------------------------------------------------ state types

    private record PreviewBlock(int x, int y, int z, BlockModelRenderState model) {}

    private record Preview(List<PreviewBlock> blocks, int xOffset, int yOffset) {}

    public static class SolarControllerRenderState extends BlockEntityRenderState {
        public boolean renderBeam;
        public float animationTime;
        public int beamColor;
        public List<PreviewBlock> preview = List.of();
        public int previewXOffset;
        public int previewYOffset;
    }
}
