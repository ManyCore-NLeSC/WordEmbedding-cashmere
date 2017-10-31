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

    public Integer getSortedIndex(String word) {
        return sortedWords.indexOf(word);
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
        sortedWords = new ArrayList<>(words.keySet());

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
    }

    // The code of this method is a straightforward translation of Google's C code
    // TODO: check if it could be written in a better way
    public void generateCodes() {
        int positionOne, positionTwo;
        int minimumOne, minimumTwo;
        ArrayList<Integer> count = new ArrayList<>((sortedWords.size() * 2) + 1);
        ArrayList<Integer> binary = new ArrayList<>((sortedWords.size() * 2) + 1);
        ArrayList<Integer> parent = new ArrayList<>((sortedWords.size() * 2) + 1);

        for ( int item = 0; item < sortedWords.size(); item++ ) {
            count.set(item, words.get(sortedWords.get(item)).getOccurrences());
        }
        for ( int item = sortedWords.size(); item < sortedWords.size() * 2; item++ ) {
            count.set(item, Integer.MAX_VALUE);
        }
        positionOne = sortedWords.size() - 1;
        positionTwo = sortedWords.size();
        for ( int item = 0; item < sortedWords.size() - 1; item++ ) {
            if ( positionOne >= 0 ) {
                if ( count.get(positionOne) < count.get(positionTwo) ) {
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
                if ( count.get(positionOne) < count.get(positionTwo) ) {
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
            count.set(sortedWords.size() + item, count.get(minimumOne) + count.get(minimumTwo));
            parent.set(minimumOne, sortedWords.size() + item);
            parent.set(minimumTwo, sortedWords.size() + item);
            binary.set(minimumTwo, 1);
        }
        for ( int item = 0; item < sortedWords.size(); item++ ) {
            ArrayList<Integer> code = new ArrayList<>();
            ArrayList<Integer> points = new ArrayList<>();
            int source = item;
            int index = 0;

            points.add(0, sortedWords.size() - 2);
            while ( source < ((sortedWords.size() * 2) - 2) ) {
                code.add(index, binary.get(source));
                points.add(index + 1, source);
                index++;
                source = parent.get(source);
            }
            words.get(sortedWords.get(item)).setCode(code);
            words.get(sortedWords.get(item)).setPoints(points);
        }
    }
}
