package nl.esciencecenter.wordembedding.math;

public class Cosine {
    public static float compute(float [] vectorOne, float [] vectorTwo) {
        return DotProduct.compute(vectorOne, vectorTwo) / (Norm.compute(vectorOne) * Norm.compute(vectorTwo));
    }
}
