package mtr.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mtr.block.BlockPSDAPGDoorBase;
import mtr.block.BlockPSDDoorStationName;
import mtr.block.IBlock;
import mtr.client.ClientData;
import mtr.client.IDrawing;
import mtr.data.IGui;
import mtr.mappings.UtilitiesClient;
import mtr.mappings.BlockEntityRendererMapper;
import mtr.render.RenderTrains;
import net.minecraft.client.renderer.RenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

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
        final Direction facing = state.getValue(BlockPSDAPGDoorBase.FACING);
        final boolean side = state.getValue(BlockPSDAPGDoorBase.SIDE) == BlockPSDAPGDoorBase.EnumSide.RIGHT;
        final long platformId = entity.getPlatformId();
        
        if (platformId != 0 && ClientData.DATA_CACHE.platformIdToStation.containsKey(platformId)) {
            final String stationName = ClientData.DATA_CACHE.platformIdToStation.get(platformId).name;
            
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

                IDrawing.drawStringWithFont(matricesNew, Minecraft.getInstance().font, vertexConsumers.getBuffer(RenderLayer.getText()), stationName, 0, 0.05F, light);
                
                matricesNew.popPose();
            });
        }
    }
}