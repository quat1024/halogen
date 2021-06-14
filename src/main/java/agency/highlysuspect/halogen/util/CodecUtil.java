package agency.highlysuspect.halogen.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class CodecUtil {
	//For when the keys of the map are an implementation detail used to speed-up looking things up in the map, and are not serialized.
	public static <K, V> Codec<HashMap<K, V>> derivedMutableMapOf(Codec<V> valueCodec, Function<V, K> keyDeriver) {
		return valueCodec.listOf().xmap(list -> {
			HashMap<K, V> map = new HashMap<>();
			for(V item : list) {
				map.put(keyDeriver.apply(item), item);
			}
			return map;
		}, map -> new ArrayList<>(map.values()));
	}
	
	public static <T> T readNbt(Codec<T> codec, NbtCompound compound) {
		DataResult<T> result = codec.parse(NbtOps.INSTANCE, compound);
		return result.getOrThrow(false, s -> {
			throw new RuntimeException("problem decoding via " + codec + ": " + s);
		});
	}
	
	public static <T> NbtCompound writeNbt(Codec<T> codec, T thing) {
		var result = codec.encodeStart(NbtOps.INSTANCE, thing);
		NbtElement elem = result.getOrThrow(false, s -> {
			throw new RuntimeException("problem encoding via " + codec + ": " + s);
		});
		if(elem instanceof NbtCompound comp) {
			return comp;
		} else throw new RuntimeException("codec returned a " + elem.getClass().toGenericString() + " instead of NbtCompound");
	}
}
