package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.aura.AuraMap;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.util.Dirtyable;
import agency.highlysuspect.halogen.util.transaction.Participant;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import com.mojang.serialization.Codec;

import java.util.Collection;

public class UnboundedHeterogeneousAuraContainer implements Participant<AuraMap>, AuraContainer, Dirtyable {
	public UnboundedHeterogeneousAuraContainer() {
		this.contents = new AuraMap();
	}
	
	public UnboundedHeterogeneousAuraContainer(AuraMap contents) {
		//defensive copy, smh my horns
		this.contents = contents;
	}
	
	protected AuraMap contents;
	protected boolean dirty = false;
	public static final Codec<UnboundedHeterogeneousAuraContainer> CODEC = AuraMap.CODEC.xmap(UnboundedHeterogeneousAuraContainer::new, x -> x.contents);
	
	@Override
	public AuraMap backupState() {
		return contents.copy();
	}
	
	@Override
	public void restoreState(AuraMap state) {
		contents = state;
	}
	
	@Override
	public <T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(toAdd.isEmpty()) return toAdd;
		
		contents.modify(toAdd.key(), stack -> stack.grownBy(toAdd.amount()));
		dirty = true;
		return toAdd.withAmount(0);
	}
	
	@Override
	public <T> AuraStack<T> withdraw(AuraStack<T> toWithdraw, Transaction tx) {
		tx.enlist(this);
		
		AuraStack<T> existing = contents.get(toWithdraw.key());
		
		int howMuch = Math.min(existing.amount(), toWithdraw.amount());
		contents.put(existing.shrunkBy(howMuch));
		
		if(howMuch != 0) dirty = true;
		
		return existing.withAmount(howMuch);
	}
	
	@Override
	public Collection<AuraStack<?>> contents() {
		return contents.asStackCollection();
	}
	
	@Override
	public int computeTotalVolume() {
		return contents.computeTotalVolume();
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
