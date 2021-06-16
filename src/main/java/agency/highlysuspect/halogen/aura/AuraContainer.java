package agency.highlysuspect.halogen.aura;

import agency.highlysuspect.halogen.util.transaction.Transaction;

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
}