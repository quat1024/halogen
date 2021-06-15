package agency.highlysuspect.halogen.aura;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Objects;

//Less a literal "stack" of anything, more "by analogy with ItemStack".
public final class AuraStack {
	private final AuraType type;
	private int amount;
	
	public AuraStack(AuraType type, int amount) {
		this.type = type;
		this.amount = amount;
	}
	
	public AuraStack copy() {
		return new AuraStack(type, amount);
	}
	
	public AuraStack mutGrow(int howMuch) {
		amount += howMuch;
		return this;
	}
	
	public AuraStack mutShrink(int howMuch) {
		amount -= howMuch;
		assert amount >= 0;
		return this;
	}
	
	public AuraStack withType(AuraType newType) {
		return new AuraStack(newType, amount);
	}
	
	public AuraStack withAmount(int newAmount) {
		assert newAmount >= 0;
		return new AuraStack(type, newAmount);
	}
	
	public boolean hasAtLeast(int howMuch) {
		return amount >= howMuch;
	}
	
	public boolean isEmpty() {
		return amount == 0;
	}
	
	public boolean canMerge(AuraStack other) {
		return this.isEmpty() || other.isEmpty() || type.equals(other.type);
	}
	
	public AuraStack add(AuraStack other) {
		assert canMerge(other);
		
		if(this.isEmpty()) return other;
		if(other.isEmpty()) return this;
		return mutGrow(other.amount);
	}
	
	public AuraType type() {
		return type;
	}
	
	public int amount() {
		return amount;
	}
	
	public NbtCompound toTag() {
		NbtCompound nbt = toTypelessTag();
		nbt.putString("type", AuraType.REGISTRY.getId(type).toString());
		return nbt;
	}
	
	public NbtCompound toTypelessTag() {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("amount", amount);
		return nbt;
	}
	
	public static AuraStack fromTag(NbtCompound nbt) {
		AuraType type = AuraType.REGISTRY.get(Identifier.tryParse(nbt.getString("type")));
		return fromTypelessTag(type, nbt);
	}
	
	public static AuraStack fromTypelessTag(AuraType type, NbtCompound nbt) {
		return new AuraStack(type, nbt.getInt("amount"));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		var that = (AuraStack) obj;
		return Objects.equals(this.type, that.type) &&
			this.amount == that.amount;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, amount);
	}
	
	@Override
	public String toString() {
		return "AuraStack[" +
			"type=" + type + ", " +
			"amount=" + amount + ']';
	}
}
