package nl.esciencecenter.wordembedding.validation;

import nl.esciencecenter.wordembedding.data.WordEmbedding;

public class CompareWordEmbeddings {
    public static Boolean compareIdentity(WordEmbedding [] embeddings) {
        if ( embeddings.length > 1 ) {
            if ( !checkDimension(embeddings) ) {
                return false;
            }
            // If all embeddings have the same dimensions, check that they all contain the same words
            // and that words are at the same coordinates
            for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
                for ( String referenceWord : embeddings[0].getWords() ) {
                    Float [] referenceCoordinates = embeddings[0].getWordCoordinates(referenceWord);
                    Float [] coordinates = embeddings[embeddingID].getWordCoordinates(referenceWord);
                    if ( coordinates == null ) {
                        return false;
                    }
                    // TODO: change the comparison to allow for precision error
                    for ( int dimension = 0; dimension < embeddings[0].getVectorDimensions(); dimension++ ) {
                        if ( referenceCoordinates[dimension].compareTo(coordinates[dimension]) != 0 ) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static Boolean checkDimension(WordEmbedding [] embeddings) {
        for ( int embeddingID = 1; embeddingID < embeddings.length; embeddingID++ ) {
            if ( !embeddings[embeddingID].getVectorDimensions().equals(embeddings[0].getVectorDimensions()) ) {
                return false;
            }
        }
        return true;
    }
}
