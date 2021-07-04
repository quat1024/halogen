package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.aura.AuraMap;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import com.mojang.serialization.Codec;

public class BoundedHeterogeneousAuraContainer extends UnboundedHeterogeneousAuraContainer {
	public BoundedHeterogeneousAuraContainer(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public BoundedHeterogeneousAuraContainer(AuraMap contents, int maxSize) {
		super(contents);
		this.maxSize = maxSize;
	}
	
	protected final int maxSize;
	
	@Override
	public <T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(toAdd.isEmpty()) return toAdd;
		
		int occupiedSpace = computeTotalVolume();
		if(occupiedSpace == maxSize) return toAdd; //It's full, reject any aura.
		
		int remainingSpace = maxSize - occupiedSpace;
		int amountToAdd = Math.min(toAdd.amount(), remainingSpace);
		int leftover = toAdd.amount() - amountToAdd;
		
		if(amountToAdd > 0) {
			contents.modify(toAdd.key(), s -> s.grownBy(amountToAdd));
			dirty = true;
		}
		
		assert occupiedSpace + amountToAdd == computeTotalVolume();
		assert leftover >= 0;
		
		return toAdd.withAmount(leftover);
	}
	
	public Codec<BoundedHeterogeneousAuraContainer> sizedCodec() {
		return AuraMap.CODEC.xmap(map -> new BoundedHeterogeneousAuraContainer(map, maxSize), c -> c.contents);
	}
}
