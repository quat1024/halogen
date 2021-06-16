package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;

public class NodeAuraContainer implements SerializableAuraContainer {
	public NodeAuraContainer(SerializableAuraContainer incoming, SerializableAuraContainer main) {
		this.incoming = incoming;
		this.main = main;
	}
	
	SerializableAuraContainer incoming;
	SerializableAuraContainer main;
	
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		return incoming.accept(toAdd, tx);
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Transaction tx) {
		return main.withdraw(toWithdraw, tx);
	}
	
	public void pourIncomingIntoMain() {
		//TODO: I need some kind of forEach
		try(Transaction tx = new Transaction()) {
			//Take all the white aura out of the incoming tank
			AuraStack withdrawn = incoming.withdraw(new AuraStack(AuraType.WHITE, Integer.MAX_VALUE), tx);
			//Dump as much as will fit into the main tank
			AuraStack leftover = main.accept(withdrawn, tx);
			//If it didn't all fit, keep the remainder in the incoming tank
			leftover = incoming.accept(leftover, tx);
			if(leftover.isEmpty()) {
				tx.commit();
			}
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
