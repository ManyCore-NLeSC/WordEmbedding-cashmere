package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class PearsonCorrelationCoefficient {
    public static Float compute(ArrayList<Float []> vectors) {
        Float sumXY = 0.0f;
        Float sumX = 0.0f;
        Float sumX2 = 0.0f;
        Float sumY = 0.0f;
        Float sumY2 = 0.0f;

        for ( int item = 0; item < vectors.size(); item++ ) {
            sumXY += vectors.get(item)[0] * vectors.get(item)[1];
            sumX += vectors.get(item)[0];
            sumX2 += vectors.get(item)[0] * vectors.get(item)[0];
            sumY += vectors.get(item)[1];
            sumY2 += vectors.get(item)[1] * vectors.get(item)[1];
        }
        return (float)(((vectors.size() * sumXY) - (sumX * sumY))
                / Math.sqrt(((vectors.size() * sumX2) - (sumX * sumX)) * ((vectors.size() * sumY2) - (sumY * sumY))));
    }
}
