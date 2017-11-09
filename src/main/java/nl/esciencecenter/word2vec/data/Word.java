package nl.esciencecenter.word2vec.data;

public class Word {
    private Integer occurrences;
    private String word;
    private int [] code;
    private int [] points;

    public Word(String word) {
        occurrences = 0;
        this.word = word;
    }

    public Word(String word, Integer occurrences) {
        this.occurrences = occurrences;
        this.word = word;
    }

    public Integer getOccurrences() {
        return occurrences;
    }

    public void incrementOccurrences() {
        occurrences++;
    }

    public String getWord() {
        return word;
    }

    public Integer getCodeLength() {
        return code.length;
    }

    public int getCode(int index) {
        return code[index];
    }

    public void setCodes(int [] code) {
        this.code = code;
    }

    public int [] getCodes() {
        return code;
    }

    public int getPoint(int index) {
        return points[index];
    }

    public void setPoints(int [] points) {
        this.points = points;
    }

    public int [] getPoints() {
        return points;
    }
}
