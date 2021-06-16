package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Transaction;

import java.util.List;

public class BoundedHeterogenousAuraContainer extends UnboundedHeterogenousAuraContainer {
	public BoundedHeterogenousAuraContainer(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public BoundedHeterogenousAuraContainer(List<AuraStack> contents, int maxSize) {
		super(contents);
		this.maxSize = maxSize;
	}
	
	protected final int maxSize;
	
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(toAdd.isEmpty()) return toAdd;
		
		int occupiedSpace = computeTotalVolume();
		if(occupiedSpace == maxSize) return toAdd; //It's full, reject any aura.
		
		int remainingSpace = maxSize - occupiedSpace;
		int amountToAdd = Math.min(toAdd.amount(), remainingSpace);
		int leftover = toAdd.amount() - amountToAdd;
		
		findOrCreateOfType(toAdd).mutGrow(amountToAdd);
		
		assert occupiedSpace + amountToAdd == computeTotalVolume();
		assert leftover >= 0;
		
		return toAdd.withAmount(leftover);
	}
}
