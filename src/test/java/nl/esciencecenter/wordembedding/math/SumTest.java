package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class SumTest {

    @Test
    public void compute() {
        Float [] vectorOne = new Float [5];
        Float [] vectorTwo = new Float [5];
        Float [] results;

        for ( int item = 0; item < 5; item++ ) {
            vectorOne[item] = item * 1.0f;
            vectorTwo[item] = (5 - item) * 1.0f;
        }
        results = Sum.compute(vectorOne, vectorTwo);
        for ( int item = 0; item < 5; item++ ) {
            assertTrue(FloatComparison.areSimilar(5.0f, results[item], 1.0e-06f));
        }

    }
}