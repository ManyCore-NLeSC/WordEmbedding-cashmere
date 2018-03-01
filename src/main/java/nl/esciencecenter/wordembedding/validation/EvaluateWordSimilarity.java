package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordSimilarity;
import nl.esciencecenter.wordembedding.math.Cosine;
import nl.esciencecenter.wordembedding.math.SpearmanRankCorrelation;

import java.util.ArrayList;

public class EvaluateWordSimilarity {
    public static Float correlation(WordSimilarity similarity, WordEmbedding embedding) {
        ArrayList<Float []> results = new ArrayList<>();

        for ( String wordOne : similarity.getWords() ) {
            for ( String wordTwo : similarity.getWords(wordOne) ) {
                Float [] pair = new Float [2];

                if ( (embedding.getWordCoordinates(wordOne) != null)
                        && (embedding.getWordCoordinates(wordTwo) != null) ) {
                    pair[0] = similarity.getSimilarityScore(wordOne, wordTwo);
                    pair[1] = Cosine.compute(embedding.getWordCoordinates(wordOne),
                            embedding.getWordCoordinates(wordTwo));
                    results.add(pair);
                }
            }
        }

        return SpearmanRankCorrelation.compute(results);
    }
}
