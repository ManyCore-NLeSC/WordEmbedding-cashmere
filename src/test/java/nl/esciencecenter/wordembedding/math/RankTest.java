package nl.esciencecenter.wordembedding.math;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class RankTest {
    @Test
    public void compute() {
        ArrayList<Float []> vectors = new ArrayList<>();
        ArrayList<Float []> ranks;
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
        ranks = Rank.compute(vectors);
        assertEquals(1.0f, ranks.get(0)[0]);
        assertEquals(2.0f, ranks.get(1)[0]);
        assertEquals(3.0f, ranks.get(2)[0]);
        assertEquals(4.0f, ranks.get(3)[0]);
        assertEquals(5.0f, ranks.get(4)[0]);
        assertEquals(1.0f, ranks.get(0)[1]);
        assertEquals(2.0f, ranks.get(1)[1]);
        assertEquals(4.0f, ranks.get(2)[1]);
        assertEquals(3.0f, ranks.get(3)[1]);
        assertEquals(5.0f, ranks.get(4)[1]);
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
        ranks = Rank.compute(vectors);
        assertEquals(1.5f, ranks.get(0)[0]);
        assertEquals(3.0f, ranks.get(1)[0]);
        assertEquals(4.5f, ranks.get(2)[0]);
        assertEquals(1.5f, ranks.get(3)[0]);
        assertEquals(4.5f, ranks.get(4)[0]);
        assertEquals(1.5f, ranks.get(0)[1]);
        assertEquals(1.5f, ranks.get(1)[1]);
        assertEquals(4.0f, ranks.get(2)[1]);
        assertEquals(4.0f, ranks.get(3)[1]);
        assertEquals(4.0f, ranks.get(4)[1]);
    }
}
