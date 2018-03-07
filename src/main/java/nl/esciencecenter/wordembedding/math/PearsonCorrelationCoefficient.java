package nl.esciencecenter.wordembedding.math;

import java.util.ArrayList;

public class PearsonCorrelationCoefficient {
    public static float compute(ArrayList<float []> vectors) {
        float sumXY = 0.0f;
        float sumX = 0.0f;
        float sumX2 = 0.0f;
        float sumY = 0.0f;
        float sumY2 = 0.0f;

        for (float [] vector : vectors) {
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
