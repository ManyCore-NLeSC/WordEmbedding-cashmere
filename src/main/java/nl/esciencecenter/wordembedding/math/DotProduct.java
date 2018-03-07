package nl.esciencecenter.wordembedding.math;

public class DotProduct {
    public static float compute(float [] vectorOne, float [] vectorTwo) {
        float accumulator = 0.0f;

        for ( int dimension = 0; dimension < vectorOne.length; dimension++ ) {
            accumulator += vectorOne[dimension] * vectorTwo[dimension];
        }
        return accumulator;
    }
}
