package agency.highlysuspect.halogen.aura;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutableAuraContainer implements AuraContainer {
	public ImmutableAuraContainer(List<AuraStack> contentsList) {
		this.contents = new HashMap<>();
		for(AuraStack stack : contentsList) {
			this.contents.put(stack.type(), stack);
		}
	}
	
	private final Map<AuraType, AuraStack> contents;
	
	@Override
	public AuraStack accept(AuraStack toAdd, Simulation sim) {
		//Reject all attempts to add aura
		return toAdd;
	}
	
	@Override
	public AuraStack withdraw(AuraStack toWithdraw, Simulation sim) {
		//Never take away any aura
		return toWithdraw.withAmount(0);
	}
	
	@Override
	public Iterable<AuraStack> contents() {
		return contents.values();
	}
}
