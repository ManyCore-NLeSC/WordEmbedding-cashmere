package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class SumTest {
    private static final int length = 42;

    @Test
    public void compute() {
        Float [] vectorOne = new Float [length];
        Float [] vectorTwo = new Float [length];
        Float [] results;

        for ( int item = 0; item < length; item++ ) {
            vectorOne[item] = item * 1.0f;
            vectorTwo[item] = (length - item) * 1.0f;
        }
        results = Sum.compute(vectorOne, vectorTwo);
        for ( int item = 0; item < length; item++ ) {
            assertTrue(FloatComparison.areSimilar((float)(length), results[item], 1.0e-06f));
        }

    }
}