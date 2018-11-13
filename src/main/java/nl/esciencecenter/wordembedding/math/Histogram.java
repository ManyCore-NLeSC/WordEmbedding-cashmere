package nl.esciencecenter.wordembedding.math;

public class Histogram
{
    public static long [] compute(float [][] vector, float min, float max)
    {
        long [] histogram = new long [100];
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = itemOne; itemTwo < vector[itemOne].length; itemTwo++ ) {
                if ( Float.isFinite(vector[itemOne][itemTwo]) )
                {
                    histogram[(int)(((vector[itemOne][itemTwo] - min) * (histogram.length - 1)) / (max - min))]++;
                }
            }
        }
        return histogram;
    }

    public static void print(long [] histogram, float min, float max)
    {
        for ( int item = 0; item < histogram.length; item++ )
        {
            System.out.println((item) + " " + (min + (item * ((max - min) / histogram.length))) + " " + histogram[item]);
        }
    }
}
