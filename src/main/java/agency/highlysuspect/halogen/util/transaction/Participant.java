package agency.highlysuspect.halogen.util.transaction;

public interface Participant<T> {
	T backupState();
	void restoreState(T state);
	
	//Go directly to Java generics hell. Do not pass Go, do not collect $200.
	@SuppressWarnings("unchecked")
	default void restoreStateErased(Object state) {
		restoreState((T) state);
	}
}
