# Aura types

Spitballing.

## pure

Doesn't do anything special. Gets pumped around, can trigger sensors and things I guess?

## bore

Breaks the blocks in the path of its travel. The mining level is proportional to the amount of aura.

* kind of generic, but i like the idea
* Normally aura can't travel through blocks, so, this'll be interesting to design around I guess...
* Possibly the *radius* is proportional to the area as well. Hmm.

## push

Nudges nearby entities. The force/area is proportional to the amount of aura.

* definitely kind of generic. Not really sure what you'd use this for?
	* maybe funky item transport?

## item

You can dissolve any item into this type of aura. Lets say that mechanic turns 100 pure aura into 100 item aura. Then there's another block where, when it holds at least 100 item aura of the same item, it will turn it back into plain aura and produce the item again.

## exciter

Wrote a bit about this in the document on aura pumps but basically it'd (cause other nodes to act like a pump|is the thing that makes pumps work)

## meta

An aura type that "wraps" another aura type. Special aura doesn't perform its effect when wrapped, but it can be unwrapped with a machine to turn it back into what it was. I think this would be interesting with the exciter aura, it's like being able to move potential energy around.

## vehicle

You fuckin.. you can sit on it, and it carries you around the aura nodes, lmao, Like a luminizer

# Note on colors, appearance

I think it would be cool to have 16-color dyeable aura, but if color is meaningless then what do you use to tell the types apart? Pattern? Actually I think pattern would be a *great* signifier for aura types

like there could be a base particle to show the aura moving, that gets dyed the 16 color, and a separate particle texture for each type of functional aura that doesn't get dyed.