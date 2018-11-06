package nl.esciencecenter.wordembedding.math;

public class Mean
{
    public static float compute(float [][] vector)
    {
        long nrElements = 0;
        float mean = 0;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ )
            {
                if ( Float.isFinite(vector[itemOne][itemTwo]) )
                {
                    nrElements++;
                    if ( nrElements == 1 )
                    {
                        mean = vector[itemOne][itemTwo];
                    }
                    else
                    {
                        float oldMean = mean;
                        mean = oldMean + ((vector[itemOne][itemTwo] - oldMean) / nrElements);
                    }
                }
            }
        }
        return mean;
    }
}