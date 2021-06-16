package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

//TODO fill this out more
public class AuraType {
	public static final RegistryKey<Registry<AuraType>> AURA_TYPE_KEY = RegistryKey.ofRegistry(Init.id("aura_type"));
	//Todo maybe add this registry to the metaregistry. All a mess of private stuff though.
	public static final Registry<AuraType> REGISTRY = new SimpleRegistry<>(AURA_TYPE_KEY, Lifecycle.stable()); //"stable"
	
	public static final AuraType WHITE = reg(Init.id("white"), new AuraType());
	public static final AuraType RED = reg(Init.id("red"), new AuraType());
	public static final AuraType BLUE = reg(Init.id("blue"), new AuraType());
	
	public static AuraType reg(Identifier id, AuraType type) {
		return Registry.register(REGISTRY, id, type);
	}
	
	public AuraStack makeStack(int amount) {
		return new AuraStack(this, amount);
	}
	
	public AuraStack makeEmptyStack() {
		return makeStack(0);
	}
}
