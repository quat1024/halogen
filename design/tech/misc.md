* Enable assertions with the `-ea` java flag. Aura handler stuff has a lot of assertions

# Transactions

Bug that cropped up: Linking wand would use isBindingValid to ask "hello node A, is it cool if I bind you to node B?", and then actually bound it to node A (itself) instead.

All the preconditions in the world don't matter if you never actually listen to them.

Would have caught that with an assertion (assert isBindingValid in addBinding) but a transaction system might be a good idea as well so it's impossible to get into the broken state in the first place.