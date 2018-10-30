package nl.esciencecenter.wordembedding.math;

public class Min
{
    public static float compute(float [] vector)
    {
        float min = Float.MAX_VALUE;
        for ( int item = 0; item < vector.length; item++ )
        {
            if ( vector[item] < min )
            {
                min = vector[item];
            }
        }
        return min;
    }
}