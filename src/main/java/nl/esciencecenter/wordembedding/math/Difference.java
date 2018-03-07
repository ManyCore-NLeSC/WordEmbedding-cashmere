package nl.esciencecenter.wordembedding.math;

public class Difference {
    public static float [] compute(float [] vectorOne, float [] vectorTwo) {
        float [] resultingVector = new float [vectorOne.length];

        for ( int dimension = 0; dimension < vectorOne.length; dimension++ ) {
            resultingVector[dimension] = vectorOne[dimension] - vectorTwo[dimension];
        }
        return resultingVector;
    }
}
