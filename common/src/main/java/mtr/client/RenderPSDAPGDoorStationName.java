package mtr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.client.ClientCache;
import mtr.data.Platform;
import mtr.data.RailwayData;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.Set;
import mtr.mappings.IDrawing;
import net.minecraft.client.renderer.RenderType;

public class RenderPSDAPGDoorStationName<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends BlockEntityRendererMapper<T> {
    
    private Direction getFacing(T entity) {
        if (entity.getLevel() != null) {
            return IBlock.getStatePropertySafe(entity.getLevel().getBlockState(entity.getBlockPos()), BlockPSDAPGDoorBase.FACING);
        }
        return Direction.NORTH;
    }
    
    private float getDoorPosition(T entity, float tickDelta) {
        return entity.getOpen(tickDelta);
    }
    
    private boolean getIsRight(T entity) {
        if (entity.getLevel() != null) {
            return IBlock.getStatePropertySafe(entity.getLevel().getBlockState(entity.getBlockPos()), BlockPSDAPGDoorBase.SIDE) == BlockPSDAPGDoorBase.EnumSide.RIGHT;
        }
        return false;
    }
    
    private long getPlatformId(BlockPos pos, ClientCache dataCache) {
        final Set<Platform> platforms = dataCache.platforms;
        return RailwayData.getClosePlatformId(platforms, dataCache, pos);
    }
    
    private void renderPixels(PoseStack matrices, MultiBufferSource vertexConsumers, byte[] pixels, int width, int height, int x, int y, int color, int light) {
        final int r = (color >> 16) & 0xFF;
        final int g = (color >> 8) & 0xFF;
        final int b = color & 0xFF;
        final int a = (color >> 24) & 0xFF;
        
        final float pixelWidth = 1.0f / width;
        final float pixelHeight = 1.0f / height;
        
        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                final int pixelIndex = (py * width + px) * 4;
                if (pixelIndex + 3 < pixels.length && pixels[pixelIndex + 3] > 0) {
                    final float alpha = pixels[pixelIndex + 3] / 255.0f;
                    IDrawing.drawTexture(matrices, vertexConsumers.getBuffer(RenderType.solid()),
                            x + px * pixelWidth, y + py * pixelHeight, 0,
                            x + (px + 1) * pixelWidth, y + (py + 1) * pixelHeight, 0,
                            Direction.UP, (int)(r * alpha), (int)(g * alpha), (int)(b * alpha), (int)(a * alpha), light);
                }
            }
        }
    }

    public RenderPSDAPGDoorStationName(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity != null) {
            final BlockPos pos = entity.getBlockPos();
            final Direction facing = getFacing(entity);
            final float doorPosition = getDoorPosition(entity, tickDelta);
            final boolean isRight = getIsRight(entity);

            renderStationName(matrices, vertexConsumers, pos, facing, doorPosition, isRight, light);
        }
    }

    private void renderStationName(PoseStack matrices, MultiBufferSource vertexConsumers, BlockPos pos, Direction facing, float doorPosition, boolean isRight, int light) {
        final long platformId = getPlatformId(pos, ClientData.DATA_CACHE);
        if (platformId > 0) {
            final String stationName = ClientData.DATA_CACHE.platformIdToStation.get(platformId).name;
            final int textColor = 0xFF000000;
            final float scale = 0.2f;
            final float yOffset = 0.8f;
            

            final double xOffset = isRight ? (1 - doorPosition) : doorPosition;
            final double zOffset = 0.02;
            
            matrices.pushPose();
            UtilitiesClient.rotateYDegrees(matrices, facing.toYRot());
            matrices.translate(isRight ? xOffset - 1 : xOffset, yOffset, zOffset);
            matrices.scale(scale, scale, scale);
            
            final Consumer<PoseStack> renderCallback = matricesNew -> {
                matricesNew.pushPose();
                final int[] dimensions = new int[2];
                final byte[] pixels = ClientData.DATA_CACHE.getTextPixels(stationName, dimensions, 200, 50, 20, 20, 0, null, true);
                renderPixels(matricesNew, vertexConsumers, pixels, dimensions[0], dimensions[1], 0, 0, textColor, light);
                matricesNew.popPose();
            };
            
            renderCallback.accept(matrices);
            matrices.popPose();
        }
    }
}