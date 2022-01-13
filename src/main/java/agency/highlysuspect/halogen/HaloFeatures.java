package agency.highlysuspect.halogen;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.mechanics.prism.MiniGeodeFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class HaloFeatures {
	public static void onInitialize() {
		var miniGeode = new MiniGeodeFeature();
		Registry.register(Registry.FEATURE, Init.id("mini_geode_feature"), miniGeode);
		
		var placed = miniGeode
			.configured(NoneFeatureConfiguration.INSTANCE)
			.placed(RarityFilter.onAverageOnceEvery(2), PlacementUtils.HEIGHTMAP_TOP_SOLID);
		
		Registry.register(BuiltinRegistries.PLACED_FEATURE, Init.id("mini_geode"), placed);
		
		BiomeModifications.addFeature(select -> select.hasBuiltInFeature(CaveFeatures.AMETHYST_GEODE),
			GenerationStep.Decoration.SURFACE_STRUCTURES,
			BuiltinRegistries.PLACED_FEATURE.getResourceKey(placed).get()
		);
	}
}
