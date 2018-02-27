package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.Normalization;

public class NormalizeWordEmbedding {
    public static void compute(WordEmbedding embedding) {
        for ( String word : embedding.getWords() ) {
            embedding.setWordCoordinates(word, Normalization.compute(embedding.getWordCoordinates(word)));
        }
    }
}
