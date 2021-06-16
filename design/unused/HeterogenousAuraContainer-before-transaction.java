package agency.highlysuspect.halogen.aura;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class HeterogenousAuraContainer implements AuraContainer {
	public HeterogenousAuraContainer(int maxSize) {
		contents = new HashMap<>();
		this.maxSize = maxSize;
	}
	
	protected final HashMap<AuraType, AuraStack> contents;
	public final int maxSize;
	
	//deserialization constructor
	private HeterogenousAuraContainer(HashMap<AuraType, AuraStack> contents, int maxSize) {
		this.contents = contents;
		this.maxSize = maxSize;
	}
	
	protected AuraStack getOrCreate(AuraType type) {
		return contents.computeIfAbsent(type, AuraType::makeEmptyStack);
	}
	
	@Override
	public AuraStack accept(AuraStack toAdd, Simulation sim) {
		int totalVolume = computeTotalVolume();
		
		//It's full. Reject all of the aura.
		if(totalVolume == maxSize) {
			return toAdd;
		}
		
		//The amount of remaining space in the container, before adding the aura
		int remainingSpace = maxSize - totalVolume;
		
		//How much aura can be added to the container
		int amountToAdd = Math.min(toAdd.amount(), remainingSpace);
		//How much aura doesn't fit in the container
		int leftover = toAdd.amount() - amountToAdd;
		
		if(sim.forReal()) {
			getOrCreate(toAdd.type()).mutGrow(amountToAdd);
			
			assert totalVolume + amountToAdd == computeTotalVolume();
			assert hasntOverflowed();
		}
		
		assert leftover >= 0;
		return toAdd.withAmount(leftover);
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Simulation sim) {
		AuraStack myStack = getOrCreate(toWithdraw.type());
		
		int amountToWithdraw = MathHelper.clamp(toWithdraw.amount(), 0, myStack.amount());
		
		if(sim.forReal()) {
			int oldTotalVolume = -1;
			//annoyingly you can't "only run a block of code when assertions are enabled", so i stick it as a side effect
			//noinspection AssertWithSideEffects
			assert (oldTotalVolume = computeTotalVolume()) != -1;
			
			myStack.mutShrink(amountToWithdraw);
			
			assert oldTotalVolume - computeTotalVolume() == amountToWithdraw;
		}
		
		//Return the amount that was just withdrawn.
		return toWithdraw.withAmount(amountToWithdraw);
	}
	
	@Override
	public Iterable<AuraStack> contents() {
		return contents.values();
	}
	
	@Override
	public boolean hasAny(AuraType type) {
		return !getOrCreate(type).isEmpty();
	}
	
	public NbtCompound toTag() {
		NbtCompound nbt = toSizelessTag();
		nbt.putInt("maxSize", maxSize);
		return nbt;
	}
	
	public NbtCompound toSizelessTag() {
		NbtCompound contentTag = new NbtCompound();
		contents.forEach((type, stack) -> {
			if(!stack.isEmpty()) contentTag.put(AuraType.REGISTRY.getId(type).toString(), stack.toTypelessTag());
		});
		
		NbtCompound nbt = new NbtCompound();
		nbt.put("contents", contentTag);
		return nbt;
	}
	
	public static HeterogenousAuraContainer fromTag(NbtCompound nbt) {
		return fromSizelessTag(nbt.getInt("maxSize"), nbt);
	}
	
	public static HeterogenousAuraContainer fromSizelessTag(int maxSize, NbtCompound nbt) {
		HashMap<AuraType, AuraStack> contents = new HashMap<>();
		
		NbtCompound contentTag = nbt.getCompound("contents");
		for(String type : contentTag.getKeys()) {
			AuraType aType = AuraType.REGISTRY.get(Identifier.tryParse(type));
			if(aType == null) continue;
			contents.put(aType, AuraStack.fromTypelessTag(aType, contentTag.getCompound(type)));
		}
		
		return new HeterogenousAuraContainer(contents, maxSize);
	}
	
	private int computeTotalVolume() {
		int i = 0;
		for(AuraStack stack : contents()) {
			i += stack.amount();
		}
		assert i >= 0;
		return i;
	}
	
	private boolean hasntOverflowed() {
		return computeTotalVolume() <= maxSize;
	}
	
	@Override
	public String toString() {
		return "HeterogenousAuraContainer{" +
			"contents=" + contents +
			", maxSize=" + maxSize +
			'}';
	}
}
