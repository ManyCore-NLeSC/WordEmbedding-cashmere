package nl.esciencecenter.wordembedding.math;

public class Norm {
    public static Float norm(Float [] vector) {
        return (float)(Math.sqrt(DotProduct.dotProduct(vector, vector)));
    }
}
