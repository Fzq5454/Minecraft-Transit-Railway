package mtr.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.mappings.UtilitiesClient;
import mtr.data.RailwayData;
import mtr.mappings.BlockEntityRendererMapper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Set;

public class RenderPSDAPGDoorStationName<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends RenderPSDAPGDoor<T> {

    public RenderPSDAPGDoorStationName(BlockEntityRenderDispatcher dispatcher, int type) {
        super(dispatcher, type);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        
        // 只有顶部的门块才渲染站名字
        final Level world = entity.getLevel();
        if (world == null) {
            return;
        }
        
        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        
        // 只在顶部门块渲染站名字
        if (IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }
        
        // 获取站台信息并渲染站名字
        renderStationName(entity, pos, state, matrices, vertexConsumers, light, overlay);
    }
    
    private void renderStationName(T entity, BlockPos pos, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING);
        final boolean side = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.SIDE) == EnumSide.RIGHT;
        
        // 获取最近的站台ID
        final DataCache dataCache = ClientData.DATA_CACHE;
        final Set<Platform> platforms = dataCache.platforms;
        final long platformId = RailwayData.getClosePlatformId(platforms, dataCache, pos);
        
        if (platformId != 0) {
            // 获取站台名称
            final String stationName = dataCache.getStationName(dataCache.platforms, platformId);
            
            // 渲染站名字
            RenderTrains.scheduleRender(new ResourceLocation("mtr:textures/block/sign/white.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                // 设置位置和旋转
                matricesNew.pushPose();
                matricesNew.translate(0.5 + pos.getX(), pos.getY() + 1.2, 0.5 + pos.getZ());
                UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
                UtilitiesClient.rotateXDegrees(matricesNew, 180);
                
                // 根据门的位置调整文字位置
                if (side) {
                    matricesNew.translate(-0.5, 0, 0);
                } else {
                    matricesNew.translate(0.5, 0, 0);
                }
                
                // 渲染文字背景
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.5F, 0, -0.501F, 0.5F, 0.1F, 0.501F, facing, -1, light);
                
                // 渲染站名字
                IDrawing.drawText(matricesNew, vertexConsumer, stationName, 0, 0.05F, 0, 0.03F, 0xFFFFFF, false, light, IDrawing.Alignment.MIDDLE);
                
                matricesNew.popPose();
            });
        }
    }
}