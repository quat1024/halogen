package agency.highlysuspect.halogen.util;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;

public class NbtHelper2 {
	public static Set<BlockPos> toBlockPosHashSet(ListTag list) {
		HashSet<BlockPos> positions = new HashSet<>();
		for(int i = 0; i < list.size(); i++) {
			positions.add(NbtUtils.readBlockPos(list.getCompound(i)));
		}
		return positions;
	}
	
	public static ListTag fromBlockPosSet(Set<BlockPos> positions) {
		ListTag list = new ListTag();
		positions.forEach(p -> list.add(NbtUtils.writeBlockPos(p)));
		return list;
	}
}
