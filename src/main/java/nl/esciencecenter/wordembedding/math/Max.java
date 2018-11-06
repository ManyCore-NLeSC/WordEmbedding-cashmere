package nl.esciencecenter.wordembedding.math;

public class Max
{
    public static float compute(float [][] vector)
    {
        float max = Float.MIN_VALUE;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( Float.isFinite(vector[itemOne][itemTwo]) && vector[itemOne][itemTwo] > max )
                {
                    max = vector[itemOne][itemTwo];
                }
            }
        }
        return max;
    }
}