package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Participant;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;

//TOdo: Not very useful in practice, just a test of the transactionable aura container system
public class UnboundedAuraContainer implements Participant<AuraStack>, AuraContainer, SerializableAuraContainer {
	public UnboundedAuraContainer(AuraStack stack) {
		this.stack = stack;
	}
	
	private AuraStack stack;
	private boolean dirty = false;
	
	@Override
	public AuraStack backupState() {
		return stack.copy();
	}
	
	@Override
	public void restoreState(AuraStack state) {
		stack = state;
	}
	
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toAdd)) {
			stack.mutGrow(toAdd.amount());
			dirty = true;
			return toAdd.withAmount(0);
		} else return toAdd;
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Transaction tx) {
		tx.enlist(this);
		
		if(stack.canMerge(toWithdraw)) {
			int howMuch = Math.min(stack.amount(), toWithdraw.amount());
			stack.mutShrink(howMuch);
			if(howMuch != 0) dirty = true;
			return stack.withAmount(howMuch);
		} else return toWithdraw.withAmount(0);
	}
	
	@Override
	public NbtCompound writeNbt() {
		NbtCompound yeah = new NbtCompound();
		yeah.put("aura", stack.toTag());
		return yeah;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		stack = AuraStack.fromTag(nbt.getCompound("aura"));
		dirty = true;
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
