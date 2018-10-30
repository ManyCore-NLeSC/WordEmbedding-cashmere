package nl.esciencecenter.wordembedding.math;

public class Max
{
    public static float compute(float [] vector)
    {
        float max = Float.MIN_VALUE;
        for ( int item = 0; item < vector.length; item++ )
        {
            if ( vector[item] > max )
            {
                max = vector[item];
            }
        }
        return max;
    }
}