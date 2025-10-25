package mtr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.IBlock;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.mappings.UtilitiesClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import mtr.render.RenderTrains;

public class RenderPSDAPGDoorStationName<T extends BlockPSDAPGDoorBase.TileEntityPSDAPGDoorBase> extends BlockEntityRendererMapper<T> {

    public RenderPSDAPGDoorStationName(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Level world = entity.getLevel();
        if (world == null) {
            return;
        }
        
        final BlockPos pos = entity.getBlockPos();
        final BlockState state = world.getBlockState(pos);
        if (IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }
        renderStationName(entity, pos, state, matrices, vertexConsumers, light, overlay);
    }
    
    private void renderStationName(T entity, BlockPos pos, BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        final Direction facing = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.FACING);
        final boolean side = IBlock.getStatePropertySafe(state, BlockPSDAPGDoorBase.SIDE) == BlockPSDAPGDoorBase.EnumSide.RIGHT;
        final ClientCache dataCache = ClientData.DATA_CACHE;
        final long platformId = dataCache.getClosePlatformId(pos);
        
        if (platformId != 0 && dataCache.platformIdToStation.containsKey(platformId)) {
            final String stationName = dataCache.platformIdToStation.get(platformId).name;
            
            RenderTrains.scheduleRender(new ResourceLocation("mtr:textures/block/sign/white.png"), false, RenderTrains.QueuedRenderLayer.EXTERIOR, (matricesNew, vertexConsumer) -> {
                matricesNew.pushPose();
                matricesNew.translate(0.5 + pos.getX(), pos.getY() + 1.2, 0.5 + pos.getZ());
                UtilitiesClient.rotateYDegrees(matricesNew, -facing.toYRot());
                UtilitiesClient.rotateXDegrees(matricesNew, 180);
                if (side) {
                    matricesNew.translate(-0.5, 0, 0);
                } else {
                    matricesNew.translate(0.5, 0, 0);
                }
                
                IDrawing.drawTexture(matricesNew, vertexConsumer, -0.5F, 0, -0.501F, 0.5F, 0.1F, 0.501F, facing, -1, light);
                
                IDrawing.drawStringWithFont(matricesNew, vertexConsumer, stationName, 0, 0.05F, 0xFFFFFF, 0.03F, false, light, IGui.HorizontalAlignment.CENTER);
                
                matricesNew.popPose();
            });
        }
    }
}