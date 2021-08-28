package agency.highlysuspect.halogen.block.entity;

import agency.highlysuspect.halogen.block.HaloBlocks;
import agency.highlysuspect.halogen.util.BlockPosIteration;
import agency.highlysuspect.halogen.util.States;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
 
public class MoonlightPrismBlockEntity extends BlockEntity {
	public MoonlightPrismBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int radius, int downshift) {
		super(type, pos, state);
		this.radius = radius;
		this.centerPos = pos.down(downshift);
		
		List<BlockPos> sphereOffsets = BlockPosIteration.sphereAbsoluteOffsetsCached(radius);
		neighborhood = new BlockPos[sphereOffsets.size()];
		for(int i = 0; i < neighborhood.length; i++) {
			//we do a little allocating
			neighborhood[i] = sphereOffsets.get(i).add(centerPos);
		}
		
		//Preferably about 1/20th of the entire neighborhood unless that'd make way too many buckets
		this.bucketCount = MathHelper.clamp(neighborhood.length / 20, 1, 100);
	}
	
	public static MoonlightPrismBlockEntity large(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.LARGE_MOONLIGHT_PRISM, pos, state, 13, 0);
	}
	
	public static MoonlightPrismBlockEntity small(BlockPos pos, BlockState state) {
		return new MoonlightPrismBlockEntity(HaloBlockEntityTypes.SMALL_MOONLIGHT_PRISM, pos, state, 5, 2);
	}
	
	//The radius of the sphere of influence surrounding this moonlight prism.
	//Amethyst shards inside this region are eligible to be turned into moonlight shards.
	public final int radius;
	//Where that sphere of influence is centered.
	public final BlockPos centerPos;
	
	//A precomputed list of all BlockPos-es, in a sphere of radius "radius" surrounding me.
	private final BlockPos[] neighborhood;
	//If this is 20 then 1/20th of the neighborhood is checked for amethyst shards every tick.
	//This is because large prisms have a *really* big neighborhood.
	//Damn it's almost like the volume of a sphere grows with the cube of its radius or something! Wild
	//I don't wanna check like 15k blocks in one tick
	private final int bucketCount;
	
	//The amethyst shard I'm currently working on moonlightifying, or `null` if i'm not doing that.
	public @Nullable BlockPos target;
	//How many ticks until that amethyst shard has changed.
	public int changeProgress;
	//How many ticks until I scan for a new target.
	public int scanCooldown;
	
	public void tick(World world, BlockPos pos, BlockState state) {
		//If i can't see the sky, there's nothing to do
		if(!world.isSkyVisible(pos.up())) {
			if(target != null) markDirty();
			target = null;
			return;
		}
		
		if(target != null) {
			if(!isFullyGrownAmethyst(world, target)) target = null; //Changed away from an amethyst block; find a new target
			else {
				//Tick down the change progress, and if it's zero, perform the change
				changeProgress--;
				markDirty();
				
				if(changeProgress <= 0) {
					world.setBlockState(target, States.copyOnto(
						world.getBlockState(target),
						HaloBlocks.MOONLIGHT_CLUSTER.getDefaultState())
					);
					
					//Now search for a new target.
					target = null;
					scanCooldown = MathHelper.nextInt(world.random, 5, 20);
				}
			}
		}
		
		if(target == null) {
			final float MIDNIGHT = (float) Math.PI; //getSkyAngleRadians() at exactly midnight
			final float MIDNIGHT_DEVIATION = (float) Math.toRadians(10); //Max deviation from the midnight point, controls the duration of the effect.
			//It's hard to express this number in terms of real-time seconds, i just messed with it until it felt okay.
			//Experimentally, this is ~a little under 1 minute
			
			//If there's currently no target, and it's midnight, scan for a new target
			float skyAngle = world.getSkyAngleRadians(1f);
			if(skyAngle > MIDNIGHT - MIDNIGHT_DEVIATION && skyAngle < MIDNIGHT + MIDNIGHT_DEVIATION) {
				scanCooldown--;
				if(scanCooldown <= 0) {
					scanCooldown = 0;
					
					for(int i = (int) (world.getTime() % bucketCount); i < neighborhood.length; i += bucketCount) {
						BlockPos check = neighborhood[i];
						
//						//hmmmmm
//						for(ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
//							PacketByteBuf b = PacketByteBufs.create();
//							b.writeBlockPos(check);
//							b.writeInt(0xABCDEF87);
//							b.writeString("");
//							b.writeInt(300);
//							player.networkHandler.sendPacket(new CustomPayloadS2CPacket(CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER, b));
//						}
						
						if(isFullyGrownAmethyst(world, check)) {
							target = check;
							//The amount of time I will spend working on this amethyst shard.
							//3 seconds (+/- .5) on a new moon.
							//1 second  (+/- .5) on a full moon.
							changeProgress = 60 - MathHelper.floor(world.getMoonSize() * 40) + MathHelper.nextInt(world.random, -10, 10);
							markDirty();
							break;
						}
					}
				}
			}
		}
	}
	
	private static boolean isFullyGrownAmethyst(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() == Blocks.AMETHYST_CLUSTER;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		
		if(nbt.contains("Target", NbtElement.COMPOUND_TYPE)) {
			target = NbtHelper.toBlockPos(nbt.getCompound("Target"));
		} else {
			target = null;
		}
		
		changeProgress = nbt.getInt("Progress");
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		if(target != null) {
			nbt.put("Target", NbtHelper.fromBlockPos(target));
		}
		
		nbt.putInt("Progress", changeProgress);
		
		return super.writeNbt(nbt);
	}
}