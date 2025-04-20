package mtr.mappings;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityMapper extends BlockEntity {

	public BlockEntityMapper(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public final void load(CompoundTag compoundTag) {
		super.load(compoundTag);
		readCompoundTag(compoundTag);
	}

	@Override
	public final void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);
		writeCompoundTag(compoundTag);
	}

	public void readCompoundTag(CompoundTag compoundTag) {
	}

	public void writeCompoundTag(CompoundTag compoundTag) {
	}
}
