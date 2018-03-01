package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class SpearmanRankCorrelation {
    public static Float compute(ArrayList<Float []> vectors) {
        return PearsonCorrelationCoefficient.compute(Rank.compute(vectors));
    }
}
