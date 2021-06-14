package agency.highlysuspect.halogen.aura;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//Less a literal "stack" of anything, more "by analogy with ItemStack".
public record AuraStack(AuraType type, int amount) {
	public static Codec<AuraStack> CODEC = RecordCodecBuilder.create(i -> i.group(
		AuraType.CODEC.fieldOf("aura_type").forGetter(AuraStack::type),
		Codec.INT.fieldOf("amount").forGetter(AuraStack::amount)
	).apply(i, AuraStack::new));
	
	public static AuraStack empty(AuraType type) {
		return new AuraStack(type, 0);
	} 
	
	public AuraStack grow(int howMuch) {
		return new AuraStack(type, amount + howMuch);
	}
	
	public AuraStack shrink(int howMuch) {
		assert amount - howMuch >= 0;
		return new AuraStack(type, amount - howMuch);
	}
	
	public AuraStack withAmount(int newAmount) {
		assert newAmount >= 0;
		return new AuraStack(type, newAmount);
	}
	
	public boolean isEmpty() {
		return amount == 0;
	}
	
	public boolean sameType(AuraStack other) {
		return type.equals(other.type);
	}
	
	//todo are these useful
	public AuraStack add(AuraStack other) {
		assert sameType(other);
		return grow(other.amount);
	}
	
	public AuraStack take(AuraStack other) {
		assert sameType(other);
		return shrink(other.amount);
	}
}
