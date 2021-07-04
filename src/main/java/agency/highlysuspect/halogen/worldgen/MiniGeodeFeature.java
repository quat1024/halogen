package agency.highlysuspect.halogen.worldgen;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.block.HaloBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniGeodeFeature extends Feature<DefaultFeatureConfig> {
	//Todo allow for configuration of this feature instead of being lazy about it
	public MiniGeodeFeature() {
		super(DefaultFeatureConfig.CODEC);
	}
	
	@SuppressWarnings("UnnecessaryContinue") // :D
	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Random random = context.getRandom();
		BlockPos origin = context.getOrigin();
		StructureWorldAccess world = context.getWorld();
		
		double diameter = UniformFloatProvider.create(7, 9).get(random);
		double radius = diameter / 2;
		double sink = UniformFloatProvider.create(0, 3).get(random); //how far underground the center of the sphere is
		
		//Figure out the center of the sphere
		Vec3d centerDouble = new Vec3d(
			origin.getX() + radius,
			context.getGenerator().getHeight(origin.getX(), origin.getZ(), Heightmap.Type.OCEAN_FLOOR_WG, world) - sink,
			origin.getZ() + radius
		).add(random.nextDouble(), random.nextDouble(), random.nextDouble());
		BlockPos centerBlockPos = new BlockPos(Math.round(centerDouble.x), Math.round(centerDouble.y), Math.round(centerDouble.z));
		
		//Plan to place the main geode sphere, keeping track of where the blocks are supposed to go
		List<BlockPos> airBlocks = new ArrayList<>();
		List<BlockPos> amethystBlocks = new ArrayList<>();
		List<BlockPos> basaltBlocks = new ArrayList<>();
		int radiusCeil = MathHelper.ceil(radius);
		for(BlockPos pos : BlockPos.iterateOutwards(centerBlockPos, radiusCeil, radiusCeil, radiusCeil)) {
			if(!world.getBlockState(pos).isOpaque()) continue;
			
			double distance = Vec3d.ofCenter(pos).distanceTo(centerDouble);
			distance -= random.nextDouble() / 2; //breaks it up a bit idk
			
			if(distance > radius) continue;
			else if(distance < radius - 2) airBlocks.add(pos.toImmutable());
			else if(distance < radius - 1) amethystBlocks.add(pos.toImmutable());
			else basaltBlocks.add(pos.toImmutable());
		}
		
		//(This line here is why I delay placing the blocks... lol)
		//Ensure the geodes have some heft to them and aren't like, a floating moonlight-prism in the void
		if(amethystBlocks.size() < 7 || basaltBlocks.size() < 15) return false;
		
		//Actually place the dang thing
		for(BlockPos pos : airBlocks) world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
		for(BlockPos pos : amethystBlocks) world.setBlockState(pos, Blocks.AMETHYST_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
		for(BlockPos pos : basaltBlocks) world.setBlockState(pos, Blocks.SMOOTH_BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
		
		//Add some amethyst buds too
		int budCount = Math.min(5, amethystBlocks.size() / 6);
		
		for(int i = 0; i < budCount; i++) {
			if(amethystBlocks.isEmpty()) break;
			
			BlockPos pos = amethystBlocks.remove(random.nextInt(amethystBlocks.size()));
			world.setBlockState(pos, Blocks.BUDDING_AMETHYST.getDefaultState(), Block.NOTIFY_LISTENERS);
			
			for(Direction d : Direction.values()) {
				BlockPos off = pos.offset(d);
				if(random.nextBoolean() && BuddingAmethystBlock.canGrowIn(world.getBlockState(off))) {
					world.setBlockState(pos.offset(d), Blocks.MEDIUM_AMETHYST_BUD.getDefaultState().with(Properties.FACING, d), Block.NOTIFY_LISTENERS);
				}
			}
		}
		
		//Finally add the moonlight prism itself
		world.setBlockState(centerBlockPos.up((int) (radius / 3)), HaloBlocks.SMALL_MOONLIGHT_PRISM.getDefaultState(), Block.NOTIFY_LISTENERS);
		return true;
	}
}
