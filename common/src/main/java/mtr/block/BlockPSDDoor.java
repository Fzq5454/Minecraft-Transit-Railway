package mtr.block;

import mtr.BlockEntityTypes;
import mtr.Items;
import mtr.mappings.BlockEntityMapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPSDDoor extends BlockPSDAPGDoorBase {

	protected final int style;

	public BlockPSDDoor() {
		super();
		this.style = 0;
	}

	public BlockPSDDoor(int style) {
		super();
		this.style = style;
	}

	@Override
	public BlockEntityMapper createBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityPSDDoor(pos, state);
	}

	@Override
	public Item asItem() {
		return Items.PSD_DOOR_1.get();
	}

	public static class TileEntityPSDDoor extends TileEntityPSDAPGDoorBase {

		public TileEntityPSDDoor(BlockPos pos, BlockState state) {
			super(BlockEntityTypes.PSD_DOOR_TILE_ENTITY.get(), pos, state);
		}
	}
}
