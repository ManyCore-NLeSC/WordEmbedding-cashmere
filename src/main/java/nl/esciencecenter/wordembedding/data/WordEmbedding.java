package nl.esciencecenter.wordembedding.data;

import java.util.Collection;
import java.util.HashMap;

public class WordEmbedding {
    private final int vectorDimensions;
    private final HashMap<String, float []> embedding;

    public WordEmbedding(int vectorDimensions) {
        this.vectorDimensions = vectorDimensions;
        embedding = new HashMap<>();
    }

    public int getVectorDimensions() {
        return vectorDimensions;
    }

    public int getNrWords() {
        return embedding.size();
    }

    public void addWord(String word, float [] coordinates) {
        if ( coordinates.length == vectorDimensions ) {
            embedding.put(word, coordinates);
        }
    }

    public Collection<String> getWords() {
        return embedding.keySet();
    }

    public float [] getWordCoordinates(String word) {
        return embedding.get(word);
    }

    public void setWordCoordinates(String word, float [] coordinates) {
        embedding.put(word, coordinates);
    }
}
