package nl.esciencecenter.wordembedding.math;

public class Division {
    public static Float [] compute(Float [] vectorOne, Float [] vectorTwo) {
        Float [] result = new Float [vectorOne.length];

        for ( int item = 0; item < result.length; item++ ) {
            result[item] = vectorOne[item] / vectorTwo[item];
        }
        return result;
    }

    public static Float [] compute(Float [] vectorOne, Float scalar) {
        Float [] result = new Float [vectorOne.length];

        for ( int item = 0; item < result.length; item++ ) {
            result[item] = vectorOne[item] / scalar;
        }
        return result;
    }
}
