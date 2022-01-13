package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.block.entity.MoonlightPrismBlockEntity;
import agency.highlysuspect.halogen.block.entity.TickerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MoonlightPrismBlock extends Block implements EntityBlock {
	public MoonlightPrismBlock(Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> typeFactory, Properties settings) {
		super(settings);
		this.typeFactory = typeFactory;
	}
	
	private final Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> typeFactory;
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return typeFactory.get().create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if(world.isClientSide()) return null;
		else return TickerUtil.downcastTickerMemberFunc(type, typeFactory.get(), MoonlightPrismBlockEntity::tick);
	}
}
