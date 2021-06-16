package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.aura.*;
import agency.highlysuspect.halogen.jankComponent.HasAuraContainer;
import agency.highlysuspect.halogen.util.NbtHelper2;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class NodeBlockEntity extends BlockEntity implements HasAuraContainer {
	public NodeBlockEntity(BlockPos pos, BlockState state) {
		super(HaloBlockEntityTypes.NODE, pos, state);
	}
	
	private static final int INCOMING_SIZE = 100;
	private static final int MAIN_SIZE = 1000;
	
	private static final int TICK_INTERVAL = 8;
	private static final int MAX_BINDING_DISTANCE = 10;
	private static final int MAX_AURA_SEND = 2; //Todo increase this a lot
	
	private final NodeAuraContainer container = new NodeAuraContainer(
		new UnboundedAuraContainer(AuraStack.empty()),
		new UnboundedAuraContainer(AuraStack.empty())
	);
	private List<BlockPos> bindings = new ArrayList<>();
	
	public void tickServer(World world, BlockPos pos, BlockState state) {
		markDirty(); //TODO chill with the markdirties
		
		long mod = world.getTime() % TICK_INTERVAL;
		if(mod == 0) {
			ListIterator<BlockPos> poserator = bindings.listIterator();
			while(poserator.hasNext()) {
				BlockPos binding = poserator.next();
				if(!world.getChunkManager().isChunkLoaded(binding.getX() / 16, binding.getZ() / 16)) continue;
				
				if(!(world.getBlockEntity(binding) instanceof NodeBlockEntity otherNode)) {
					//Remove the binding since whatever's here is not a node anymore.
					poserator.remove();
					continue;
				}
				
				//TODO max_aura_send should proportionally adjust the amount of aura of each type
				// Rn if there's two aura types, it just sends them both, twice as fast
				// Also the number of bindings should affect it too.
				AuraContainer other = otherNode.getAuraContainer();
				try(Transaction tx = new Transaction()) {
					//TODO: I need a forEach
					AuraStack toSend = container.withdraw(new AuraStack(AuraType.WHITE, MAX_AURA_SEND), tx);
					AuraStack leftoverAfterSend = other.accept(toSend, tx);
					if(leftoverAfterSend.isEmpty()) {
						tx.commit();
					}
				}
			}
		} else if(mod == 1) {
			container.pourIncomingIntoMain();
		}
	}
	
	public boolean isValidBinding(BlockPos other) {
		assert world != null;
		
		//Cannot bind to self
		if(other.equals(getPos())) return false;
		//Can only bind downwards
		if(other.getY() >= getPos().getY()) return false;
		//Can only bind within range
		if(other.getSquaredDistance(getPos()) > MAX_BINDING_DISTANCE * MAX_BINDING_DISTANCE) return false;
		//Can only bind to aura nodes
		return world.getBlockEntity(other) instanceof NodeBlockEntity;
	}
	
	public boolean onLinkingWand(BlockPos other) {
		assert world != null;
		
		if(!isValidBinding(other)) return false;
		
		if(!world.isClient) {
			if(bindings.contains(other)) bindings.remove(other);
			else bindings.add(other);
			
			markDirty();
		}
		
		return true;
	}
	
	@Override
	public AuraContainer getAuraContainer() {
		return container;
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.put("aura", container.writeNbt());
		nbt.put("bindings", NbtHelper2.fromBlockPosList(bindings));
		return super.writeNbt(nbt);
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		container.readNbt(nbt.getCompound("aura"));
		bindings = NbtHelper2.toBlockPosArrayList(nbt.getList("bindings", 10));
	}
}
