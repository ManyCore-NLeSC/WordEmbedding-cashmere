package nl.esciencecenter.wordembedding.data;

public class ExponentialTable {
    private final int MAX_EXP = 6;
    private final int EXP_TABLE_SIZE = 1000;
    private float [] exponentialTable;

    public ExponentialTable() {}

    public void initialize() {
        exponentialTable = new float [EXP_TABLE_SIZE + 1];
        for ( int x = 0; x < EXP_TABLE_SIZE; x++ ) {
            exponentialTable[x] = (float)(Math.exp((((x / (float)(EXP_TABLE_SIZE)) * 2) - 1) * MAX_EXP));
            exponentialTable[x] = exponentialTable[x] / (exponentialTable[x] + 1);
        }
    }

    public float get(int item) {
        return exponentialTable[item];
    }

    public int getMaximumExponential() {
        return MAX_EXP;
    }

    public int getTableSize() {
        return EXP_TABLE_SIZE;
    }
}
