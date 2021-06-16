package agency.highlysuspect.halogen.util;

import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NbtHelper2 {
	public static Set<BlockPos> toBlockPosHashSet(NbtList list) {
		HashSet<BlockPos> positions = new HashSet<>();
		for(int i = 0; i < list.size(); i++) {
			positions.add(NbtHelper.toBlockPos(list.getCompound(i)));
		}
		return positions;
	}
	
	public static NbtList fromBlockPosSet(Set<BlockPos> positions) {
		NbtList list = new NbtList();
		positions.forEach(p -> list.add(NbtHelper.fromBlockPos(p)));
		return list;
	}
}
