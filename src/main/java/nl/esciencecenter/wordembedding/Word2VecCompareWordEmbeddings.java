package nl.esciencecenter.wordembedding;

import nl.esciencecenter.wordembedding.data.WordEmbedding;
import nl.esciencecenter.wordembedding.utilities.ReadWord2VecWordVector;
import nl.esciencecenter.wordembedding.validation.CompareWordEmbeddings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Word2VecCompareWordEmbeddings {
    public static void main(String [] args) {
        BufferedReader embeddingFile;
        WordEmbedding [] embeddings;
        if ( args.length < 2 ) {
            System.err.println("Usage: Word2VecCompareWordEmbeddings <embedding_file> ... <embedding_file>");
            return;
        }
        // Load vocabularies from file
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
        if ( CompareWordEmbeddings.compareIdentity(embeddings) ) {
            System.out.println("The embeddings are identical.");
        } else  {
            System.out.println("The embeddings are different.");
        }
    }
}
