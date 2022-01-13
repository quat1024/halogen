package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import net.minecraft.core.Registry;

public class HaloAuraTypes {
	public static final AuraType WHITE = new AuraType();
	public static final AuraType RED = new AuraType();
	public static final AuraType BLUE = new AuraType();
	
	private static AuraType reg(String id, AuraType type) {
		return Registry.register(AuraType.REGISTRY, Init.id(id), type);
	}
	
	public static void onInitialize() {
		reg("white", WHITE);
		reg("red", RED);
		reg("blue", BLUE);
	}
}
