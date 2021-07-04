# So uh, transaction system

What is a *transaction*?

* A transaction is *opened*
* *Participants* are added to the transaction
* Temporary modifications can be made to participants *within* the transaction
* Either the transaction succeeds:
	* all of the modifications are *applied* to all participants
	* atomic, and always succeeds
* Or it fails:
	* all of the modifications are discarded

Transactions always end by succeeding or failing. Outside the transaction, no intermediate states from within it can be observed.

What does it mean to "apply a modification"?

* Modifications added to a list. Within the transaction, participants check their "real" state and apply modifications from the list in, like, getter functions.
	* e.g. if there's 10 aura "for real" and a pending action to add 5 aura, when asked, it has 15 aura
	* Complex to reason about, error-prone maybe?
* Make a backup copy of all participants. Success is a no-op, failure replaces the real state with the copy.
	* Or the other way round.
	* *Sounds* slow.
	* Copying can be error-prone (Must be a deep-copy)

## What does this buy you?

Lemme copypaste some code from Halogen real quick. Simplified a bit for display. The API here is "my halfassed IFluidHandler ripoff" I don't intend to stick with, but wrote to get the mod sketched in a bit:

```java
AuraStack toSend = mainContainer.withdraw(stack.withAmount(5), Simulation.JUST_CHECKING);
AuraStack doesItFit = otherNode.accept(toSend, Simulation.JUST_CHECKING);
if(doesItFit.isEmpty()) {
	toSend = mainContainer.withdraw(stack.withAmount(5), Simulation.FOR_REAL);
	doesItFit = otherNode.accept(toSend, Simulation.FOR_REAL);
	assert doesItFit.isEmpty();
}
```

You can see what I mean, right?

* Perform a dry-run to make sure that the action can work.
* If it works: carefully copy-paste the code, and perform exactly the same action, but "for real."
* Just in case, assert that the dry-run didn't lie.

If you edit one of the 5s without editing the other, you just invented a resource-dupe or deletion bug, or can end up with things like overfull containers.

With a transaction system, it might look more like this:

```java
Transaction<Something> tx = new Transaction<>();
AuraStack toSend = mainContainer.withdraw(stack.withAmount(MAX_AURA_SEND), tx);
AuraStack leftover = otherNode.accept(toSend, tx);
if(leftover.isEmpty()) {
	tx.commit();
}
```

It's still possible to do "dry-runs" (unconditional `.rollback()` calls in the transaction) if you need to.

## Prior art

FastTransferLib experimented with this (but ended up discarding the idea) in their `transactions` branch. I haven't talked to technic4n - idk if it was just part of routine slimming-down the API to get something not as bikesheddable, if there were some unforseen practical issues, or what.

* Single global Transaction instance.
* Transaction is `AutoCloseable` so you can't forget to close it
* `onSuccess` (sub-transactions) vs. `onFinalSuccess` (things like inventory markdirty)

The mod (at this point in time) is a little not finished, energy API does not seem to use the transaction system. Look in item/fluid for examples.

Very general/polymorphic example usage in the `Movement` class: https://github.com/Technici4n/FastTransferLib/blob/transactions/src/main/java/dev/technici4n/fasttransferlib/api/transfer/Movement.java

see `SimpleFluidStorage`: https://github.com/Technici4n/FastTransferLib/blob/transactions/src/main/java/dev/technici4n/fasttransferlib/base/fluid/SimpleFluidStorage.java

I think `SimpleFluidStorage` is a good example for how state is kept in transactions. The current state of the inventory is saved to a pair, and in `onClose`, if the transaction was *not* successful, it gets restored.

```java
@Override
public Object onEnlist() {
	// TODO: pool these things?
	return new Object[] { fluid, amount };
}

@Override
public void onClose(Object state, boolean success) {
	if (!success) {
		Object[] oldState = (Object[]) state;
		this.fluid = (Fluid) oldState[0];
		this.amount = (long) oldState[1];
	}
}
```

`TransactionImpl` sheds some light on what happens when you `enlist` yourself in a transaction: (They emulate recursive transactions within a singleton `Transaction` using a list data structure)

```java
for (int i = 0; i <= stackPointer; ++i){
	STACK.get(i).stateStorage.computeIfAbsent(participant, Participant::onEnlist);
}
```

Couple other notes on fasttransferlib:

* `Storage`s are granular: `InventoryStorageView` produces a new `View` for each *slot* in an inventory. Seems weird, but it means `Transaction`s only need to worry about a single item slot 