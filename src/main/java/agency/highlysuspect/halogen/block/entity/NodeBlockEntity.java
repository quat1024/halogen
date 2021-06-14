package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.aura.AuraContainer;
import agency.highlysuspect.halogen.aura.HeterogenousAuraContainer;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import agency.highlysuspect.halogen.util.CodecUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeBlockEntity extends BlockEntity implements HasAuraContainer {
	public NodeBlockEntity(BlockPos pos, BlockState state) {
		super(HaloBlockEntityTypes.NODE, pos, state);
	}
	
	private HeterogenousAuraContainer container = new HeterogenousAuraContainer(1000);
	
	public void tickServer(World world, BlockPos pos, BlockState state) {
		
	}
	
	@Override
	public AuraContainer getAuraContainer() {
		return container;
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.put("aura", CodecUtil.writeNbt(HeterogenousAuraContainer.CODEC, container));
		return super.writeNbt(nbt);
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		container = CodecUtil.readNbt(HeterogenousAuraContainer.CODEC, nbt.getCompound("aura"));
	}
}
