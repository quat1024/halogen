package agency.highlysuspect.halogen.util.transaction;

import java.util.IdentityHashMap;
import java.util.Map;

public class Transaction implements AutoCloseable {
	boolean open = true;
	Map<Participant<?>, Object> participantStates = new IdentityHashMap<>();
	
	public <T> void enlist(Participant<T> participant) {
		if(open) {
			participantStates.computeIfAbsent(participant, Participant::backupState);
		}
	}
	
	public void commit() {
		if(open) {
			//All good!
			open = false;
		}
	}
	
	public void rollback() {
		if(open) {
			open = false;
			participantStates.forEach(Participant::restoreStateErased);
		}
	}
	
	@Override
	public void close() {
		rollback();
	}
	
	public boolean isOpen() {
		return open;
	}
}
