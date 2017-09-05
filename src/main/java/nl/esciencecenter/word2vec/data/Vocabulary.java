package nl.esciencecenter.word2vec.data;

import java.util.Collection;
import java.util.HashMap;

public class Vocabulary {
    private Integer maxSize;
    private Integer occurrenceThreshold;
    private HashMap<String, Word> words;

    public Vocabulary() {
        maxSize = Integer.MAX_VALUE;
        occurrenceThreshold = 0;
        words = new HashMap<>();
    }

    public Vocabulary(Integer occurrenceThreshold) {
        maxSize = Integer.MAX_VALUE;
        this.occurrenceThreshold = occurrenceThreshold;
        words = new HashMap<>();
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setOccurrenceThreshold(Integer occurrenceThreshold) {
        this.occurrenceThreshold = occurrenceThreshold;
    }

    public Integer getOccurrenceThreshold() {
        return occurrenceThreshold;
    }

    public void addWord(Word word) {
        if ( words.containsKey(word.getWord()) ) {
            words.get(word.getWord()).incrementOccurrences();
        } else {
            words.put(word.getWord(), word);
            word.incrementOccurrences();
        }
    }

    private void removeWord(String word) {
        words.remove(word);
    }

    public Word getWord(String word) {
        return words.get(word);
    }

    public Collection<Word> getWords() {
        return words.values();
    }

    public Integer getNrWords() {
        return words.size();
    }

    public void reduce() {
        for ( Word word : words.values() ) {
            if ( word.getOccurrences() <= occurrenceThreshold ) {
                removeWord(word.getWord());
            }
        }
        occurrenceThreshold++;
    }
}
