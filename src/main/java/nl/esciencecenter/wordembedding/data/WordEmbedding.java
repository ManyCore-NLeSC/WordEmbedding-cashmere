package nl.esciencecenter.wordembedding.data;

import java.util.Collection;
import java.util.HashMap;

public class WordEmbedding {
    private final Integer vectorDimensions;
    private final HashMap<Word, Float []> embedding;

    public WordEmbedding(Integer vectorDimensions) {
        this.vectorDimensions = vectorDimensions;
        embedding = new HashMap<>();
    }

    public Integer getVectorDimensions() {
        return vectorDimensions;
    }

    public void addWord(Word word, Float [] coordinates) {
        if ( coordinates.length == vectorDimensions ) {
            embedding.put(word, coordinates);
        }
    }

    public Collection<Word> getWords() {
        return embedding.keySet();
    }

    public Float [] getWordCoordinates(Word word) {
        return embedding.get(word);
    }
}
