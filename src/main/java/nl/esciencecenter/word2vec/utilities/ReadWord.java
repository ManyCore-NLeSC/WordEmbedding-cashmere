package nl.esciencecenter.word2vec.utilities;

public class ReadWord {
    public static String readWord(String line, Boolean strict) {
        String word = line.split("[ \t]")[0];

        line = line.substring(word.length());
        if ( strict ) {
            word = word.replaceAll("\\W", "");
        }
        if ( word.equals("") ) {
            return null;
        }
        return word;
    }
}
