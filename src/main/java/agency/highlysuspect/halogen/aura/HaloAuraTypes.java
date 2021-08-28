package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HaloAuraTypes {
	private static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);
	
	public static final AuraType<Unit> WHITE = reg(Init.id("white"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	public static final AuraType<Unit> RED = reg(Init.id("red"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	public static final AuraType<Unit> BLUE = reg(Init.id("blue"), new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE));
	
	private static <T> AuraType<T> reg(Identifier id, AuraType<T> type) {
		return Registry.register(AuraType.REGISTRY, id, type);
	}
	
	public static void onInitialize() {
		//Classload
	}
}
