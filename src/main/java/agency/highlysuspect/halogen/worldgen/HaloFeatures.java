package agency.highlysuspect.halogen.worldgen;

import agency.highlysuspect.halogen.Init;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class HaloFeatures {
	public static void onInitialize() {
		var miniGeode = new MiniGeodeFeature();
		Registry.register(Registry.FEATURE, Init.id("mini_geode_feature"), miniGeode);
		
		var configured = miniGeode
			.configure(DefaultFeatureConfig.INSTANCE)
			.decorate(Decorator.HEIGHTMAP.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG)).spreadHorizontally())
			.applyChance(2); //WAYYYY too common yes i know
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Init.id("mini_geode_conf"), configured);
		
		BiomeModifications.addFeature(select -> select.hasBuiltInFeature(ConfiguredFeatures.AMETHYST_GEODE),
			GenerationStep.Feature.SURFACE_STRUCTURES,
			BuiltinRegistries.CONFIGURED_FEATURE.getKey(configured).get()
		);
	}
}
