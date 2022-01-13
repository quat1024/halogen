package agency.highlysuspect.halogen.worldgen;

import agency.highlysuspect.halogen.block.HaloBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

public class MiniGeodeFeature extends Feature<NoneFeatureConfiguration> {
	//Todo allow for configuration of this feature instead of being lazy about it
	public MiniGeodeFeature() {
		super(NoneFeatureConfiguration.CODEC);
	}
	
	@SuppressWarnings("UnnecessaryContinue") // :D
	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		Random random = context.random();
		BlockPos origin = context.origin();
		WorldGenLevel level = context.level();
		
		double diameter = UniformFloat.of(7, 9).sample(random);
		double radius = diameter / 2;
		double sink = UniformFloat.of(0, 3).sample(random); //how far underground the center of the sphere is
		
		//Figure out the center of the sphere
		Vec3 centerDouble = new Vec3(
			origin.getX() + radius,
			context.chunkGenerator().getBaseHeight(origin.getX(), origin.getZ(), Heightmap.Types.OCEAN_FLOOR_WG, level) - sink,
			origin.getZ() + radius
		).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
		BlockPos centerBlockPos = new BlockPos(Math.round(centerDouble.x), Math.round(centerDouble.y), Math.round(centerDouble.z));
		
		//Plan to place the main geode sphere, keeping track of where the blocks are supposed to go
		List<BlockPos> airBlocks = new ArrayList<>();
		List<BlockPos> amethystBlocks = new ArrayList<>();
		List<BlockPos> basaltBlocks = new ArrayList<>();
		int radiusCeil = Mth.ceil(radius);
		for(BlockPos pos : BlockPos.withinManhattan(centerBlockPos, radiusCeil, radiusCeil, radiusCeil)) {
			if(!level.getBlockState(pos).canOcclude()) continue;
			
			double distance = Vec3.atCenterOf(pos).distanceTo(centerDouble);
			distance -= random.nextDouble() / 2; //breaks it up a bit idk
			
			if(distance > radius) continue;
			else if(distance < radius - 2) airBlocks.add(pos.immutable());
			else if(distance < radius - 1) amethystBlocks.add(pos.immutable());
			else basaltBlocks.add(pos.immutable());
		}
		
		//(This line here is why I delay placing the blocks... lol)
		//Ensure the geodes have some heft to them and aren't like, a floating moonlight-prism in the void
		if(amethystBlocks.size() < 7 || basaltBlocks.size() < 15) return false;
		
		//Actually place the dang thing
		for(BlockPos pos : airBlocks) level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
		for(BlockPos pos : amethystBlocks) level.setBlock(pos, Blocks.AMETHYST_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
		for(BlockPos pos : basaltBlocks) level.setBlock(pos, Blocks.SMOOTH_BASALT.defaultBlockState(), Block.UPDATE_CLIENTS);
		
		//Add some amethyst buds too
		int budCount = Math.min(5, amethystBlocks.size() / 6);
		
		for(int i = 0; i < budCount; i++) {
			if(amethystBlocks.isEmpty()) break;
			
			BlockPos pos = amethystBlocks.remove(random.nextInt(amethystBlocks.size()));
			level.setBlock(pos, Blocks.BUDDING_AMETHYST.defaultBlockState(), Block.UPDATE_CLIENTS);
			
			for(Direction d : Direction.values()) {
				BlockPos off = pos.relative(d);
				if(random.nextBoolean() && BuddingAmethystBlock.canClusterGrowAtState(level.getBlockState(off))) {
					level.setBlock(pos.relative(d), Blocks.MEDIUM_AMETHYST_BUD.defaultBlockState().setValue(BlockStateProperties.FACING, d), Block.UPDATE_CLIENTS);
				}
			}
		}
		
		//Finally add the moonlight prism itself
		level.setBlock(centerBlockPos.above((int) (radius / 3)), HaloBlocks.SMALL_MOONLIGHT_PRISM.defaultBlockState(), Block.UPDATE_CLIENTS);
		return true;
	}
}
