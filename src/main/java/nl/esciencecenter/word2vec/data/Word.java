package nl.esciencecenter.word2vec.data;

import java.util.ArrayList;

public class Word {
    private Integer occurrences;
    private String word;
    private ArrayList<Integer> code;
    private ArrayList<Integer> points;

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
        return code.size();
    }

    public void setCode(ArrayList<Integer> code) {
        this.code = code;
    }

    public ArrayList<Integer> getCode() {
        return code;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }
}
