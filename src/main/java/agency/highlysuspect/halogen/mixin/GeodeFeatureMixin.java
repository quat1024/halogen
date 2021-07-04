package agency.highlysuspect.halogen.mixin;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.block.HaloBlocks;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.FeatureContext;
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

@Mixin(GeodeFeature.class)
public class GeodeFeatureMixin {
	@Unique
	ThreadLocal<Object2DoubleOpenHashMap<BlockPos>> highScoringBlocks = ThreadLocal.withInitial(Object2DoubleOpenHashMap::new);
	
	//I am so fucking sorry
	@Inject(
		method = "generate", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/gen/feature/GeodeLayerConfig;fillingProvider:Lnet/minecraft/world/gen/stateprovider/BlockStateProvider;"
	),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void generateInnerLoop(FeatureContext<?> context, CallbackInfoReturnable<Boolean> cir,
	                               GeodeFeatureConfig geodeFeatureConfig,
	                               Random random,
	                               BlockPos pos,
	                               StructureWorldAccess worldAccess,
	                               int i,
	                               int j,
	                               List<?> list,
	                               int k,
	                               ChunkRandom chunkRandom,
	                               DoublePerlinNoiseSampler doublePerlinNoiseSampler,
	                               List<?> list2,
	                               double d,
	                               GeodeLayerThicknessConfig geodeLayerThicknessConfig,
	                               GeodeLayerConfig geodeLayerConfig,
	                               GeodeCrackConfig geodeCrackConfig,
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
		highScoringBlocks.get().put(blockPos3.toImmutable(), u);
	}
	
	@Inject(method = "generate", at = @At("RETURN"))
	private void generateReturn(FeatureContext<GeodeFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
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
			context.getWorld().setBlockState(winner, HaloBlocks.LARGE_MOONLIGHT_PRISM.getDefaultState(), Block.NOTIFY_LISTENERS);
		}
		
		highScoringBlocks.remove();
	}
}
