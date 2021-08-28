package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.block.entity.MoonlightPrismBlockEntity;
import agency.highlysuspect.halogen.block.entity.TickerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MoonlightPrismBlock extends Block implements BlockEntityProvider {
	public MoonlightPrismBlock(Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> typeFactory, Settings settings) {
		super(settings);
		this.typeFactory = typeFactory;
	}
	
	private final Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> typeFactory;
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return typeFactory.get().instantiate(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if(world.isClient()) return null;
		else return TickerUtil.downcastTickerMemberFunc(type, typeFactory.get(), MoonlightPrismBlockEntity::tick);
	}
}
