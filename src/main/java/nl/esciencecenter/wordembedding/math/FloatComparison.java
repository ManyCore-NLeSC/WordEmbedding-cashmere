package nl.esciencecenter.wordembedding.math;

public class FloatComparison {
    public static Boolean areIdentical(Float x, Float y) {
        return x.compareTo(y) == 0;
    }

    public static Boolean areSimilar(Float x, Float y, Float error) {
        return Math.abs(x - y) < error;
    }
}
