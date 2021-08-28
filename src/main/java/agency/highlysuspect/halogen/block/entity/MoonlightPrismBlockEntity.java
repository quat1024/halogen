package agency.highlysuspect.halogen.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class MoonlightPrismBlockEntity extends BlockEntity {
	public MoonlightPrismBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int radius) {
		super(type, pos, state);
		this.radius = radius;
	}
	
	public static MoonlightPrismBlockEntity large(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.LARGE_MOONLIGHT_PRISM, pos, state, 15);
	}
	
	public static MoonlightPrismBlockEntity small(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.SMALL_MOONLIGHT_PRISM, pos, state, 5);
	}
	
	public final int radius;
}
