package agency.highlysuspect.halogen.aura;

public enum Simulation {
	JUST_CHECKING,
	FOR_REAL;
	
	public boolean justChecking() {
		return this == JUST_CHECKING;
	}
	
	public boolean forReal() {
		return this == FOR_REAL;
	}
}
