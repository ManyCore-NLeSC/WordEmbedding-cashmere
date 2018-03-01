package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class Rank {
    public static ArrayList<Float []> compute(ArrayList<Float []> vectors) {
        ArrayList<Float []> ranks = new ArrayList<>();

        for ( int item = 0; item < vectors.size(); item++ ) {
            Integer [] rank = {1, 1};
            Integer [] equals = {1 ,1};

            for ( int predecessor = 0; predecessor < item; predecessor++ ) {
                if ( vectors.get(predecessor)[0].compareTo(vectors.get(item)[0]) < 0 ) {
                    rank[0] += 1;
                }
                if ( vectors.get(predecessor)[1].compareTo(vectors.get(item)[1]) < 0 ) {
                    rank[1] += 1;
                }
                if ( vectors.get(predecessor)[0].compareTo(vectors.get(item)[0]) == 0 ) {
                    equals[0] += 1;
                }
                if ( vectors.get(predecessor)[1].compareTo(vectors.get(item)[1]) == 0 ) {
                    equals[1] += 1;
                }
            }
            for ( int successor = item + 1; successor < vectors.size(); successor++ ) {
                if ( vectors.get(successor)[0].compareTo(vectors.get(item)[0]) < 0 ) {
                    rank[0] += 1;
                }
                if ( vectors.get(successor)[1].compareTo(vectors.get(item)[1]) < 0 ) {
                    rank[1] += 1;
                }
                if ( vectors.get(successor)[0].compareTo(vectors.get(item)[0]) == 0 ) {
                    equals[0] += 1;
                }
                if ( vectors.get(successor)[1].compareTo(vectors.get(item)[1]) == 0 ) {
                    equals[1] += 1;
                }
            }
            ranks.add(new Float [2]);
            ranks.get(item)[0] = rank[0] + ((equals[0] - 1) * 0.5f);
            ranks.get(item)[1] = rank[1] + ((equals[1] - 1) * 0.5f);
        }
        return ranks;
    }
}
