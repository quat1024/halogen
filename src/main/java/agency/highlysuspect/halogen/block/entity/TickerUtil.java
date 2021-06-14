package agency.highlysuspect.halogen.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TickerUtil {
	@Nullable
	public static <GIVEN extends BlockEntity, EXPECT extends BlockEntity> BlockEntityTicker<EXPECT> downcastFixedTicker(BlockEntityType<EXPECT> givenType, BlockEntityType<GIVEN> expectedType, FixedBlockEntityTicker<? super GIVEN> ticker) {
		//noinspection unchecked
		return expectedType == givenType ? (BlockEntityTicker<EXPECT>) ticker : null;
	}
	
	//A version of BlockEntityTicker that takes the BlockEntity parameter *first*, so you can write tickers as actual member functions on the block entity
	//You don't have to *implement* this interface, but passing method references of this shape into downcastFixedTicker is a good idea
	public interface FixedBlockEntityTicker<T extends BlockEntity> extends BlockEntityTicker<T> {
		void tick(T self, World world, BlockPos pos, BlockState state);
		
		@Override
		default void tick(World world, BlockPos pos, BlockState state, T blockEntity) {
			tick(blockEntity, world, pos, state);
		}
	}
}
