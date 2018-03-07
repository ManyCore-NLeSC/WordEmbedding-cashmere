package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DotProductTest {
    private static final int length = 6;

    @Test
    public void compute() {
        Float [] vectorOne = new Float [length];
        Float [] vectorTwo = new Float [length];

        for ( int item = 0; item < length; item++ ) {
            vectorOne[item] = item * 1.0f;
            vectorTwo[item] = (length - item) * 1.0f;
        }
        assertTrue(FloatComparison.areSimilar(35.0f, DotProduct.compute(vectorOne, vectorTwo), 1.0e-06f));
    }
}