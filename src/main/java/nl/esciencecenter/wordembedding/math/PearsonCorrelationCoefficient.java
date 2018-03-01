package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class PearsonCorrelationCoefficient {
    public static Float compute(ArrayList<Float []> vectors) {
        Float sumXY = 0.0f;
        Float sumX = 0.0f;
        Float sumX2 = 0.0f;
        Float sumY = 0.0f;
        Float sumY2 = 0.0f;

        for (Float[] vector : vectors) {
            sumXY += vector[0] * vector[1];
            sumX += vector[0];
            sumX2 += vector[0] * vector[0];
            sumY += vector[1];
            sumY2 += vector[1] * vector[1];
        }
        return (float)(((vectors.size() * sumXY) - (sumX * sumY))
                / Math.sqrt(((vectors.size() * sumX2) - (sumX * sumX)) * ((vectors.size() * sumY2) - (sumY * sumY))));
    }
}
