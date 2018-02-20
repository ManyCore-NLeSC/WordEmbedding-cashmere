package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.Cosine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class NearestNeighborsWordEmbedding {
    public static HashMap<String, HashMap<Integer, ArrayList<String>>> compute(WordEmbedding[] embeddings) {
        HashMap<String, HashMap<Integer, ArrayList<String>>> neighbors = new HashMap<>();

        for ( String referenceWord : embeddings[0].getWords() ) {
            neighbors.put(referenceWord, new HashMap<>());
            for ( int embedding = 0; embedding < embeddings.length; embedding++ ) {
                final int finalEmbedding = embedding;

                neighbors.get(referenceWord).put(embedding, new ArrayList<>(embeddings[embedding].getWords()));
                neighbors.get(referenceWord).get(embedding).remove(referenceWord);
                neighbors.get(referenceWord).get(embedding).sort((stringOne, stringTwo)
                        -> Float.compare(Cosine.cosine(embeddings[finalEmbedding].getWordCoordinates(referenceWord),
                        embeddings[finalEmbedding].getWordCoordinates(stringOne)),
                        Cosine.cosine(embeddings[finalEmbedding].getWordCoordinates(referenceWord),
                                embeddings[finalEmbedding].getWordCoordinates(stringTwo))));
                Collections.reverse(neighbors.get(referenceWord).get(embedding));
            }
        }
        return neighbors;
    }
}
