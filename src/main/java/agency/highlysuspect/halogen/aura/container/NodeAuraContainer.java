package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.util.Dirtyable;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collection;

public final class NodeAuraContainer implements AuraContainer, Dirtyable {
	public NodeAuraContainer(BoundedHeterogeneousAuraContainer incoming, BoundedHeterogeneousAuraContainer main) {
		this.incoming = incoming;
		this.main = main;
		
		this.codec = RecordCodecBuilder.create(i -> i.group(
			incoming.sizedCodec().fieldOf("incoming").forGetter(NodeAuraContainer::incoming),
			main.sizedCodec().fieldOf("main").forGetter(NodeAuraContainer::main)
		).apply(i, NodeAuraContainer::new));
	}
	
	private final BoundedHeterogeneousAuraContainer incoming;
	private final BoundedHeterogeneousAuraContainer main;
	public final Codec<NodeAuraContainer> codec;
	
	@Override
	public <T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx) {
		return incoming.accept(toAdd, tx);
	}
	
	@Override
	public <T> AuraStack<T> withdraw(AuraStack<T> toWithdraw, Transaction tx) {
		return main.withdraw(toWithdraw, tx);
	}
	
	@Override
	public Collection<AuraStack<?>> contents() {
		//TODO: do I need to include `incoming`'s contents as well?
		// I think this is situational
		//return CollectionsUtil.pair(incoming.contents(), main.contents());
		return main.contents();
	}
	
	public void pourIncomingIntoMain() {
		try(Transaction tx = new Transaction()) {
			incoming.pourAllInto(main, tx);
			tx.commit();
		}
	}
	
	@Override
	public boolean isDirty() {
		return incoming.isDirty() || main.isDirty();
	}
	
	@Override
	public void clean() {
		incoming.clean();
		main.clean();
	}
	
	public BoundedHeterogeneousAuraContainer incoming() {
		return incoming;
	}
	
	public BoundedHeterogeneousAuraContainer main() {
		return main;
	}
}
