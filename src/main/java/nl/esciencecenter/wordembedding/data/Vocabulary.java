package nl.esciencecenter.wordembedding.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Vocabulary {
    private Integer maxSize;
    private Integer occurrenceThreshold;
    private Integer occurrences;
    private final HashMap<String, Word> words;

    public Vocabulary() {
        maxSize = Integer.MAX_VALUE;
        occurrenceThreshold = 0;
        occurrences = 0;
        words = new HashMap<>();
    }

    public Vocabulary(Integer occurrenceThreshold) {
        maxSize = Integer.MAX_VALUE;
        this.occurrenceThreshold = occurrenceThreshold;
        occurrences = 0;
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

    public Integer getOccurrences() {
        return occurrences;
    }

    // TODO: still pretty "hacky", think how it could be improved
    public void sort() {
        ArrayList<String> sortedWords = new ArrayList<>(words.keySet());

        sortedWords.sort((stringOne, stringTwo) -> {
            if ( stringOne.equals("</s>") ) {
                return 1;
            } else if ( stringTwo.equals("</s>") ) {
                return -1;
            } else {
                return Integer.compare(getWord(stringOne).getOccurrences(), getWord(stringTwo).getOccurrences());
            }
        });
        Collections.reverse(sortedWords);
        for ( int wordIndex = 0; wordIndex < getNrWords(); wordIndex++ ) {
            words.get(sortedWords.get(wordIndex)).setSortedIndex(wordIndex);
            occurrences += words.get(sortedWords.get(wordIndex)).getOccurrences();
        }
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void generateCodes() {
        int positionOne, positionTwo;
        int minimumOne, minimumTwo;
        int [] count = new int [(getNrWords() * 2) + 1];
        int [] binary = new int [(getNrWords() * 2) + 1];
        int [] parent = new int [(getNrWords() * 2) + 1];

        for ( Word word : words.values() ) {
            count[word.getSortedIndex()] = word.getOccurrences();
        }
        for ( int wordIndex = getNrWords(); wordIndex < getNrWords() * 2; wordIndex++ ) {
            count[wordIndex] = Integer.MAX_VALUE;
        }
        positionOne = getNrWords() - 1;
        positionTwo = getNrWords();
        for ( int item = 0; item < getNrWords() - 1; item++ ) {
            if ( positionOne >= 0 ) {
                if ( count[positionOne] < count[positionTwo] ) {
                    minimumOne = positionOne;
                    positionOne--;
                } else {
                    minimumOne = positionTwo;
                    positionTwo++;
                }
            } else {
                minimumOne = positionTwo;
                positionTwo++;
            }
            if ( positionOne >= 0 ) {
                if ( count[positionOne] < count[positionTwo] ) {
                    minimumTwo = positionOne;
                    positionOne--;
                } else {
                    minimumTwo = positionTwo;
                    positionTwo++;
                }
            } else {
                minimumTwo = positionTwo;
                positionTwo++;
            }
            count[getNrWords() + item] = count[minimumOne] + count[minimumTwo];
            parent[minimumOne] = getNrWords() + item;
            parent[minimumTwo] = getNrWords() + item;
            binary[minimumTwo] = 1;
        }
        for ( Word word : words.values() ) {
            ArrayList<Integer> tempCode = new ArrayList<>();
            int [] code;
            ArrayList<Integer> tempPoints = new ArrayList<>();
            int [] points;
            int source = word.getSortedIndex();
            int index = 0;

            while ( source < ((getNrWords() * 2) - 2) ) {
                tempCode.add(binary[source]);
                tempPoints.add(source);
                index++;
                source = parent[source];
            }
            code = new int [index];
            points = new int [index + 1];
            points[0] = getNrWords() - 2;
            for ( int symbolIndex = 0; symbolIndex < index; symbolIndex++ ) {
                code[index - symbolIndex - 1] = tempCode.get(symbolIndex);
                points[index - symbolIndex] = tempPoints.get(symbolIndex) - getNrWords();
            }
            word.setCodes(code);
            word.setPoints(points);
        }
    }

    //
    // Synchronized methods
    //
    public synchronized void incrementOccurrenceThreshold(Integer increment) {
        this.occurrenceThreshold += increment;
    }

    public synchronized void addWord(Word word) {
        if ( words.containsKey(word.getWord()) ) {
            words.get(word.getWord()).incrementOccurrences();
        } else {
            words.put(word.getWord(), word);
            word.incrementOccurrences();
        }
    }

    public synchronized void addWord(String word) {
        addWord(new Word(word));
    }

    public synchronized void removeWord(String word) {
        words.remove(word);
    }

    public synchronized Word getWord(String word) {
        return words.get(word);
    }

    public synchronized Collection<Word> getWords() {
        return words.values();
    }

    public synchronized Integer getNrWords() {
        return words.size();
    }
}
