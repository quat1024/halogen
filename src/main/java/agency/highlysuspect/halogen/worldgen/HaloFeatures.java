package agency.highlysuspect.halogen.worldgen;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.registry.Registry;

public class HaloFeatures {
	public static void onInitialize() {
		
	}
	
	private static <T> void bindCallback(Registry<T> registry, RegistryEntryAddedCallback<T> callback) {
		registry.forEach(value -> callback.onEntryAdded(registry.getRawId(value), registry.getId(value), value));
		RegistryEntryAddedCallback.event(registry).register(callback);
	}
}
