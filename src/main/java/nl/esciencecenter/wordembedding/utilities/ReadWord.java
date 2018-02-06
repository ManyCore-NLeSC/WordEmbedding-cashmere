package nl.esciencecenter.wordembedding.utilities;

class ReadWord {
    public static String readWord(String line, Boolean strict) {
        String [] words = line.split("[ \t]");
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
