package nl.esciencecenter.word2vec.utilities;

import nl.esciencecenter.word2vec.data.Vocabulary;
import nl.esciencecenter.word2vec.data.Word;

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
