package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.math.Cosine;

public class CompareWordEmbeddings {
    public static Boolean compareIdentity(WordEmbedding [] embeddings) {
        return compareIdentity(embeddings, false);
    }

    public static Boolean compareIdentity(WordEmbedding [] embeddings, Boolean allowError) {
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
                            if ( !same(referenceCoordinates[dimension], coordinates[dimension], 1.0e-06f) ) {
                                return false;
                            }
                        } else {
                            if ( !identical(referenceCoordinates[dimension], coordinates[dimension]) ) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static Boolean compareSimilarity(WordEmbedding []  embeddings) {
        if ( embeddings.length > 1 ) {
            for ( String wordOne : embeddings[0].getWords() ) {
                for ( String wordTwo : embeddings[0].getWords() ) {
                    Float referenceCosine = Cosine.cosine(embeddings[0].getWordCoordinates(wordOne),
                            embeddings[0].getWordCoordinates(wordTwo));

                    for ( int embedding = 1; embedding < embeddings.length; embedding++ ) {
                        Float testedCosine = Cosine.cosine(embeddings[embedding].getWordCoordinates(wordOne),
                                embeddings[embedding].getWordCoordinates(wordTwo));
                        if ( !same(referenceCosine, testedCosine, 0.1f) ) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static Boolean testDimensionality(WordEmbedding [] embeddings) {
        for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
            if ( !embeddings[embeddingID].getVectorDimensions().equals(embeddings[0].getVectorDimensions()) ) {
                return false;
            }
        }
        return true;
    }

    private static Boolean identical(Float x, Float y) {
        return x.compareTo(y) == 0;
    }

    private static Boolean same(Float x, Float y, Float error) {
        return Math.abs(x - y) < error;
    }
}
