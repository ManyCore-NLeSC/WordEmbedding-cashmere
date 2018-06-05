package nl.esciencecenter.wordembedding.utilities.io;

public class ReadWord {
    public static String readWord(String line, boolean strict) {
        String [] words = line.split("[ \t]+");
        String word;

        if ( words.length == 0 ) {
            return null;
        } else {
            word = words[0];
        }
        if ( strict ) {
            word = word.replaceAll("\\W", "");
        }
        if ( word.equals("") ) {
            return null;
        }
        return word;
    }
}
