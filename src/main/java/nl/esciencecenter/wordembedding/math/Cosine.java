package nl.esciencecenter.wordembedding.math;

public class Cosine {
    public static Float compute(Float [] vectorOne, Float [] vectorTwo) {
        return DotProduct.compute(vectorOne, vectorTwo) / (Norm.compute(vectorOne) * Norm.compute(vectorTwo));
    }
}
