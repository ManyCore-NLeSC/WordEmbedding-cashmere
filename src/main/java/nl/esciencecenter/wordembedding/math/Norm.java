package nl.esciencecenter.wordembedding.math;

public class Norm {
    public static float compute(float [] vector) {
        return (float)(Math.sqrt(DotProduct.compute(vector, vector)));
    }
}
