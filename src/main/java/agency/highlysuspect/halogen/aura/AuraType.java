package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class AuraType {
	public static final ResourceKey<Registry<AuraType>> AURA_TYPE_KEY = ResourceKey.createRegistryKey(Init.id("aura_type"));
	public static final Registry<AuraType> REGISTRY = new MappedRegistry<>(AURA_TYPE_KEY, Lifecycle.stable());
	
	public AuraType() {
		//maybe put some data in here, idk
	}
	
	@Override
	public String toString() {
		return "AuraType[" + REGISTRY.getKey(this) + "]";
	}
}
