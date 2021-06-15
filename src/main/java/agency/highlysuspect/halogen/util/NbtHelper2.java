package agency.highlysuspect.halogen.util;

import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class NbtHelper2 {
	public static List<BlockPos> toBlockPosArrayList(NbtList list) {
		ArrayList<BlockPos> positions = new ArrayList<>();
		for(int i = 0; i < list.size(); i++) {
			positions.add(NbtHelper.toBlockPos(list.getCompound(i)));
		}
		positions.trimToSize();
		return positions;
	}
	
	public static NbtList fromBlockPosList(List<BlockPos> positions) {
		NbtList list = new NbtList();
		positions.forEach(p -> list.add(NbtHelper.fromBlockPos(p)));
		return list;
	}
}
