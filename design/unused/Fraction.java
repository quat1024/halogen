package agency.highlysuspect.halogen.aura;

import com.google.common.math.LongMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//*Heavily* based on the Fraction class from the FabLabsMC Fluid API.
public class Fraction {
	public Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public final long numerator;
	public final long denominator;
	
	public static final Fraction ZERO = whole(0);
	public static final Fraction ONE = whole(1);
	public static final Fraction HALF = new Fraction(1, 2);
	public static final Fraction THIRD = new Fraction(1, 3);
	public static final Fraction TWO_THIRDS = new Fraction(2, 3);
	public static final Fraction QUARTER = new Fraction(1, 4);
	public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
	
	public static final Fraction REALLY_REALLY_BIG = whole(Long.MAX_VALUE);
	
	public static final Codec<Fraction> CODEC = RecordCodecBuilder.create(i -> i.group(
		Codec.LONG.fieldOf("numerator").forGetter(Fraction::getNumerator),
		Codec.LONG.fieldOf("denominator").forGetter(Fraction::getDenominator)
	).apply(i, Fraction::new));
	
	public static Fraction whole(long numerator) {
		return new Fraction(numerator, 1);
	}
	
	public Fraction copy() {
		return new Fraction(numerator, denominator);
	}
	
	public Fraction simplify() {
		long gcd = LongMath.gcd(numerator, denominator);
		if(gcd == 1) return this;
		else return new Fraction(numerator / gcd, denominator / gcd);
	}
	
	//TODO (possibly), the math functions are kind of shit compared to FabLabsMC's implementation lol
	public Fraction add(Fraction other) {
		// a     c       a*d     c*b       a*d + c*b
		// -  +  -   =   ---  +  ---   =   ---------
		// b     d       b*d     b*d          b*d
		return new Fraction(
			numerator * other.denominator + other.numerator * denominator,
			denominator * other.denominator
		).simplify();
	}
	
	public Fraction multiply(Fraction other) {
		return new Fraction(
			numerator * other.numerator,
			denominator * other.denominator
		).simplify();
	}
	
	public Fraction divide(Fraction other) {
		return new Fraction(
			numerator * other.denominator,
			denominator * other.numerator
		).simplify();
	}
	
	public Fraction flip() {
		return new Fraction(denominator, numerator);
	}
	
	public long getNumerator() {
		return numerator;
	}
	
	public long getDenominator() {
		return denominator;
	}
	
	public Fraction withNumerator(long newNumerator) {
		return new Fraction(newNumerator, denominator);
	}
	
	public Fraction withDenominator(long newDenominator) {
		return new Fraction(numerator, newDenominator);
	}
	
	//hmmmmm
	public boolean isWeird() {
		return denominator > 10000;
	}
	
	public float approxFloat() {
		return (float) numerator / denominator;
	}
	
	public double approxDouble() {
		return (double) numerator / denominator;
	}
	
	public int approxInt() {
		return (int) numerator / (int) denominator;
	}
	
	public long approxLong() {
		return numerator / denominator;
	}
	
//	private static long lcm(long a, long b) {
//		return a / LongMath.gcd(a, b) * b;
//	}
}
