package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class AuraType<T> {
	public static final ResourceKey<Registry<AuraType<?>>> AURA_TYPE_KEY = ResourceKey.createRegistryKey(Init.id("aura_type"));
	public static final Registry<AuraType<?>> REGISTRY = new MappedRegistry<>(AURA_TYPE_KEY, Lifecycle.stable()); //"stable"
	
	public AuraType(Codec<T> dataCodec, Supplier<T> defaultData) {
		this.dataCodec = dataCodec;
		this.defaultData = defaultData;
		
		//this is sorta tied in a knot with AuraKey
		//(blame mojang's Feature/ConfiguredFeature stuff, it's where i got this concept from)
		//basically this is a codec where
		// given myself (non-static codec) and some data, I can produce an aura key,
		// given an aura key, I can extract the data.
		this.dataFieldCodec = dataCodec.fieldOf("data").xmap(data -> new AuraKey<>(this, data), AuraKey::data).codec();
		//importantly (i think), it does this without "completing" the object, i.e. it's not a RecordCodecBuilder.
		//this means the AuraKey's registry-dispatch codec can write the aura type to the same location, and they end up together.
		//...
		//why does aurakey dispatch over the type of *this* class, and why does this class basically contain aurakey's actual codec? idk lol
	}
	
	public final Codec<T> dataCodec;
	public final Supplier<T> defaultData;
	
	//pkg-private; read in AuraKey's codec, and nowhere else
	//this little mfer is preventing the class from being a record lol
	final Codec<AuraKey<T>> dataFieldCodec;
	
	//convenience methods
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
	
	public AuraStack<T> emptyStackOf(T data) {
		return keyOf(data).emptyStackOf();
	}
	
	@Override
	public String toString() {
		return "AuraType[" + REGISTRY.getKey(this) + "]";
	}
}
