package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class NormalizationTest {
    private static final int length = 45;

    @Test
    public void compute() {
        float [] vectorOne = new float [length];
        float [] normalizedVector;

        for ( int item = 0; item < length; item++ ) {
            vectorOne[item] = item * 1.0f;
        }
        normalizedVector = Normalization.compute(vectorOne);
        assertTrue(FloatComparison.areSimilar(1.0f, Norm.compute(normalizedVector), 1.0e-06f));
    }
}