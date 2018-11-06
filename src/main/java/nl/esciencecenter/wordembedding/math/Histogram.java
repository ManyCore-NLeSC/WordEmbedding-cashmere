package nl.esciencecenter.wordembedding.math;

public class Histogram
{
    public static long [] compute(float [][] vector, float min, float max)
    {
        long [] histogram = new long [100];
        for ( int itemOne = 0; itemOne < vector.length; itemOne++ )
        {
            for ( int itemTwo = 0; itemTwo < vector[itemOne].length; itemTwo++ ) {
                if ( (int)((Math.abs(vector[itemOne][itemTwo]) / (max - min)) * (histogram.length - 1)) > 0 )
                histogram[(int)((Math.abs(vector[itemOne][itemTwo]) / (max - min)) * (histogram.length - 1))]++;
            }
        }
        return histogram;
    }

    public static void print(long [] histogram)
    {
        for ( int item = 0; item < histogram.length; item++ )
        {
            System.out.println((item + 1) + ": " + histogram[item]);
        }
    }
}
