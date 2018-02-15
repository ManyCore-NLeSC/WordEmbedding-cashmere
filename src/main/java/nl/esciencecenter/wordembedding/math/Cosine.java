package nl.esciencecenter.wordembedding.math;

public class Cosine {
    public static Float cosine(Float [] vectorOne, Float [] vectorTwo) {
        return DotProduct.dotProduct(vectorOne, vectorTwo) / (Norm.norm(vectorOne) * Norm.norm(vectorTwo));
    }
}
