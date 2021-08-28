package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.aura.HaloAuraTypes;
import agency.highlysuspect.halogen.util.Dirtyable;
import agency.highlysuspect.halogen.util.transaction.Participant;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import com.mojang.serialization.Codec;

import java.util.Collection;
import java.util.Collections;

public class UnboundedHomogenousAuraContainer implements Participant<AuraStack<?>>, AuraContainer, Dirtyable {
	public UnboundedHomogenousAuraContainer() {
		this(HaloAuraTypes.WHITE.emptyStackOf());
	}
	
	public UnboundedHomogenousAuraContainer(AuraStack<?> stack) {
		this.stack = stack;
	}
	
	protected AuraStack<?> stack;
	protected boolean dirty = false;
	public static final Codec<UnboundedHomogenousAuraContainer> CODEC = AuraStack.CODEC.xmap(UnboundedHomogenousAuraContainer::new, x -> x.stack);
	
	@Override
	public AuraStack<?> backupState() {
		return stack;
	}
	
	@Override
	public void restoreState(AuraStack<?> state) {
		stack = state;
	}
	
	@Override
	public <T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toAdd)) {
			stack = stack.grownBy(toAdd.castKey());
			dirty = true;
			
			return toAdd.withAmount(0);
		} else return toAdd;
	}
	
	@Override
	public <T> AuraStack<T> withdraw(AuraStack<T> toWithdraw, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toWithdraw)) {
			int howMuch = Math.min(stack.amount(), toWithdraw.amount());
			stack = stack.shrunkBy(howMuch);
			if(howMuch != 0) dirty = true;
			
			return toWithdraw.withAmount(howMuch);
		} else return toWithdraw.withAmount(0);
	}
	
	@Override
	public Collection<AuraStack<?>> contents() {
		return Collections.singletonList(stack);
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public void clean() {
		dirty = false;
	}
}
