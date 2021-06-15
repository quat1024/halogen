package agency.highlysuspect.halogen.aura;

//Loosely iitemhandler-ish interface. It's not great, but gets the job done.
//I can never remember whether the return param for iitemhandler is "amount left over" or "amount inserted".
//(I think that ifluidhandler is the other way round, confusingly.)
//This might be some wacky mishmash of both, I dunno.
public interface AuraContainer {
	/**
	 * Insert this much aura into the container.
	 * If sim == Simulation.JUST_CHECKING, perform a dry-run.
	 * 
	 * @return The leftover portion of aura that was *not* inserted into the container.
	 *         Can be different from, but not larger than, the "aura" parameter.
	 */
	AuraStack accept(AuraStack toAdd, Simulation sim);
	
	/**
	 * Take this much aura out of the container.
	 * If sim == Simulation.JUST_CHECKING, perform a dry-run.
	 * 
	 * @return The amount of aura that was *just withdrawn from* the container.
	 *         Can be different from, but not larger than, the "aura" parameter.
	 */
	AuraStack withdraw(AuraStack toWithdraw, Simulation sim);
	
	/**
	 * @return A view of this container's contents.
	 */
	Iterable<AuraStack> contents();
	
	default boolean hasAny(AuraType type) {
		return !withdraw(new AuraStack(type, 1), Simulation.JUST_CHECKING).isEmpty();
	}
	
	default boolean isEmpty() {
		for(AuraStack stack : contents()) {
			if(!stack.isEmpty()) return false;
		}
		return true;
	}
	
	default void pourInto(AuraContainer other) {
		for(AuraStack stack : contents()) {
			AuraStack withdrawn = withdraw(stack, Simulation.FOR_REAL);
			AuraStack leftover = other.accept(withdrawn, Simulation.FOR_REAL);
			AuraStack leftoverLeftover = accept(leftover, Simulation.FOR_REAL);
			assert leftoverLeftover.isEmpty(); //todo jank
		}
	}
}
