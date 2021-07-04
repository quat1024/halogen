package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.Objects;
import java.util.function.Supplier;

public final class AuraType<T> {
	public static final RegistryKey<Registry<AuraType<?>>> AURA_TYPE_KEY = RegistryKey.ofRegistry(Init.id("aura_type"));
	public static final Registry<AuraType<?>> REGISTRY = new SimpleRegistry<>(AURA_TYPE_KEY, Lifecycle.stable()); //"stable"
	
	private static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);
	
	public static final AuraType<Unit> WHITE = reg(Init.id("white"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	public static final AuraType<Unit> RED = reg(Init.id("red"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	public static final AuraType<Unit> BLUE = reg(Init.id("blue"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	
	public AuraType(Codec<T> dataCodec, Supplier<T> defaultData) {
		this.dataCodec = dataCodec;
		this.defaultData = defaultData;
		this.keyedDataCodec = dataCodec.fieldOf("data").xmap(d -> new AuraKey<>(this, d), AuraKey::data).codec();
	}
	
	public final Codec<T> dataCodec;
	public final Supplier<T> defaultData;
	
	public final Codec<AuraKey<T>> keyedDataCodec;
	
	public static <T> AuraType<T> reg(Identifier id, AuraType<T> type) {
		return Registry.register(REGISTRY, id, type);
	}
	
	public AuraKey<T> keyOf() {
		return keyOf(defaultData.get());
	}
	
	public AuraKey<T> keyOf(T data) {
		return new AuraKey<>(this, data);
	}
	
	public AuraStack<T> stackOf(int amount) {
		return keyOf().stackOf(amount);
	}
	
	public AuraStack<T> stackOf(T data, int amount) {
		return keyOf(data).stackOf(amount);
	}
	
	public AuraStack<T> emptyStackOf() {
		return keyOf().emptyStackOf();
	}
	
	public Codec<AuraKey<T>> keyedDataCodec() {
		return keyedDataCodec;
	}
	
	@Override
	public String toString() {
		return "AuraType[" + REGISTRY.getId(this) + "]";
	}
}
