package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.aura.container.AuraContainer;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.aura.container.BoundedHeterogeneousAuraContainer;
import agency.highlysuspect.halogen.aura.container.NodeAuraContainer;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import agency.highlysuspect.halogen.util.NbtHelper2;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashSet;
import java.util.Set;

public class NodeBlockEntity extends BlockEntity implements HasAuraContainer {
	public NodeBlockEntity(BlockPos pos, BlockState state) {
		super(HaloBlockEntityTypes.NODE, pos, state);
	}
	
	private static final int INCOMING_SIZE = 100;
	private static final int MAIN_SIZE = 1000;
	
	private static final int TICK_INTERVAL = 8;
	private static final int MAX_BINDING_DISTANCE = 10;
	private static final int MAX_AURA_SEND = 2; //Todo increase this a lot
	
	private NodeAuraContainer container = new NodeAuraContainer(
		new BoundedHeterogeneousAuraContainer(INCOMING_SIZE),
		new BoundedHeterogeneousAuraContainer(MAIN_SIZE)
	);
	private Set<BlockPos> bindings = new HashSet<>();
	
	public void tickServer(Level world, BlockPos pos, BlockState state) {
		long phase = world.getGameTime() % TICK_INTERVAL;
		if(phase == 0) {
			HashSet<BlockPos> bindingsToRemove = null;
			
			for(BlockPos binding : bindings) {
				if(!world.getChunkSource().hasChunk(binding.getX() / 16, binding.getZ() / 16)) continue;
				
				if(!(world.getBlockEntity(binding) instanceof NodeBlockEntity otherNode)) {
					//Remove the binding, since whatever's here is not a node anymore.
					if(bindingsToRemove == null) bindingsToRemove = new HashSet<>();
					bindingsToRemove.add(binding);
					continue;
				}
				
				//TODO max_aura_send should proportionally adjust the amount of aura sent of each type
				// Rn if there's two aura types, it just sends them both, twice as fast
				// Also the number of bindings should affect it too.
				AuraContainer other = otherNode.getAuraContainer();
				try(Transaction tx = new Transaction()) {
					for(AuraStack<?> stack : container.contentsCopy()) {
						AuraStack<?> toSend = stack.reduceToAtMost(MAX_AURA_SEND);
						AuraStack<?> leftover = container.pourStackIntoOverflow(toSend, other, container.main(), tx);
						assert leftover.isEmpty();
					}
					
					tx.commit();
				}
			}
			
			if(bindingsToRemove != null) bindingsToRemove.forEach(this::unbindFrom);
		} else if(phase == 1) {
			container.pourIncomingIntoMain();
		}
		
		if(container.isDirty()) {
			container.clean();
			setChanged();
		}
	}
	
	public boolean isValidBinding(BlockPos other) {
		assert level != null;
		
		//Cannot bind to self
		if(other.equals(getBlockPos())) return false;
		//Can only bind downwards
		if(other.getY() >= getBlockPos().getY()) return false;
		//Can only bind within range
		if(other.distSqr(getBlockPos()) > MAX_BINDING_DISTANCE * MAX_BINDING_DISTANCE) return false;
		//Can only bind to aura nodes
		return level.getBlockEntity(other) instanceof NodeBlockEntity;
	}
	
	public void bindTo(BlockPos other) {
		assert isValidBinding(other);
		
		bindings.add(other);
		setChanged();
	}
	
	public void unbindFrom(BlockPos other) {
		assert bindings.contains(other);
		
		bindings.remove(other);
		setChanged();
	}
	
	public boolean onLinkingWand(BlockPos other) {
		assert level != null;
		
		if(!isValidBinding(other)) return false;
		
		if(!level.isClientSide) {
			if(bindings.contains(other)) unbindFrom(other);
			else bindTo(other);
		}
		
		return true;
	}
	
	@Override
	public AuraContainer getAuraContainer() {
		return container;
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		nbt.put("aura", container.codec.encodeStart(NbtOps.INSTANCE, container).getOrThrow(false, Init.LOG::error));
		nbt.put("bindings", NbtHelper2.fromBlockPosSet(bindings));
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		container = container.codec.parse(NbtOps.INSTANCE, nbt.get("aura")).getOrThrow(false, Init.LOG::error);
		bindings = NbtHelper2.toBlockPosHashSet(nbt.getList("bindings", 10));
	}
}
