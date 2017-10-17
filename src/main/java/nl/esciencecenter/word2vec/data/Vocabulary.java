package nl.esciencecenter.word2vec.data;

import java.util.*;

public class Vocabulary {
    private Integer maxSize;
    private Integer occurrenceThreshold;
    private HashMap<String, Word> words;
    private ArrayList<String> sortedWords;

    public Vocabulary() {
        maxSize = Integer.MAX_VALUE;
        occurrenceThreshold = 0;
        words = new HashMap<>();
        sortedWords = new ArrayList<>();
    }

    public Vocabulary(Integer occurrenceThreshold) {
        maxSize = Integer.MAX_VALUE;
        this.occurrenceThreshold = occurrenceThreshold;
        words = new HashMap<>();
        sortedWords = new ArrayList<>();
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
            sortedWords.add(word.getWord());
            word.incrementOccurrences();
        }
    }

    public void addWord(String word) {
        addWord(new Word(word));
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

    public ArrayList<String> getSortedWords() {
        return sortedWords;
    }

    public Integer getNrWords() {
        return words.size();
    }

    public void reduce() {
        ArrayList<String> wordsToRemove = new ArrayList<>();

        for ( Word word : words.values() ) {
            if ( word.getOccurrences() < occurrenceThreshold ) {
                wordsToRemove.add(word.getWord());
            }
        }
        for ( String word : wordsToRemove ) {
            removeWord(word);
        }
        occurrenceThreshold++;
    }

    public void sort() {
        Collections.sort(sortedWords, new Comparator<String>() {
            @Override
            public int compare(String stringOne, String stringTwo) {
                return Integer.compare(getWord(stringOne).getOccurrences(), getWord(stringTwo).getOccurrences());
            }
        });
    }
}
