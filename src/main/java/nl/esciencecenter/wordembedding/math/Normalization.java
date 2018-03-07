package nl.esciencecenter.wordembedding.math;

public class Normalization {
    public static float [] compute(float [] vector) {
        return Division.compute(vector, Norm.compute(vector));
    }
}
