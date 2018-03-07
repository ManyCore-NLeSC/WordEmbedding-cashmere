package nl.esciencecenter.wordembedding.math;

public class FloatComparison {
    public static Boolean areIdentical(float x, float y) {
        return x == y;
    }

    public static Boolean areSimilar(float x, float y, float error) {
        return Math.abs(x - y) < error;
    }
}
