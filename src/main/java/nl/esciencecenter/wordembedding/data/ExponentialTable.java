package nl.esciencecenter.wordembedding.data;

public class ExponentialTable {
    private final Integer MAX_EXP = 6;
    private final Integer EXP_TABLE_SIZE = 1000;
    private float [] exponentialTable;

    public void initialize() {
        exponentialTable = new float [EXP_TABLE_SIZE + 1];
        for ( int x = 0; x < EXP_TABLE_SIZE; x++ ) {
            exponentialTable[x] = (float)(Math.exp((((x / (float)(EXP_TABLE_SIZE)) * 2) - 1) * MAX_EXP));
            exponentialTable[x] = exponentialTable[x] / (exponentialTable[x] + 1);
        }
    }

    public float get(Integer item) {
        return exponentialTable[item];
    }
}
