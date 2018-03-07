package nl.esciencecenter.wordembedding;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.data.WordSimilarity;
import nl.esciencecenter.wordembedding.utilities.NormalizeWordEmbedding;
import nl.esciencecenter.wordembedding.utilities.io.ReadWord2VecWordVector;
import nl.esciencecenter.wordembedding.utilities.io.ReadWordSimilarity;
import nl.esciencecenter.wordembedding.validation.EvaluateWordSimilarity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Word2VecEvaluateWordSimilarity {
    public static void main(String [] args) {
        WordSimilarity similarity = new WordSimilarity();
        BufferedReader embeddingFile;
        WordEmbedding [] embeddings;

        if ( args.length < 2 ) {
            System.err.println("Usage: Word2VecEvaluateWordSimilarity <test_file> <embedding_file> ... <embedding_file>");
            return;
        }
        try {
            ReadWordSimilarity.read(similarity, new BufferedReader(new FileReader(args[0])));
        } catch (IOException err) {
            System.err.println("Impossible to open \"" + args[0] + "\".");
            return;
        }
        // Load word embeddings from file
        embeddings = new WordEmbedding[args.length - 1];
        for ( int argument = 1; argument < args.length; argument++ ) {
            try {
                embeddingFile = new BufferedReader(new FileReader(args[argument]));
                embeddings[argument - 1] = ReadWord2VecWordVector.read(embeddingFile);
                embeddingFile.close();
            } catch ( IOException err ) {
                System.err.println("Impossible to open \"" + args[argument] + "\".");
                return;
            }
        }
        for ( WordEmbedding embedding : embeddings ) {
            NormalizeWordEmbedding.compute(embedding);
            System.out.format("Correlation: %.3f\n", EvaluateWordSimilarity.correlation(similarity, embedding));
        }
    }
}
