package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.WordEmbedding;

import java.util.HashMap;

public class NearestNeighborsWordEmbedding {
    public static HashMap<String, HashMap<Integer, String []>> compute(WordEmbedding[] embeddings) {
        HashMap<String, HashMap<Integer, String []>> neighbors = new HashMap<>();

        for ( String referenceWord : embeddings[0].getWords() ) {
            neighbors.put(referenceWord, new HashMap<>());
            for ( int embedding = 0; embedding < embeddings.length; embedding++ ) {
                if ( !embeddings[embedding].getWords().contains(referenceWord) ) {
                    neighbors.remove(referenceWord);
                    break;
                }
                neighbors.get(referenceWord).put(embedding,
                        NearestNeighborsWord.compute(referenceWord, embeddings[embedding]));
            }
        }
        return neighbors;
    }
}
