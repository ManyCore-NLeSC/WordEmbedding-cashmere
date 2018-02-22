package nl.esciencecenter.wordembedding.math;

public class DotProduct {
    public static Float compute(Float [] vectorOne, Float [] vectorTwo) {
        Float accumulator = 0.0f;

        for ( int dimension = 0; dimension < vectorOne.length; dimension++ ) {
            accumulator += vectorOne[dimension] * vectorTwo[dimension];
        }
        return accumulator;
    }
}
