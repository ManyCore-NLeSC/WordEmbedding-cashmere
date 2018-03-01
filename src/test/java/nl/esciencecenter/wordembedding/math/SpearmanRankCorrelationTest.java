package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

public class SpearmanRankCorrelationTest {
    @Test
    public void compute() {
        ArrayList<Float []> vectors = new ArrayList<>();
        for ( int item = 0; item < 5; item++ ) {
            vectors.add(new Float [2]);
        }
        // {15, 18, 19, 20, 21} {25, 26, 28, 27, 29}
        vectors.get(0)[0] = 15.0f;
        vectors.get(0)[1] = 25.0f;
        vectors.get(1)[0] = 18.0f;
        vectors.get(1)[1] = 26.0f;
        vectors.get(2)[0] = 19.0f;
        vectors.get(2)[1] = 28.0f;
        vectors.get(3)[0] = 20.0f;
        vectors.get(3)[1] = 27.0f;
        vectors.get(4)[0] = 21.0f;
        vectors.get(4)[1] = 29.0f;
        assertTrue(FloatComparison.areSimilar(0.9f, SpearmanRankCorrelation.compute(Rank.compute(vectors)),
                1.0e-06f));
        vectors = new ArrayList<>();
        for ( int item = 0; item < 5; item++ ) {
            vectors.add(new Float [2]);
        }
        // {15, 18, 21, 15, 21} {25, 25, 27, 27, 27}
        vectors.get(0)[0] = 15.0f;
        vectors.get(0)[1] = 25.0f;
        vectors.get(1)[0] = 18.0f;
        vectors.get(1)[1] = 25.0f;
        vectors.get(2)[0] = 21.0f;
        vectors.get(2)[1] = 27.0f;
        vectors.get(3)[0] = 15.0f;
        vectors.get(3)[1] = 27.0f;
        vectors.get(4)[0] = 21.0f;
        vectors.get(4)[1] = 27.0f;
        assertTrue(FloatComparison.areSimilar(0.456435f, SpearmanRankCorrelation.compute(Rank.compute(vectors)),
                1.0e-06f));
    }
}
