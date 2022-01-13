package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.block.HaloBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HaloBlockEntityTypes {
	public static final BlockEntityType<MoonlightPrismBlockEntity> LARGE_MOONLIGHT_PRISM = make(MoonlightPrismBlockEntity::large, HaloBlocks.LARGE_MOONLIGHT_PRISM);
	public static final BlockEntityType<MoonlightPrismBlockEntity> SMALL_MOONLIGHT_PRISM = make(MoonlightPrismBlockEntity::small, HaloBlocks.SMALL_MOONLIGHT_PRISM);
	
	private static <T extends BlockEntity> BlockEntityType<T> make(FabricBlockEntityTypeBuilder.Factory<T> factory, Block... applicableBlocks) {
		return FabricBlockEntityTypeBuilder.create(factory, applicableBlocks).build();
	}
	
	private static <T extends BlockEntity> BlockEntityType<T> reg(String id, BlockEntityType<T> type) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, Init.id(id), type);
	}
	
	public static void onInitialize() {
		reg("large_moonlight_prism", LARGE_MOONLIGHT_PRISM);
		reg("small_moonlight_prism", SMALL_MOONLIGHT_PRISM);
	}
}
