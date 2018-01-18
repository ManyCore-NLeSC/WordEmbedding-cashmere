package nl.esciencecenter.wordembedding.utilities;

import nl.esciencecenter.wordembedding.data.Vocabulary;
import nl.esciencecenter.wordembedding.data.Word;

import java.io.BufferedWriter;
import java.io.IOException;

public class SaveVocabulary {
    public static void save(Vocabulary vocabulary, BufferedWriter fileWriter) throws IOException {
        for ( Word word : vocabulary.getWords() ) {
            fileWriter.write(word.getWord() + " " + String.valueOf(word.getOccurrences()));
            fileWriter.newLine();
        }
    }
}
