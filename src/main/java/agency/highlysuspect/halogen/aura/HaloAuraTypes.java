package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public class HaloAuraTypes {
	private static final Codec<Unit> UNIT_CODEC = Codec.unit(Unit.INSTANCE);
	
	public static final AuraType<Unit> WHITE = new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE);
	public static final AuraType<Unit> RED = new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE);
	public static final AuraType<Unit> BLUE = new AuraType<>(UNIT_CODEC, () -> Unit.INSTANCE);
	
	private static <T> AuraType<T> reg(String id, AuraType<T> type) {
		return Registry.register(AuraType.REGISTRY, Init.id(id), type);
	}
	
	public static void onInitialize() {
		reg("white", WHITE);
		reg("red", RED);
		reg("blue", BLUE);
	}
}
