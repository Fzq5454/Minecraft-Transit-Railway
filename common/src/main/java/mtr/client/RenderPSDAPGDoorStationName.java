package mtr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.data.BlockEntityBase;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import mtr.render.RenderTrains;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import java.util.function.Consumer;

public class RenderPSDAPGDoorStationName<T extends BlockEntityBase> extends BlockEntityRendererMapper<T> {

    public RenderPSDAPGDoorStationName(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity instanceof BlockPSDAPGDoorBase.ITileEntityPSDAPGDoorBase) {
            final BlockPSDAPGDoorBase.ITileEntityPSDAPGDoorBase psdDoorEntity = (BlockPSDAPGDoorBase.ITileEntityPSDAPGDoorBase) entity;
            final Direction facing = psdDoorEntity.getDirection();
            final float doorPosition = psdDoorEntity.getDoorPosition(tickDelta);
            final boolean isRight = psdDoorEntity.getSide() == BlockPSDAPGDoorBase.EnumSide.RIGHT;


            renderStationName(matrices, vertexConsumers, entity.getBlockPos(), facing, doorPosition, isRight, light);
        }
    }

    private void renderStationName(PoseStack matrices, MultiBufferSource vertexConsumers, net.minecraft.core.BlockPos pos, Direction facing, float doorPosition, boolean isRight, int light) {
        final long platformId = ClientData.DATA_CACHE.getPlatformId(pos);
        if (platformId > 0) {
            final String stationName = ClientData.DATA_CACHE.platformIdToStation.get(platformId).name;
            final int textColor = 0xFF000000;
            final float scale = 0.2f;
            final float yOffset = 0.8f;
            

            final double xOffset = isRight ? (1 - doorPosition) : doorPosition;
            final double zOffset = 0.02;
            
            matrices.pushPose();
            UtilitiesClient.rotate90Degrees(matrices, facing);
            matrices.translate(isRight ? xOffset - 1 : xOffset, yOffset, zOffset);
            matrices.scale(scale, scale, scale);
            
            final Consumer<PoseStack> renderCallback = matricesNew -> {
                matricesNew.pushPose();
                final int[] dimensions = new int[2];
                final byte[] pixels = ClientData.DATA_CACHE.getTextPixels(stationName, dimensions, 200, 50, 20, 20, 0, null, true);
                RenderTrains.renderPixels(matricesNew, vertexConsumers, pixels, dimensions[0], dimensions[1], 0, 0, textColor, light);
                matricesNew.popPose();
            };
            
            RenderTrains.scheduleRender(renderCallback);
            matrices.popPose();
        }
    }
}