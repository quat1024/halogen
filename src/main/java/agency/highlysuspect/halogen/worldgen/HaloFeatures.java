package agency.highlysuspect.halogen.worldgen;

import agency.highlysuspect.halogen.Init;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class HaloFeatures {
	public static void onInitialize() {
		var miniGeode = new MiniGeodeFeature();
		Registry.register(Registry.FEATURE, Init.id("mini_geode_feature"), miniGeode);
		
		var placed = miniGeode
			.configure(DefaultFeatureConfig.INSTANCE)
			.withPlacement(RarityFilterPlacementModifier.of(2), PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP);
		
		Registry.register(BuiltinRegistries.PLACED_FEATURE, Init.id("mini_geode"), placed);
		
		BiomeModifications.addFeature(select -> select.hasBuiltInFeature(UndergroundConfiguredFeatures.AMETHYST_GEODE),
			GenerationStep.Feature.SURFACE_STRUCTURES,
			BuiltinRegistries.PLACED_FEATURE.getKey(placed).get()
		);
	}
}
