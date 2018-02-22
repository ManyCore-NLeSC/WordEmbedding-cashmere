package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.Cosine;
import nl.esciencecenter.wordembedding.math.FloatComparison;
import nl.esciencecenter.wordembedding.utilities.NearestNeighborsWordEmbedding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CompareWordEmbeddings {
    public static Boolean compareDimensionality(WordEmbedding [] embeddings) {
        for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
            if ( !embeddings[embeddingID].getVectorDimensions().equals(embeddings[0].getVectorDimensions()) ) {
                return false;
            }
        }
        return true;
    }

    public static Boolean compareNumericalIdentity(WordEmbedding [] embeddings) {
        return compareNumericalIdentity(embeddings, false);
    }

    public static Boolean compareNumericalIdentity(WordEmbedding [] embeddings, Boolean allowError) {
        if ( embeddings.length > 1 ) {
            for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
                for ( String referenceWord : embeddings[0].getWords() ) {
                    Float [] referenceCoordinates = embeddings[0].getWordCoordinates(referenceWord);
                    Float [] coordinates = embeddings[embeddingID].getWordCoordinates(referenceWord);
                    if ( coordinates == null ) {
                        return false;
                    }
                    for ( int dimension = 0; dimension < embeddings[0].getVectorDimensions(); dimension++ ) {
                        if ( allowError ) {
                            if ( !FloatComparison.areSimilar(referenceCoordinates[dimension],
                                    coordinates[dimension], 1.0e-06f) ) {
                                return false;
                            }
                        } else {
                            if ( !FloatComparison.areIdentical(referenceCoordinates[dimension],
                                    coordinates[dimension]) ) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static Boolean compareNumericalSimilarity(WordEmbedding []  embeddings) {
        if ( embeddings.length > 1 ) {
            for ( String wordOne : embeddings[0].getWords() ) {
                for ( String wordTwo : embeddings[0].getWords() ) {
                    Float referenceCosine = Cosine.compute(embeddings[0].getWordCoordinates(wordOne),
                            embeddings[0].getWordCoordinates(wordTwo));

                    for ( int embedding = 1; embedding < embeddings.length; embedding++ ) {
                        Float testedCosine = Cosine.compute(embeddings[embedding].getWordCoordinates(wordOne),
                                embeddings[embedding].getWordCoordinates(wordTwo));
                        if ( !FloatComparison.areSimilar(referenceCosine, testedCosine, 0.1f) ) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static Float compareNearestNeighbors(WordEmbedding [] embeddings, Integer percentage) {
        Float averageNeighborhoodIntersection = 0.0f;
        HashMap<String, HashMap<Integer, ArrayList<String>>> neighbors
                = NearestNeighborsWordEmbedding.compute(embeddings);

        for ( String referenceWord : embeddings[0].getWords() ) {
            HashSet<String> neighborhoodIntersection = new HashSet<>();

            if ( !neighbors.containsKey(referenceWord) ) {
                continue;
            }
            for ( int embedding = 0; embedding < embeddings.length; embedding++ ) {
                HashSet<String> neighborhood = new HashSet<>();

                for ( int neighborID = 0;
                      neighborID < ((embeddings[0].getNrWords() - 1) * percentage) / 100;
                      neighborID++ ) {
                    neighborhood.add(neighbors.get(referenceWord).get(embedding).get(neighborID));
                }
                if ( embedding == 0 ) {
                    neighborhoodIntersection.addAll(neighborhood);
                } else {
                    neighborhoodIntersection.retainAll(neighborhood);
                }
            }
            averageNeighborhoodIntersection += neighborhoodIntersection.size();
        }
        return averageNeighborhoodIntersection / embeddings[0].getNrWords();
    }
}
