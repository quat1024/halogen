package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.block.HaloBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class HaloBlockEntityTypes {
	public static final BlockEntityType<NodeBlockEntity> NODE = reg("node", FabricBlockEntityTypeBuilder.create(NodeBlockEntity::new, HaloBlocks.BASIC_NODE).build());
	
	private static <T extends BlockEntity> BlockEntityType<T> reg(String id, BlockEntityType<T> type) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, Init.id(id), type);
	}
	
	public static void onInitialize() {
		//Classload
	}
}
