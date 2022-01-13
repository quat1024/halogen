package agency.highlysuspect.halogen.mixin;

import agency.highlysuspect.halogen.block.HaloBlocks;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

@Mixin(GeodeFeature.class)
public class GeodeFeatureMixin {
	@Unique
	ThreadLocal<Object2DoubleOpenHashMap<BlockPos>> highScoringBlocks = ThreadLocal.withInitial(Object2DoubleOpenHashMap::new);
	
	//I am so fucking sorry
	@Inject(
		method = "place", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/level/levelgen/GeodeBlockSettings;fillingProvider:Lnet/minecraft/world/level/levelgen/feature/stateproviders/BlockStateProvider;"
	), locals = LocalCapture.CAPTURE_FAILHARD)
	private void placeInnerLoop(FeaturePlaceContext<?> context, CallbackInfoReturnable<Boolean> cir,
	                            GeodeConfiguration geodeFeatureConfig,
	                            Random random,
	                            BlockPos pos,
	                            WorldGenLevel worldAccess,
	                            int i,
	                            int j,
	                            List<?> list,
	                            int k,
	                            WorldgenRandom chunkRandom,
	                            NormalNoise doublePerlinNoiseSampler,
	                            List<?> list2,
	                            double d,
	                            GeodeLayerSettings geodeLayerThicknessConfig,
	                            GeodeBlockSettings geodeLayerConfig,
	                            GeodeCrackSettings geodeCrackConfig,
	                            double e,
	                            double f,
	                            double g,
	                            double h,
	                            double l,
	                            boolean bl,
	                            List<?> list3,
	                            Predicate<?> predicate,
	                            Iterator<?> var32,
	                            BlockPos blockPos3,
	                            double u) {
		highScoringBlocks.get().put(blockPos3.immutable(), u);
	}
	
	@Inject(method = "place", at = @At("RETURN"))
	private void placeReturn(FeaturePlaceContext<GeodeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
		Object2DoubleOpenHashMap<BlockPos> map = highScoringBlocks.get();
		
		//The largest number in this collection is the farthest from any metaball points i.e. it's arooooound where the middle is.
		double score = -10000;
		BlockPos winner = null;
		
		var iter = map.object2DoubleEntrySet().fastIterator();
		while(iter.hasNext()) {
			var entry = iter.next();
			if(entry.getDoubleValue() > score) {
				score = entry.getDoubleValue();
				winner = entry.getKey();
			}
		}
		
		if(winner != null) {
			context.level().setBlock(winner, HaloBlocks.LARGE_MOONLIGHT_PRISM.defaultBlockState(), Block.UPDATE_CLIENTS);
		}
		
		highScoringBlocks.remove();
	}
}
