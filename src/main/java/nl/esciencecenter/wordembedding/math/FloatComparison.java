package nl.esciencecenter.wordembedding.math;

public class FloatComparison {
    public static boolean areIdentical(float x, float y) {
        return x == y;
    }

    public static boolean areSimilar(float x, float y, float error) {
        return Math.abs(x - y) < error;
    }
}
