package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class PearsonCorrelationCoefficientTest {
    @Test
    public void compute() {
        ArrayList<Float []> vectors = new ArrayList<>();
        for ( int item = 0; item < 6; item++ ) {
            vectors.add(new Float [2]);
        }
        // {43, 21, 25, 42, 57, 59} {99, 65, 79, 75, 87, 81}
        vectors.get(0)[0] = 43.0f;
        vectors.get(0)[1] = 99.0f;
        vectors.get(1)[0] = 21.0f;
        vectors.get(1)[1] = 65.0f;
        vectors.get(2)[0] = 25.0f;
        vectors.get(2)[1] = 79.0f;
        vectors.get(3)[0] = 42.0f;
        vectors.get(3)[1] = 75.0f;
        vectors.get(4)[0] = 57.0f;
        vectors.get(4)[1] = 87.0f;
        vectors.get(5)[0] = 59.0f;
        vectors.get(5)[1] = 81.0f;
        assertTrue(FloatComparison.areSimilar(0.529809f, PearsonCorrelationCoefficient.compute(vectors),
                1.0e-06f));
        vectors = new ArrayList<>();
        for ( int item = 0; item < 5; item++ ) {
            vectors.add(new Float [2]);
        }
        // {15, 18, 21, 24, 27} {25, 25, 27, 31, 32}
        vectors.get(0)[0] = 15.0f;
        vectors.get(0)[1] = 25.0f;
        vectors.get(1)[0] = 18.0f;
        vectors.get(1)[1] = 25.0f;
        vectors.get(2)[0] = 21.0f;
        vectors.get(2)[1] = 27.0f;
        vectors.get(3)[0] = 24.0f;
        vectors.get(3)[1] = 31.0f;
        vectors.get(4)[0] = 27.0f;
        vectors.get(4)[1] = 32.0f;
        assertTrue(FloatComparison.areSimilar(0.953463f, PearsonCorrelationCoefficient.compute(vectors),
                1.0e-06f));
    }
}
