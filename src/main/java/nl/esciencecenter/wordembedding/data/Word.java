package nl.esciencecenter.wordembedding.data;

import java.util.Comparator;

public class Word implements Comparator<Word> {
    private int occurrences;
    private int sortedIndex;
    private final String word;
    private int [] code;
    private int [] points;

    public Word(String word) {
        occurrences = 0;
        sortedIndex = -1;
        this.word = word;
    }

    public Word(String word, int occurrences) {
        this.occurrences = occurrences;
        sortedIndex = -1;
        this.word = word;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void incrementOccurrences() {
        occurrences++;
    }

    public int getSortedIndex() {
        return sortedIndex;
    }

    public void setSortedIndex(int index) {
        sortedIndex = index;
    }

    public String getWord() {
        return word;
    }

    public int getCodeLength() {
        return code.length;
    }

    public int getCode(int index) {
        return code[index];
    }

    public void setCodes(int [] code) {
        this.code = code;
    }

    public int getPoint(int index) {
        return points[index];
    }

    public void setPoints(int [] points) {
        this.points = points;
    }

    @Override
    public int compare(Word wordOne, Word wordTwo) {
        return wordOne.getWord().compareTo(wordTwo.getWord());
    }
}
