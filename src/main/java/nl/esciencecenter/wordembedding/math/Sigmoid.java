package nl.esciencecenter.wordembedding.math;

import static java.lang.Math.exp;

public class Sigmoid {
    public static double compute(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
