package mtr.block;

import mtr.BlockEntityTypes;
import mtr.Items;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPSDDoorStationName extends BlockPSDDoor {

    public BlockPSDDoorStationName(int style) {
        super(style);
    }

    @Override
    public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityPSDDoorStationName(style, pos, state);
    }

    @Override
    public Item asItem() {
        return style == 0 ? Items.PSD_DOOR_STATION_NAME_1.get() : Items.PSD_DOOR_STATION_NAME_2.get();
    }

    public static class TileEntityPSDDoorStationName extends TileEntityPSDDoor {

        public TileEntityPSDDoorStationName(int style, BlockPos pos, BlockState state) {
            super(style, pos, state);
        }
    }
}