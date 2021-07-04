# Aura pumps

An aura pump is a type of node that sends aura upwards, at the cost of some resource. Aura normally only flows downwards, so aura pumps are the only way to send it up again. Aura also normally does work when flowing downwards. This up-and-down cycle is like a waterwheel, and it's the heart of Halogen's playstyle.

## Ramifications of the pump system

Pumps need to be the *only* way to send aura upwards. This means:

* aura crystals cannot be automatically created from existing aura;
* aura nodes cannot hold their contents when broken

## Excitation

Something im thinking about...

Aura pumps have a limited range, just like all aura nodes. If you want to move aura up 50 blocks, and there's a pump that's like "sends aura up with redstone items" or something with a range of 10 blocks, you will need a whole ton of redstone dispensers along the way. And that just doesn't sound very fun or attractive. Even if aura nodes had a super long range, you'd only be able to go in a straight line per pump.

An interesting mechanic would be some way to turn aura into "excited" aura; if enough excited aura is in a node, it can pump aura upwards (proportional to how much excited aura is in the node), at the cost of turning some of it back into regular aura.

### open questions

This could be from a special "excitation" machine or mechanic, or maybe all nodes can pump upwards, and all pumps do is produce excited aura.

Is "excited" a *state* of aura, or a separate *type*? Can all aura types become excited?

# pump mechanics

Pumps consume some resource to charge them; while charged they can send aura upwards. The charge decreases proportional to the amount of aura sent, and maybe it decays over time a little too. This is where I try to mirror botania's philosophy behind generating flowers lol, they kinda occupy the same design space.

* Can i pull off a low-powered passive pump without falling into the daybloom trap???????? idk????
* growth pump. Charges when plants or trees grow in a nearby area.
* Fall damage pump lol
	* AC had something where mobs just fall*ing* nearby would pump, but that do be kinda passive though