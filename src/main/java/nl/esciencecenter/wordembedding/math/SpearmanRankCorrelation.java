package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class SpearmanRankCorrelation
{
    public static float compute(ArrayList<float []> vectors)
    {
        return PearsonCorrelationCoefficient.compute(Rank.compute(vectors));
    }
}
