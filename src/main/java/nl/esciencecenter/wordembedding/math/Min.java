package nl.esciencecenter.wordembedding.math;

public class Min
{
    public static float compute(float [][] vector)
    {
        float min = Float.MAX_VALUE;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = 0; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( vector[itemOne][itemTwo] < min )
                {
                    min = vector[itemOne][itemTwo];
                }
            }
        }
        return min;
    }
}