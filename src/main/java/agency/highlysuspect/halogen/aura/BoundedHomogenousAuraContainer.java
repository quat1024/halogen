package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Transaction;

public class BoundedHomogenousAuraContainer extends UnboundedHomogenousAuraContainer {
	public BoundedHomogenousAuraContainer(int maxSize) {
		super();
		this.maxSize = maxSize;
	}
	
	public BoundedHomogenousAuraContainer(AuraStack stack, int maxSize) {
		super(stack);
		this.maxSize = maxSize;
	}
	
	private final int maxSize;
	
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toAdd)) {
			int remainingSpace = maxSize - stack.amount();
			int amountToAdd = Math.min(toAdd.amount(), remainingSpace);
			int leftover = toAdd.amount() - amountToAdd;
			
			stack.mutGrow(amountToAdd);
			dirty = true;
			return stack.withAmount(leftover);
		} else return toAdd;
	}
}