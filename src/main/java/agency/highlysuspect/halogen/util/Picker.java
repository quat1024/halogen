package agency.highlysuspect.halogen.util;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Picker {
	public static <T> @Nullable T chooseUniform(Random random, List<T> things) {
		if(things.isEmpty()) return null;
		return things.get(random.nextInt(things.size()));
	}
}
