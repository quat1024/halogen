package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.util.transaction.Transaction;

import java.util.ArrayList;
import java.util.Collection;

public interface AuraContainer {
	/**
	 * Insert this much aura into the container.
	 *
	 * @return The leftover portion of aura that was *not* inserted into the container.
	 * Can be different from, but not larger than, the "aura" parameter.
	 */
	AuraStack accept(AuraStack toAdd, Transaction tx);
	
	/**
	 * Take this much aura out of the container.
	 *
	 * @return The amount of aura that was *just withdrawn from* the container.
	 * Can be different from, but not larger than, the "aura" parameter.
	 */
	AuraStack withdraw(AuraStack toWithdraw, Transaction tx);
	
	/**
	 * @return A view into the contents of this aura container.
	 */
	Collection<AuraStack> contents();
	
	/**
	 * @return A view into the contents of this aura container, such that it is safe to modify the container and iterate this collection at the same time.
	 */
	default Collection<AuraStack> contentsCopy() {
		return new ArrayList<>(contents());
	}
	
	/**
	 * Try to pour all of my aura into another aura container.
	 * @return Whether all of my aura fit into the other container.
	 */
	default boolean pourInto(AuraContainer other, Transaction tx) {
		boolean allFit = true;
		
		for(AuraStack stack : contentsCopy()) {
			AuraStack withdrawnStack = withdraw(stack, tx);
			AuraStack leftoverAfterInsertion = other.accept(withdrawnStack, tx);
			if(!leftoverAfterInsertion.isEmpty()) {
				allFit = false;
				AuraStack leftoverAfterPutback = accept(leftoverAfterInsertion, tx);
				if(!leftoverAfterPutback.isEmpty()) {
					Init.warn("Couldn't reinsert aura in pourInto");
				}
			}
		}
		
		return allFit;
	}
	
	/**
	 * @return The total amount of aura currently in this container.
	 */
	default int computeTotalVolume() {
		int total = 0;
		for(AuraStack stack : contents()) {
			total += stack.amount();
		}
		return total;
	}
}