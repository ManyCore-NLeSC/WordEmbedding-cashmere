package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.Cosine;

import java.util.ArrayList;

public class NearestNeighborsWord {
    public static String [] compute(String word, WordEmbedding embedding) {
        ArrayList<String> neighborsList = new ArrayList<>(embedding.getWords());
        String [] neighborsArray = new String [embedding.getNrWords() - 1];

        neighborsList.remove(word);
        neighborsList.sort((String wordOne, String wordTwo) -> Float.compare(
                Cosine.compute(embedding.getWordCoordinates(wordOne), embedding.getWordCoordinates(word)),
                Cosine.compute(embedding.getWordCoordinates(wordTwo), embedding.getWordCoordinates(word))));
        return neighborsList.toArray(neighborsArray);
    }
}
