package nl.esciencecenter.wordembedding.math;

public class Division {
    public static float [] compute(float [] vectorOne, float [] vectorTwo) {
        float [] result = new float [vectorOne.length];

        for ( int item = 0; item < result.length; item++ ) {
            result[item] = vectorOne[item] / vectorTwo[item];
        }
        return result;
    }

    public static float [] compute(float [] vectorOne, float scalar) {
        float [] result = new float [vectorOne.length];

        for ( int item = 0; item < result.length; item++ ) {
            result[item] = vectorOne[item] / scalar;
        }
        return result;
    }
}
