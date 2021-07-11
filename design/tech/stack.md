# aura stacks

There's a couple degrees of freedom here

## Like vanilla

* Aura Type (~ Item), a registered class containing some functionality
* Aura Stack (~ Item stack), class holding together type, opaque mutable NBT, and mutable count

I can talk all day about why I don't like mutable item stacks. I guess they're most performant; fewer copies. But they lead to so many bugs and they're just not fun to work with in my opinion.

## Vanilla but it's immutable

* Aura Type
* Aura Stack (type + NBT + count)

This is most familiar, but in cases like "how much halogen:item_aura with a diamond item is in the aura node", you can't really talk about that piece of data as a unit? either you supply item / nbt as two separate parameters, or you make zero-sized stacks, or ignore the size by convention to the method arguments. It's clearly workable, just ask any tech mod ever, but i don't like it. 

## Typed auxillary data (what's in the mod now)

* Aura Type (~item, a type T, a codec for T)
* Aura key (type + immutable instance of T)
* Aura stack (key + immutable count)

I like this approach - mainly because it lets me represent aura-stack auxillary data as real Java objects, and not opaque NBT blobs. if you've ever written super tedious error-prone methods to pull a POJO off of an item nbt tag and write it back, you know what i mean - and there aren't good answers for "what if there's no nbt tag". Codecs solve this, u just read the Java object off of the nbt one time, and only need to use the codec again when it's time to serialize.

Having no slot for arbitrary NBT, though, means there's no way to attach arbitrary data... If I ever add a crosscutting concern, like how all minecraft items can be enchanted by writing to a location on the nbt tag, I just... can't represent that in this system.

### Collection of typed auxillary data?

This is when it gets hairy and i haven't sketched all of this out

* Aura *keys* are `Map<AuraData<X>, X>` where X is the same for the key and value (in reality it'd be `<?>`)
  * auradata is some registered wrapper around a codec<x>
* Aura stacks are that plus a count
* I dunno if aura types fit in this picture?

I think this is too much. the goal is to allow attaching multiple pieces of cross-cutting data to a single aura stack, but a) Idk if i need that, b) this is getting really messy, c) If I really need to do arbitrary data under the previous system I can attach an opaque NBT tag to aura key

## What's so good about immutable?

The whole "transaction" system performs a lot of copying to back-up the pre-transaction state. If stacks are immutable, collections can be shallow-copied. Single-stacks can even be not copied at all... at the cost of all *other* effects on aura stacks requiring a copy

It's also easier to reason about imo

