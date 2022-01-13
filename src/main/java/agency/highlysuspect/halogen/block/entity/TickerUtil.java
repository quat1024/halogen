package agency.highlysuspect.halogen.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TickerUtil {
	@Nullable
	public static <GIVEN extends BlockEntity, EXPECT extends BlockEntity> BlockEntityTicker<EXPECT> downcastTickerMemberFunc(BlockEntityType<EXPECT> givenType, BlockEntityType<GIVEN> expectedType, BlockEntityTickerMember<? super GIVEN> ticker) {
		//noinspection unchecked
		return expectedType == givenType ? (BlockEntityTicker<EXPECT>) ticker : null;
	}
	
	//A version of BlockEntityTicker that takes the BlockEntity parameter *first*, so you can write tickers as actual member functions on the block entity
	//You don't have to *implement* this interface, but passing method references of this shape into downcastFixedTicker is a good idea
	public interface BlockEntityTickerMember<T extends BlockEntity> extends BlockEntityTicker<T> {
		void tick(T self, Level world, BlockPos pos, BlockState state);
		
		@Override
		default void tick(Level world, BlockPos pos, BlockState state, T blockEntity) {
			tick(blockEntity, world, pos, state);
		}
	}
}
