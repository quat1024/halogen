package agency.highlysuspect.halogen.util;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

public class States {
	//Copy all applicable blockstate properties of "src" onto "target".
	public static BlockState copyOnto(BlockState src, BlockState target) {
		BlockState result = target;
		for(Property<?> prop : src.getProperties()) {
			if(result.contains(prop)) result = resultWith(result, src, prop);
		}
		
		return result;
	}
	
	//need to name the generic parameter
	private static <T extends Comparable<T>> BlockState resultWith(BlockState result, BlockState src, Property<T> prop) {
		return result.with(prop, src.get(prop));
	}
}
