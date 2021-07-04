# Aura transfer innards

Ok right now it's decidedly *not* written like this, but this is how it'll have to work...

## Entityless approach

There is no such thing as an "aura burst" *entity*. Aura bursts are a mutable class containing fields like `AuraStack stack; BlockPos target; int ticksRemaining)`. Aura nodes create one of these when deciding to send aura to another node, and maintain a list of their own bursts. Every tick, the node ticks the burst's timer down and calls some virtual function on the stack's `AuraType`.

### Benefits

* No entities to potentially lose track of.
* Aura will always reach its destination (unless that destination is removed or something)

### Drawbacks

* Weird?
* Hard to control exactly how fast an aura burst moves. Unless it keeps position/velocity.
* Gridlocked. Can't redirect aura mid-flight.
	* Unlike Botania, "losing" aura due to it not hitting anything is very bad, though.
* Hard to query "gimme all the in-flight aura within 5 blocks of position X, Y, Z".
* Potential rendering headaches: bounding box, (depth sorting?)

## Entity approach

Aura nodes create "aura burst" entities and fire them at the target node. Bursts keep a position/velocity and when they tick, it calls a virtual function on the stack's `AuraType`.

### Benefits

* Familiar Botania-style
* Relatively simple rendering.
* Easy to query for nearby aura bursts, easy to deflect them to go in a different direction.

### Drawbacks

* Entities can get lost across chunk borders, build-up in unloaded chunks, get deflected by funny interdiction torches, and other assorted fun times.
