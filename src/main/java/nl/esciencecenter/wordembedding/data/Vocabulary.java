package nl.esciencecenter.wordembedding.data;

import java.util.*;

public class Vocabulary {
    private int maxSize;
    private int occurrenceThreshold;
    private long maxWordOccurrences;
    private long occurrences;
    private final LinkedHashMap<String, Word> words;
    private Random randomNumberGenerator;

    public Vocabulary() {
        maxSize = Integer.MAX_VALUE;
        occurrenceThreshold = 0;
        maxWordOccurrences = 0;
        occurrences = 0;
        words = new LinkedHashMap<>();
        randomNumberGenerator = new Random(System.nanoTime());
    }

    public Vocabulary(int occurrenceThreshold) {
        maxSize = Integer.MAX_VALUE;
        this.occurrenceThreshold = occurrenceThreshold;
        occurrences = 0;
        words = new LinkedHashMap<>();
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setOccurrenceThreshold(int occurrenceThreshold) {
        this.occurrenceThreshold = occurrenceThreshold;
    }

    public int getOccurrenceThreshold() {
        return occurrenceThreshold;
    }

    public long getMaxWordOccurrences()
    {
        return maxWordOccurrences;
    }

    public long getOccurrences() {
        return occurrences;
    }

    // TODO: still pretty "hacky", think how it could be improved
    public void sort() {
        ArrayList<String> sortedWords = new ArrayList<>(words.keySet());

        sortedWords.sort((stringOne, stringTwo) -> {
            if ( stringOne.equals("</s>") ) {
                return -1;
            } else if ( stringTwo.equals("</s>") ) {
                return 1;
            } else {
                return getWord(stringTwo).getOccurrences() - getWord(stringOne).getOccurrences();
            }
        });
        occurrences = 0;
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
        byte [] binary = new byte [(getNrWords() * 2) + 1];
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
            ArrayList<Byte> tempCode = new ArrayList<>();
            byte [] code;
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
            code = new byte [index];
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

    public void incrementOccurrenceThreshold(int increment) {
        this.occurrenceThreshold += increment;
    }

    public void addWord(Word word) {
        if ( words.containsKey(word.getWord()) )
        {
            words.get(word.getWord()).incrementOccurrences();
        }
        else
        {
            words.put(word.getWord(), word);
            word.incrementOccurrences();
        }
        if ( words.get(word.getWord()).getOccurrences() > maxWordOccurrences)
        {
            maxWordOccurrences = words.get(word.getWord()).getOccurrences();
        }
    }

    public void addWord(String word) {
        addWord(new Word(word));
    }

    public void removeWord(String word) {
        words.remove(word);
    }

    public Word getWord(String word) {
        return words.get(word);
    }

    public String getRandomWord(float fraction)
    {
        // Pick a random number between 0 and occurrences
        long randomOccurrences = (long)(((occurrences - words.get("</s>").getOccurrences()) * randomNumberGenerator.nextFloat()) * fraction);
        long accumulator = 0;
        for ( String word : words.keySet() )
        {
            if ( word.equals("</s>") )
            {
                continue;
            }
            accumulator += words.get(word).getOccurrences();
            if ( randomOccurrences < accumulator )
            {
                return word;
            }
        }
        return null;
    }

    public Collection<Word> getWords() {
        return words.values();
    }

    public int getNrWords() {
        return words.size();
    }
}
