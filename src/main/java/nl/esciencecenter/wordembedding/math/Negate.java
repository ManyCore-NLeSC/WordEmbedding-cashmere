package nl.esciencecenter.wordembedding.math;

public class Negate {
    public static float [] compute(float [] vectorOne) {
        float [] negatedVector = new float [vectorOne.length];

        for ( int dimension = 0; dimension < vectorOne.length; dimension++ ) {
            negatedVector[dimension] = -vectorOne[dimension];
        }
        return negatedVector;
    }
}
