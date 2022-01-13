package agency.highlysuspect.halogen.util;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class BlockPosIteration {
	public static AbstractInt2ObjectMap<List<BlockPos>> sphereAbsoluteCache = new Int2ObjectOpenHashMap<>();
	public static AbstractInt2ObjectMap<List<BlockPos>> sphereRelativeCache = new Int2ObjectOpenHashMap<>();
	
	//Iterates outwards in a sphere of radius "radius" from the starting position.
	//Hands you each BlockPos in sequence. Return "true" to break, whole function returns "true" if you broke in this manner.
	public static boolean sphere(BlockPos start, int radius, Predicate<? super BlockPos.MutableBlockPos> shouldBreak) {
		BlockPos.MutableBlockPos cursor = start.mutable();
		for(BlockPos d : sphereRelativeOffsetsCached(radius)) {
			cursor.move(d);
			if(shouldBreak.test(cursor)) return true;
		}
		return false;
	}
	
	//Same thing.
	//Return a non-null value to break with that value, return `null` to continue iterating.
	public static <T> @Nullable T sphereWithBreak(BlockPos start, int radius, Function<? super BlockPos.MutableBlockPos, @Nullable T> func) {
		BlockPos.MutableBlockPos cursor = start.mutable();
		for(BlockPos d : sphereRelativeOffsetsCached(radius)) {
			cursor.move(d);
			T whatever = func.apply(cursor);
			if(whatever != null) return whatever;
		}
		return null;
	}
	
	public static List<BlockPos> sphereAbsoluteOffsetsCached(int radius) {
		return sphereAbsoluteCache.computeIfAbsent(radius, BlockPosIteration::sphereAbsoluteOffsets);
	}
	
	public static List<BlockPos> sphereRelativeOffsetsCached(int radius) {
		return sphereRelativeCache.computeIfAbsent(radius, r -> relativize(sphereAbsoluteOffsetsCached(r)));
	}
	
	public static List<BlockPos> sphereAbsoluteOffsets(int radius) {
		//Volume of a sphere: 4/3 * pi * radius^3
		//Preallocate about that many slots in the array.
		//TODO: Check that i'm not overallocating here.
		ArrayList<BlockPos> results = new ArrayList<>(Mth.ceil(4d/3d * Math.PI * radius * radius * radius));
		
		for(int x = -radius; x <= radius; x++) {
			for(int y = -radius; y <= radius; y++) {
				for(int z = -radius; z <= radius; z++) {
					if(x * x + y * y + z * z <= radius * radius) {
						results.add(new BlockPos(x, y, z));
					}
				}
			}
		}
		
		results.sort(Comparator.comparingInt(BlockPosIteration::magSq));
		return results;
	}
	
	private static List<BlockPos> relativize(List<BlockPos> absolute) {
		List<BlockPos> results = new ArrayList<>(absolute.size());
		
		Map<BlockPos, BlockPos> objectCache = new HashMap<>();
		BlockPos prev = null;
		for(BlockPos next : absolute) {
			results.add(objectCache.computeIfAbsent(prev == null ? next : next.subtract(prev), x -> x));
			prev = next;
		}
		
		return results;
	}
	
	public static float distanceSq(BlockPos a, BlockPos b) {
		//Existing methods sometimes add a (.5, .5, .5) offset to one of the positions.
		//It's kinda fiddly whether they do or not, so i'll just do it myself.
		int dx = a.getX() - b.getX();
		int dy = a.getY() - b.getY();
		int dz = a.getZ() - b.getZ();
		return (dx * dx) + (dy * dy) + (dz * dz);
	}
	
	public static int magSq(BlockPos a) {
		return (a.getX() * a.getX()) + (a.getY() * a.getY()) + (a.getZ() * a.getZ());
	}
}
