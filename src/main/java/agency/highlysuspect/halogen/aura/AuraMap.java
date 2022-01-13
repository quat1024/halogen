package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.util.Dirtyable;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class AuraMap implements Dirtyable {
	public AuraMap() {
		map = new Object2ObjectOpenHashMap<>();
	}
	
	public AuraMap(Collection<AuraStack<?>> stacks) {
		this();
		putAll(stacks);
	}
	
	public static final Codec<AuraMap> CODEC = AuraStack.CODEC.listOf().xmap(AuraMap::new, AuraMap::asStackList);
	
	@SuppressWarnings("CopyConstructorMissesField") //isDirty
	private AuraMap(AuraMap copySrc) {
		this();
		map.putAll(copySrc.map);
	}
	
	//Do not put empty stacks in the map!!!!!!!!!!!!!!!!!!!!!!!!!!!
	private final Object2ObjectOpenHashMap<AuraKey<?>, AuraStack<?>> map;
	private boolean isDirty;
	
	public <T> AuraStack<T> get(AuraKey<T> key) {
		if(map.containsKey(key)) return (AuraStack<T>) map.get(key);
		else return key.emptyStackOf();
	}
	
	//Doesn't return what was already there. Idk if it's really important.
	public <T> void put(AuraStack<T> stack) {
		if(stack.isEmpty()) map.remove(stack.key());
		else map.put(stack.key(), stack);
		isDirty = true;
	}
	
	public void putAll(Collection<AuraStack<?>> stacks) {
		for(AuraStack<?> stack : stacks) {
			put(stack);
		}
	}
	
	public <T> void modify(AuraKey<T> key, UnaryOperator<AuraStack<T>> function) {
		put(function.apply(get(key)));
	}
	
	public interface TryModify<T> {
		void success(AuraStack<T> newValue);
		void fail();
	}
	
	public <T> boolean tryModify(AuraKey<T> key, BiConsumer<AuraStack<T>, TryModify<T>> modifier) {
		boolean[] failed = new boolean[] { false };
		
		modifier.accept(get(key), new TryModify<>() {
			@Override
			public void success(AuraStack<T> newValue) {
				if(!failed[0]) put(newValue);
			}
			
			@Override
			public void fail() {
				failed[0] = true;
			}
		});
		
		return !failed[0];
	}
	
	public AuraMap copy() {
		return new AuraMap(this);
	}
	
	public void clear() {
		map.clear();
	}
	
	public Collection<AuraStack<?>> asStackCollection() {
		return map.values();
	}
	
	public List<AuraStack<?>> asStackList() {
		return new ArrayList<>(map.values());
	}
	
	public int computeTotalVolume() {
		int volume = 0;
		for(AuraStack<?> stack : map.values()) {
			volume += stack.amount();
		}
		return volume;
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public void clean() {
		isDirty = false;
	}
	
	//For convenience.
	public Tag toNbt() {
		return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow(false, Init.LOG::error);
	}
	
	public static AuraMap fromNbt(Tag element) {
		return CODEC.parse(NbtOps.INSTANCE, element).getOrThrow(false, Init.LOG::error);
	}
}
