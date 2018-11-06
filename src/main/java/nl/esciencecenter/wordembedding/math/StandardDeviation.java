package nl.esciencecenter.wordembedding.math;

public class StandardDeviation
{
    public static float compute(float [][] vector)
    {
        long nrElements = 0;
        float mean = 0;
        float variance = 0;
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = 0; itemTwo < vector[itemOne].length; itemTwo++ )
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
                    variance += (vector[itemOne][itemTwo] - oldMean) * (vector[itemOne][itemTwo] - mean);
                }
            }
        }
        if ( nrElements > 1 )
        {
            variance /= (nrElements - 1);
        }
        else
        {
            variance = 0;
        }
        return (float)(Math.sqrt(variance));
    }
}
