package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Participant;
import agency.highlysuspect.halogen.util.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnboundedHeterogeneousAuraContainer implements Participant<List<AuraStack>>, SerializableAuraContainer {
	public UnboundedHeterogeneousAuraContainer() {
		this.contents = new ArrayList<>();
	}
	
	public UnboundedHeterogeneousAuraContainer(List<AuraStack> contents) {
		//defensive copy, smh my horns
		this.contents = new ArrayList<>(contents);
		filterEmpties();
	}
	
	protected List<AuraStack> contents;
	protected boolean dirty = false;
	
	protected List<AuraStack> deepCopyContents() {
		List<AuraStack> list = new ArrayList<>();
		for(AuraStack stack : contents) {
			list.add(stack.copy());
		}
		return list;
	}
	
	protected void filterEmpties() {
		contents.removeIf(AuraStack::isEmpty);
	}
	
	//TODO revisit this when i add aurastacks with NBT
	protected AuraStack findOrCreateOfType(AuraStack ofThisType) {
		for(AuraStack stack : contents) {
			if(stack.type() == ofThisType.type()) {
				return stack;
			}
		}
		AuraStack newStack = ofThisType.withAmount(0);
		contents.add(newStack);
		return newStack;
	}
	
	/////
	
	@Override
	public List<AuraStack> backupState() {
		return deepCopyContents();
	}
	
	@Override
	public void restoreState(List<AuraStack> state) {
		contents = state;
	}
	
	@Override
	public AuraStack accept(AuraStack toAdd, Transaction tx) {
		tx.enlist(this);
		
		if(toAdd.isEmpty()) return toAdd;
		
		findOrCreateOfType(toAdd).mutGrow(toAdd.amount());
		dirty = true;
		return toAdd.withAmount(0);
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Transaction tx) {
		tx.enlist(this);
		
		AuraStack existing = findOrCreateOfType(toWithdraw);
		int howMuch = Math.min(existing.amount(), toWithdraw.amount());
		existing.mutShrink(howMuch);
		
		if(howMuch != 0) {
			filterEmpties();
			dirty = true;
		}
		
		return existing.withAmount(howMuch);
	}
	
	@Override
	public Collection<AuraStack> contents() {
		return contents;
	}
	
	@Override
	public NbtCompound writeNbt() {
		NbtList list = new NbtList();
		contents.forEach(stack -> {
			//Should always get filtered out via filterEmpties.
			assert !stack.isEmpty();
			list.add(stack.toTag());
		});
		
		NbtCompound nbt = new NbtCompound();
		nbt.put("contents", list);
		return nbt;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		contents.clear();
		
		NbtList list = nbt.getList("contents", 10);
		for(int i = 0; i < list.size(); i++) {
			contents.add(AuraStack.fromTag(list.getCompound(i)));
		}
		
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
