package agency.highlysuspect.halogen.mechanics.prism;

import agency.highlysuspect.halogen.HaloBlocks;
import agency.highlysuspect.halogen.HaloBlockEntityTypes;
import agency.highlysuspect.halogen.util.States;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
 
public class MoonlightPrismBlockEntity extends BlockEntity {
	public MoonlightPrismBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int radius, int downshift) {
		super(type, pos, state);
		this.radius = radius;
		this.downshift = downshift;
	}
	
	public static MoonlightPrismBlockEntity large(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.LARGE_MOONLIGHT_PRISM, pos, state, 13, 0);
	}
	
	public static MoonlightPrismBlockEntity small(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.SMALL_MOONLIGHT_PRISM, pos, state, 5, 2);
	}
	
	//Amethyst clusters inside this sphere of influence are eligible to be converted to moonlight clusters, if it's midnight.
	private final int radius;
	private final int downshift;
	
	//The amethyst cluster I'm currently working on moonlightifying, or `null` if i'm not doing that.
	private @Nullable BlockPos target;
	//How many ticks until that amethyst cluster will change.
	private int changeProgress;
	//How many ticks until I scan for a new target.
	private int scanCooldown;
	
	private static final float MIDNIGHT = (float) Math.PI;
	private static final float MIDNIGHT_DEVIATION = (float) Math.toRadians(10);
	
	public void tick(Level level, BlockPos pos, BlockState state) {
		if(!(level instanceof ServerLevel serverLevel)) return; //Should never happen; server-only ticker
		
		//If i can't see the sky, there's nothing to do
		if(!level.canSeeSky(pos.above())) {
			if(target != null) setChanged();
			target = null;
			return;
		}
		
		//If there is a target, tick down the processing time, or convert to a moonlight cluster if the time has expired.
		if(target != null) {
			if(!isFullyGrownAmethyst(level, target)) target = null;
			else {
				changeProgress--;
				setChanged();
				
				if(changeProgress <= 0) {
					convertToMoonlightCluster(level, target);
					
					//Now search for a new target.
					target = null;
					scanCooldown = Mth.nextInt(level.random, 5, 20);
				}
			}
		}
		
		//If there's currently no target, and it's midnight, scan for a new target
		if(target == null) {
			float skyAngle = level.getSunAngle(1f);
			if(skyAngle > MIDNIGHT - MIDNIGHT_DEVIATION && skyAngle < MIDNIGHT + MIDNIGHT_DEVIATION) {
				scanCooldown--;
				if(scanCooldown <= 0) {
					scanCooldown = 0;
					
					BlockPos sphereCenter = pos.below(downshift);
					BlockPos nextTarget = findTarget(serverLevel, sphereCenter, radius);
					if(nextTarget != null) {
						target = nextTarget;
						//3 seconds on a new moon, 1 second on a full moon. (Plus or minus half a second; why not.)
						changeProgress = 60 - Mth.floor(level.getMoonBrightness() * 40) + Mth.nextInt(level.random, -10, 10);
						setChanged();
					}
				}
			}
		}
	}
	
	private static void convertToMoonlightCluster(Level level, BlockPos amethystPos) {
		level.setBlockAndUpdate(amethystPos, States.copyOnto(
			level.getBlockState(amethystPos),
			HaloBlocks.MOONLIGHT_CLUSTER.defaultBlockState())
		);
	}
	
	private static @Nullable BlockPos findTarget(ServerLevel level, BlockPos centerPos, int radius) {
		ArrayList<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values())); //Will be repeatedly shuffled.
		Random random = level.getRandom();
		
		//Find nearby budding amethyst blocks.
		List<BlockPos> nearbyClusters = level.getPoiManager()
			.getInRange(type -> type.is(Blocks.BUDDING_AMETHYST.defaultBlockState()), centerPos, radius, PoiManager.Occupancy.ANY)
			.map(PoiRecord::getPos)
			.collect(Collectors.toList());
		
		//Toss them in a bag.
		Collections.shuffle(nearbyClusters, random);
		
		//Search around each one for fully grown amethyst clusters, with no directional preference.
		//Yeah, yeah, it doesn't check to see that the cluster is resting on *this* bud. Who cares.
		for(BlockPos bud : nearbyClusters) {
			Collections.shuffle(directions, random);
			for(Direction offset : directions) {
				BlockPos candidate = bud.relative(offset);
				if(isFullyGrownAmethyst(level, candidate)) return candidate;
			}
		}
		
		return null;
	}
	
	private static boolean isFullyGrownAmethyst(Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock() == Blocks.AMETHYST_CLUSTER;
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		if(nbt.contains("Target", Tag.TAG_COMPOUND)) {
			target = NbtUtils.readBlockPos(nbt.getCompound("Target"));
		} else {
			target = null;
		}
		
		changeProgress = nbt.getInt("Progress");
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		if(target != null) {
			nbt.put("Target", NbtUtils.writeBlockPos(target));
		}
		
		nbt.putInt("Progress", changeProgress);
	}
}
