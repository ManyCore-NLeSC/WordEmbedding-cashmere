package nl.esciencecenter.word2vec.data;

public class Word {
    private Integer occurrences;
    private String word;

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
}
