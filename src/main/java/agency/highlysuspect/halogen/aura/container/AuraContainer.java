package agency.highlysuspect.halogen.aura.container;

import agency.highlysuspect.halogen.Init;
import agency.highlysuspect.halogen.aura.AuraStack;
import agency.highlysuspect.halogen.util.transaction.Transaction;

import java.util.ArrayList;
import java.util.Collection;

public interface AuraContainer {
	/**
	 * Insert this much aura into the container.
	 *
	 * @return The leftover portion of aura that was *not* inserted into the container.
	 * The aura stack's key must be the same.
	 * The aura stack's amount can be less than or equal to toAdd's amount.
	 */
	<T> AuraStack<T> accept(AuraStack<T> toAdd, Transaction tx);
	
	/**
	 * Take this much aura out of the container.
	 *
	 * @return The portion of aura that was *just withdrawn from* the container.
	 * The aura stack's key must be the same.
	 * The aura stack's amount can be less than or equal to toWithdraw's amount.
	 */
	<T> AuraStack<T> withdraw(AuraStack<T> toWithdraw, Transaction tx);
	
	/**
	 * @return A view over the contents of this aura container.
	 */
	Collection<AuraStack<?>> contents();
	
	/**
	 * @return A view over the contents of this aura container, such that it is safe to modify the container and iterate this collection at the same time.
	 */
	default Collection<AuraStack<?>> contentsCopy() {
		return new ArrayList<>(contents());
	}
	
	/**
	 * Try to pour all of my aura into another aura container.
	 * If any aura doesn't fit, it's put back into this container.
	 * @return Whether all of my aura fit in the other container.
	 */
	default boolean pourAllInto(AuraContainer other, Transaction tx) {
		return pourAllIntoOverflow(other, this, tx);
	}
	
	/**
	 * Try to pour all of my aura into another aura container.
	 * If any aura doesn't fit, it's put into the "overflow" container.
	 * @return Whether all of my aura fit in the other container.
	 */
	default boolean pourAllIntoOverflow(AuraContainer other, AuraContainer overflow, Transaction tx) {
		boolean allFit = true;
		
		for(AuraStack<?> stack : contentsCopy()) {
			AuraStack<?> leftover = transferStack(stack, other, tx);
			if(!leftover.isEmpty()) {
				allFit = false;
				leftover = overflow.accept(leftover, tx);
				if(!leftover.isEmpty()) {
					Init.warn("pourAllInto overflow container full; losing aura " + leftover);
				}
			}
		}
		
		return allFit;
	}
	
	/**
	 * Try to transfer this stack into another aura container.
	 * If any aura doesn't fit, it's put back into this container.
	 * @return The portion that neither fit into the other container, nor into myself when trying to put it back.
	 */
	default <T> AuraStack<T> pourStackInto(AuraStack<T> stack, AuraContainer other, Transaction tx) {
		return pourStackIntoOverflow(stack, other, this, tx);
	}
	
	/**
	 * Try to transfer this stack into another aura container.
	 * If any aura doesn't fit, it's put into the "overflow" container.
	 * @return The portion that neither fit into the other container, nor into "overflow".
	 */
	default <T> AuraStack<T> pourStackIntoOverflow(AuraStack<T> stack, AuraContainer other, AuraContainer overflow, Transaction tx) {
		AuraStack<T> leftover = transferStack(stack, other, tx);
		if(!leftover.isEmpty()) {
			leftover = overflow.accept(stack, tx);
			if(!leftover.isEmpty()) {
				Init.warn("pourStackInto overflow container full; losing aura " + leftover);
			}
		}
		return leftover;
	}
	
	/**
	 * Extract this much from my container, then try to put it in the other container.
	 * @return Whatever didn't fit in the other container.
	 */
	default <T> AuraStack<T> transferStack(AuraStack<T> stack, AuraContainer other, Transaction tx) {
		return other.accept(withdraw(stack, tx), tx);
	}
	
	/**
	 * @return The total amount of aura currently in this container.
	 */
	default int computeTotalVolume() {
		int total = 0;
		for(AuraStack<?> stack : contents()) {
			total += stack.amount();
		}
		return total;
	}
}