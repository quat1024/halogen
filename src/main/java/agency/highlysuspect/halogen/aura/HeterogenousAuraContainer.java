package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class HeterogenousAuraContainer implements AuraContainer {
	public HeterogenousAuraContainer(int maxSize) {
		contents = new HashMap<>();
		this.maxSize = maxSize;
	}
	
	protected final HashMap<AuraType, AuraStack> contents;
	public final int maxSize;
	
	public static final Codec<HeterogenousAuraContainer> CODEC = RecordCodecBuilder.create(i -> i.group(
		CodecUtil.derivedMutableMapOf(AuraStack.CODEC, AuraStack::type).fieldOf("contents").forGetter(c -> c.contents),
		Codec.INT.fieldOf("maxSize").forGetter(c -> c.maxSize)
	).apply(i, HeterogenousAuraContainer::new));
	
	//deserialization constructor
	private HeterogenousAuraContainer(HashMap<AuraType, AuraStack> contents, int maxSize) {
		this.contents = contents;
		this.maxSize = maxSize;
	}
	
	@Override
	public AuraStack accept(AuraStack toAdd, Simulation sim) {
		int totalVolume = computeTotalVolume();
		
		//Case 1: It's full. Reject all of the aura.
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
			contents.put(toAdd.type(), contents.computeIfAbsent(toAdd.type(), AuraStack::empty).grow(amountToAdd));
			
			assert totalVolume + amountToAdd == computeTotalVolume();
			assert hasntOverflowed();
		}
		
		assert leftover >= 0;
		return toAdd.withAmount(leftover);
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Simulation sim) {
		AuraStack stackOfThisType = contents.computeIfAbsent(toWithdraw.type(), AuraStack::empty);
		
		int amountToWithdraw = MathHelper.clamp(stackOfThisType.amount() - toWithdraw.amount(), 0, stackOfThisType.amount());
		
		if(sim.forReal()) {
			int totalVolume_ = -1;
			//annoyingly you can't "only run a block of code when assertions are enabled", so i stick it as a side effect
			//noinspection AssertWithSideEffects
			assert (totalVolume_ = computeTotalVolume()) != -1;
			
			contents.put(stackOfThisType.type(), stackOfThisType.shrink(amountToWithdraw));
			
			assert computeTotalVolume() - totalVolume_ == amountToWithdraw;
		}
		
		//Return the amount that was just withdrawn.
		return toWithdraw.withAmount(amountToWithdraw);
	}
	
	@Override
	public Iterable<AuraStack> contents() {
		return contents.values();
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
}
