package nl.esciencecenter.wordembedding.math;

public class Difference {
    public static Float [] compute(Float [] vectorOne, Float [] vectorTwo) {
        Float [] resultingVector = new Float [vectorOne.length];

        for ( int dimension = 0; dimension < vectorOne.length; dimension++ ) {
            resultingVector[dimension] = vectorOne[dimension] - vectorTwo[dimension];
        }
        return resultingVector;
    }
}
