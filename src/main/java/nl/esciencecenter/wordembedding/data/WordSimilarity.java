package nl.esciencecenter.wordembedding.data;

import java.util.Collection;
import java.util.HashMap;

public class WordSimilarity {
    private long similaritiesNumber;

    private HashMap<String, HashMap<String, Float>> similarities;

    public WordSimilarity() {
        similarities = new HashMap<>();
    }

    public WordSimilarity(HashMap<String, HashMap<String, Float>> similarities) {
        this.similarities = similarities;
        similaritiesNumber = this.similarities.size();
        for ( HashMap<String, Float> similarity : this.similarities.values() ) {
            similaritiesNumber += similarity.size();
        }
    }

    public long getNumberOfSimilarities() {
        return similaritiesNumber;
    }

    public float getSimilarityScore(String wordOne, String wordTwo) {
        return similarities.get(wordOne).get(wordTwo);
    }

    public void addSimilarityScore(String wordOne, String wordTwo, float similarity) {
        if ( similarities.get(wordOne) == null ) {
            similarities.put(wordOne, new HashMap<>());
            similarities.get(wordOne).put(wordTwo, similarity);
        } else {
            similarities.get(wordOne).put(wordTwo, similarity);
        }
        similaritiesNumber++;
    }

    public int getNrWords() {
        return similarities.size();
    }

    public Collection<String> getWords() {
        return similarities.keySet();
    }

    public int getNrWords(String word) {
        return similarities.get(word).size();
    }

    public Collection<String> getWords(String word) {
        return similarities.get(word).keySet();
    }
}
