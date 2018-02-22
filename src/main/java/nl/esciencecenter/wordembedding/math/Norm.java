package nl.esciencecenter.wordembedding.math;

public class Norm {
    public static Float compute(Float [] vector) {
        return (float)(Math.sqrt(DotProduct.compute(vector, vector)));
    }
}
