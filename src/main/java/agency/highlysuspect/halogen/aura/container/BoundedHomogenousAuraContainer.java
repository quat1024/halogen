package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import com.mojang.serialization.Codec;

public class BoundedHomogenousAuraContainer extends UnboundedHomogenousAuraContainer {
	public BoundedHomogenousAuraContainer(int maxSize) {
		super();
		this.maxSize = maxSize;
	}
	
	public BoundedHomogenousAuraContainer(AuraStack<?> stack, int maxSize) {
		super(stack);
		this.maxSize = maxSize;
	}
	
	private final int maxSize;
	
	@Override
	public <T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toAdd)) {
			int remainingSpace = maxSize - stack.amount();
			int amountToAdd = Math.min(toAdd.amount(), remainingSpace);
			int leftover = toAdd.amount() - amountToAdd;
			
			stack = stack.grownBy(amountToAdd);
			dirty = true;
			return toAdd.withAmount(leftover);
		} else return toAdd;
	}
	
	public Codec<BoundedHomogenousAuraContainer> sizedCodec() {
		return AuraStack.CODEC.xmap(stack -> new BoundedHomogenousAuraContainer(stack, maxSize), c -> c.stack);
	}
}