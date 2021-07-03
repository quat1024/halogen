package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;

import java.util.Collection;

public record NodeAuraContainer(SerializableAuraContainer incoming, SerializableAuraContainer main) implements SerializableAuraContainer {
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		return incoming.accept(toAdd, tx);
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Transaction tx) {
		return main.withdraw(toWithdraw, tx);
	}
	
	@Override
	public Collection<AuraStack> contents() {
		//TODO: do I need to include `incoming`'s contents as well?
		// I think this is situational
		//return CollectionsUtil.pair(incoming.contents(), main.contents());
		return main.contents();
	}
	
	public void pourIncomingIntoMain() {
		try(Transaction tx = new Transaction()) {
			incoming.pourInto(main, tx);
			tx.commit();
		}
	}
	
	@Override
	public NbtCompound writeNbt() {
		NbtCompound yeah = new NbtCompound();
		yeah.put("incoming", incoming.writeNbt());
		yeah.put("main", main.writeNbt());
		return yeah;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		incoming.readNbt(nbt.getCompound("incoming"));
		main.readNbt(nbt.getCompound("main"));
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
}
