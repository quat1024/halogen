package agency.highlysuspect.halogen.aura;

import com.mojang.serialization.Codec;

/**
 * An AuraKey is an AuraType, together with the data that that type stores.
 * If the AuraKeys of any two stacks are equal, the stacks are comparable with each other.
 */
public record AuraKey<T>(AuraType<T> type, T data) {
	public static final Codec<AuraKey<?>> CODEC = AuraType.REGISTRY.dispatch(AuraKey::type, AuraType::keyedDataCodec);
	
	public AuraStack<T> stackOf(int amount) {
		return new AuraStack<>(this, amount);
	}
	
	public AuraStack<T> emptyStackOf() {
		return new AuraStack<>(this, 0);
	}
}
