package nl.esciencecenter.wordembedding;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.utilities.NormalizeWordEmbedding;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVector;
import nl.esciencecenter.wordembedding.validation.CompareWordEmbeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Word2VecCompareWordEmbeddings {
    public static void main(String [] args) {
        Float averageNeighborhoodIntersection;
        Integer neighborhoodFraction = 5;
        BufferedReader embeddingFile;
        WordEmbedding [] embeddings;

        if ( args.length < 2 ) {
            System.err.println("Usage: Word2VecCompareWordEmbeddings <embedding_file> ... <embedding_file>");
            return;
        }
        // Load word embeddings from file
        embeddings = new WordEmbedding [args.length];
        for ( int argument = 0; argument < args.length; argument++ ) {
            try {
                embeddingFile = new BufferedReader(new FileReader(args[argument]));
                embeddings[argument] = ReadWord2VecWordVector.read(embeddingFile);
                embeddingFile.close();
            } catch ( IOException err ) {
                System.err.println("Impossible to open \"" + args[argument] + "\".");
                return;
            }
        }
        // Compare
        if ( !CompareWordEmbeddings.compareDimensionality(embeddings) ) {
            System.out.println("The embeddings have different dimensionality.");
            return;
        }
        if ( CompareWordEmbeddings.compareNumericalIdentity(embeddings) ) {
            System.out.println("The embeddings are numerically identical.");
        } else if ( CompareWordEmbeddings.compareNumericalIdentity(embeddings, true) ) {
            System.out.println("The embeddings are numerically similar.");
        }
        for ( WordEmbedding embedding : embeddings ) {
            NormalizeWordEmbedding.compute(embedding);
        }
        averageNeighborhoodIntersection = CompareWordEmbeddings.compareNearestNeighbors(embeddings,
                neighborhoodFraction);
        System.out.format("Average Neighborhood Intersection: %d/%d (%.2f%%)",
                averageNeighborhoodIntersection.intValue(),
                ((embeddings[0].getNrWords() - 1) * neighborhoodFraction) / 100,
                (averageNeighborhoodIntersection * 100)
                        / (((embeddings[0].getNrWords() - 1) * neighborhoodFraction) / 100));
    }
}
