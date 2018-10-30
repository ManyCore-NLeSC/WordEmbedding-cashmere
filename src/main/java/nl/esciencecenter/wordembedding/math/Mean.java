package nl.esciencecenter.wordembedding.math;

public class Mean
{
    public static float compute(float [] vector)
    {
        float mean = vector[0];
        for ( int item = 1; item < vector.length; item++ )
        {
            float oldMean = mean;
            mean = oldMean + ((vector[item] - oldMean) / (item + 1));
        }
        return mean;
    }
}