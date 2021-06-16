package agency.highlysuspect.halogen.util;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

public class CollectionsUtil {
	public static <T> Collection<T> pair(Collection<T> a, Collection<T> b) {
		return new PairCollection<>(a, b);
	}
	
	private record PairCollection<T>(Collection<T> a, Collection<T> b) implements Collection<T> {
		@Override
		public int size() {
			return a.size() + b.size();
		}
		
		@Override
		public boolean isEmpty() {
			return a.isEmpty() && b.isEmpty();
		}
		
		@Override
		public boolean contains(Object o) {
			return a.contains(o) || b.contains(o);
		}
		
		@NotNull
		@Override
		public Iterator<T> iterator() {
			return Iterators.concat(a.iterator(), b.iterator());
		}
		
		@NotNull
		@Override
		public Object[] toArray() {
			Object[] aArray = a.toArray();
			Object[] bArray = b.toArray();
			Object[] total = new Object[aArray.length + bArray.length];
			System.arraycopy(aArray, 0, total, 0, aArray.length);
			System.arraycopy(bArray, 0, total, aArray.length, bArray.length);
			return total;
		}
		
		@NotNull
		@Override
		public <T1> T1[] toArray(@NotNull T1[] a) {
			throw new UnsupportedOperationException("toArray with an argument is not implemented because im lazy");
		}
		
		@Override
		public boolean add(T t) {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
		
		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
		
		@Override
		public boolean containsAll(@NotNull Collection<?> c) {
			throw new UnsupportedOperationException("containsAll is not implemented because im lazy");
		}
		
		@Override
		public boolean addAll(@NotNull Collection<? extends T> c) {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
		
		@Override
		public boolean removeAll(@NotNull Collection<?> c) {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
		
		@Override
		public boolean retainAll(@NotNull Collection<?> c) {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException("PairCollection is immutable");
		}
	}
}
