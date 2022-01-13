package agency.highlysuspect.halogen.aura;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

/**
 * An AuraKey is an AuraType, together with the data that that type stores.
 * If the AuraKeys of any two stacks are equal, the stacks are comparable with each other.
 */
public record AuraKey<T>(@NotNull AuraType<T> type, @NotNull T data) {
	public static final Codec<AuraKey<?>> CODEC = AuraType.REGISTRY.byNameCodec().dispatch(AuraKey::type, t -> t.dataFieldCodec);
	
	//convenience methods
	public AuraStack<T> stackOf(int amount) {
		return new AuraStack<>(this, amount);
	}
	
	public AuraStack<T> emptyStackOf() {
		return new AuraStack<>(this, 0);
	}
}
