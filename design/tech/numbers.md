How should amounts of aura be represented?

# Large integers

Botania's system for dealing with fractional mana amounts is "scaling up" - mana bursts fire in multiples of 120, which is a number you can divide evenly into 1, 2, 3, 4, 5, and 6.

Benefits:

* Cheap.
* Simple to code.

Drawbacks:

* The raw numbers end up being sorta arbitrary.
  * (Easy to fix: don't expose the numbers to users haha; I was going to do that anyway)
* When it doesn't divide evenly, you have to decide who gets the leftovers. Could lead to hard-to-predict aura behaviors. I want the mod to be 100% reliable.

# Rationals

Like in the FabLabs fluid API. I have a stripped-down version of their Fraction class in the `unused/` folder.

Benefits:

* No need to decide "who gets the leftovers".
* As many subdivisions as can fit in a `long`.
* Trendy, lol. It's my favorite implementation of a fluid API, and a production mod using a rational-based power system is proof that the idea works in real life.

Drawbacks:

* How much is "1/1"?
* Possible denominator explosion.
* Expensive.
* Complicated to code - At this very early stage of the api, I can't wrap my head around it.

could deal with denominator issues by:

* Capping the maximum number of outputs from a node to like, 10 or 15.
* Arbitrarily deleting slivers of aura below a certain threshold (1/100,000 or so).

# Floats

Oh god.

Benefits:

* Can *sorta* infinitely subdivide them?
* Cheap.
* Simple to code.

Drawbacks:

* Rounding error everywhere.
* Rounding error could lead to duplication bugs.

# The current plan

`int`s for now, move over to rationals as I get more of the mod sketched in.