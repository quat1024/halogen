package agency.highlysuspect.halogen.block;

import agency.highlysuspect.halogen.block.entity.MoonlightPrismBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MoonlightPrismBlock extends Block implements BlockEntityProvider {
	public MoonlightPrismBlock(Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> type, Settings settings) {
		super(settings);
		this.type = type;
	}
	
	private final Supplier<BlockEntityType<? extends MoonlightPrismBlockEntity>> type;
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return type.get().instantiate(pos, state);
	}
}
