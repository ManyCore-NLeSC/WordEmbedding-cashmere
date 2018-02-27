package nl.esciencecenter.wordembedding.math;

public class Normalization {
    public static Float [] compute(Float [] vector) {
        return Division.compute(vector, Norm.compute(vector));
    }
}
