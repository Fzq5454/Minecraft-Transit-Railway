package mtr.block;

import mtr.BlockEntityTypes;
import mtr.Items;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
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
        return null;
    }

    public static class TileEntityPSDDoorStationName extends TileEntityPSDDoor {
        private long platformId;
        private static final String KEY_PLATFORM_ID = "platform_id";

        public TileEntityPSDDoorStationName(int style, BlockPos pos, BlockState state) {
            super(pos, state);
            // style参数保留以保持与BlockEntityTypes.java的兼容性，但不再传递给父类
        }

        @Override
        public void readCompoundTag(CompoundTag compoundTag) {
            super.readCompoundTag(compoundTag);
            platformId = compoundTag.getLong(KEY_PLATFORM_ID);
        }

        @Override
        public void writeCompoundTag(CompoundTag compoundTag) {
            super.writeCompoundTag(compoundTag);
            compoundTag.putLong(KEY_PLATFORM_ID, platformId);
        }

        public void setPlatformId(long platformId) {
            this.platformId = platformId;
            setChanged();
            syncData();
        }

        public long getPlatformId() {
            return platformId;
        }
    }
}