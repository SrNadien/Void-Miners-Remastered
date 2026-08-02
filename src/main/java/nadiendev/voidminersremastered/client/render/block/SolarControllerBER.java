package nadiendev.voidminersremastered.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import nadiendev.voidminersremastered.util.MiscUtil;
import nadiendev.voidminersremastered.world.block.entity.SolarControllerBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;

import java.util.List;

public class SolarControllerBER implements BlockEntityRenderer<SolarControllerBE> {

    public SolarControllerBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SolarControllerBE pBlockEntity, float pPartialTick, PoseStack pose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.foundStructure && pBlockEntity.canSeeSky) {
            long gameTime = pBlockEntity.getLevel().getGameTime();
            float f = (float) Math.floorMod(gameTime, 40) + pPartialTick;

            pose.pushPose();
            pose.translate(0.5f, 0, 0.5f);
            pose.mulPose(Axis.YP.rotationDegrees(f * 2.25f - 45f));

            renderBeam(pBuffer.getBuffer(RenderType.gui()), pose, new Vector3f(0f, 0f, 0f), pBlockEntity.getBeamColor(), 320, 0.3f);

            pose.popPose();
            return;
        }

        if (!pBlockEntity.showStructure) return;

        if(pBlockEntity.getStructure() == null) return;

        String structure = pBlockEntity.getStructure().toString();

        if (!MiscUtil.structureMap.containsKey(structure)) return;

        int xOffset = MiscUtil.structureMap.get(structure).getFirst().size() / 2;
        // yOffset otherwise it renders the multiblock structure under the controller as if it was a miner
        int yOffset = MiscUtil.structureMap.get(structure).size();

        pose.pushPose();
        pose.translate(-xOffset, yOffset, -xOffset);
        pose.pushPose();
        pose.mulPose(Axis.ZN.rotationDegrees(90));

        List<List<List<BlockState>>> blocks = MiscUtil.structureMap.get(structure);

        float blockScale = 0.5f;

        for (int x = 0; x < blocks.size(); x++) {
            List<List<BlockState>> b2 = blocks.get(x);

            for (int y = 0; y < b2.size(); y++) {
                List<BlockState> b3 = b2.get(y);

                for (int z = 0; z < b3.size(); z++) {
                    BlockState block = b3.get(z);

                    pose.pushPose();
                    pose.translate(x, y, z);
                    pose.translate(0.5f, 0.5f, 0.5f);
                    pose.scale(blockScale, blockScale, blockScale);
                    pose.translate(-0.5f, -0.5f, -0.5f);

                    renderBlock(
                            block,
                            pose,
                            pBuffer
                    );

                    pose.popPose();
                }
            }
        }

        pose.popPose();
        pose.popPose();
    }

    public void renderBlock(BlockState state, PoseStack pose, MultiBufferSource buffer) {
        Minecraft minecraft = Minecraft.getInstance();

        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();

        assert minecraft.level != null;
        blockRenderer.renderSingleBlock(
                state,
                pose,
                buffer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                blockRenderer.getBlockModel(state).getModelData(minecraft.level, new BlockPos(0, 0, 0), state, ModelData.EMPTY),
                RenderType.translucent()
        );
    }

    public void renderBeam(VertexConsumer vC, PoseStack pose, Vector3f center, int color, float length, float width) {
        for (int i = 0; i < 4; i++) {
            pose.pushPose();
            pose.mulPose(Axis.YP.rotationDegrees(90 * i));
            pose.pushPose();
            pose.translate(-width / 2, 0, -width / 2);
            renderQuad(vC, pose, center, color, length, width);
            pose.popPose();
            pose.popPose();
        }
    }

    public void renderQuad(VertexConsumer vC, PoseStack pose, Vector3f pos, int color, float length, float width) {

        vC.addVertex(pose.last().pose(), pos.x, pos.y + 1, pos.z)
                .setColor(color)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose.last().pose(), pos.x, pos.y + length, pos.z)
                .setColor(color)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose.last().pose(), pos.x + width, pos.y + length, pos.z)
                .setColor(color)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 0, 1);

        vC.addVertex(pose.last().pose(), pos.x + width, pos.y + 1, pos.z)
                .setColor(color)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0, 0, 1);
    }

    @Override
    public boolean shouldRenderOffScreen(SolarControllerBE pBlockEntity) {
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
}