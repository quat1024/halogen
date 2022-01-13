package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

//Less a literal "stack" of anything, more "by analogy with ItemStack".
public final record AuraStack<T>(AuraKey<T> key, int amount) {
	public AuraStack {
		assert amount >= 0 : "Negative amount " + amount;
	}
	
	public static final Codec<AuraStack<?>> CODEC = RecordCodecBuilder.create(i -> i.group(
		AuraKey.CODEC.fieldOf("key").forGetter(AuraStack::key),
		Codec.INT.fieldOf("amount").forGetter(AuraStack::amount)
	).apply(i, AuraStack::new));
	
	//withers
	public <X> AuraStack<X> withKey(AuraKey<X> newKey) {
		return new AuraStack<>(newKey, amount);
	}
	
	public AuraStack<T> withAmount(int newAmount) {
		return new AuraStack<>(key, newAmount);
	}
	
	public AuraStack<T> grownBy(int howMuch) {
		return new AuraStack<>(key, amount + howMuch);
	}
	
	public AuraStack<T> grownBy(AuraStack<T> other) {
		assert canMerge(other);
		return grownBy(other.amount);
	}
	
	public AuraStack<T> shrunkBy(int howMuch) {
		assert amount - howMuch >= 0 : "Shrinking AuraStack by " + howMuch + " would make its size " + (amount - howMuch);
		return new AuraStack<>(key, amount - howMuch);
	}
	
	public AuraStack<T> shrunkBy(AuraStack<T> other) {
		assert canMerge(other);
		return shrunkBy(other.amount);
	}
	
	public AuraStack<T> reduceToAtMost(int howMuch) {
		if(amount > howMuch) return withAmount(howMuch);
		else return this;
	}
	
	//merging, emptiness
	public boolean isEmpty() {
		return amount == 0;
	}
	
	public boolean canMerge(AuraStack<?> other) {
		return key.equals(other.key);
	}
	
	//Only a valid operation when canMerge is true.
	public <X> AuraStack<X> castKey() {
		//noinspection unchecked
		return (AuraStack<X>) this;
	}
	
	//For convenience. Please use e.g. codec.listOf instead of calling this a bunch and writing to an nbt list yourself.
	public Tag toNbt() {
		return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, Init.LOG::error);
	}
	
	public static AuraStack<?> fromNbt(Tag elem) {
		return CODEC.parse(NbtOps.INSTANCE, elem).getOrThrow(false, Init.LOG::error);
	}
}
