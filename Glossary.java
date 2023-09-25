import java.util.Comparator;
import java.util.Set;

import components.map.Map1L;
import components.queue.Queue1L;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Takes a given file (in the appropriate structure) and creates an online glossary
 * of the words and definitions in the given file. The html files the program creates
 * are stored in a folder given by the user. (Uses OSU's CSE components by requirement)
 *
 * @author Patrick Suijk
 *
 */
public final class Glossary {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private Glossary() {
    }

    /**
     * Compare {@code Map.Pair<String, String>}s in lexicographic order.
     */
    private static class Alphabetize implements Comparator<Map.Pair<String, String>> {
        @Override
        public int compare(Map.Pair<String, String> o1, Map.Pair<String, String> o2) {
            return o1.key().compareTo(o2.key());
        }
    }

    /**
     * Reads from a given input and assigns each word and definition to a key
     * and value in a given map.
     * @param wordDefs
     *      map in which to store words and definitions
     * @param input
     *      stream of input
     * @updates
     * @requires input.is_open
     * @ensures
     * wordDefs = [map of words and definition pairs from #input.content]
     */
    public static void getWordsAndDefinitions(Map<String, String> wordDefs,
            SimpleReader input) {
        if (input.isOpen()) {
            /*
             * While the end of the input stream has not been reached, read words and
             * definitions from the stream and add them as pairs <word, definition>
             * to wordDefs.
             */
            while (!input.atEOS()) {
                String word = input.nextLine();
                /*
                 * Declare a StringBuilder object for constructing the corresponding
                 * definition and declare a line to read the fist line of the definition.
                 */
                StringBuilder definition = new StringBuilder();
                String line = input.nextLine();
                /*
                 * While line is not an empty string and the end of the input stream
                 * has not been reached, append line to definition then set line equal
                 * to the value of the next line.
                 */
                while (!line.equals("") && !input.atEOS()) {
                    definition.append(line);
                    /*
                     * If the definition converted to a string is not an empty string,
                     * append a space to definition so that there is proper spacing
                     * if/when the next line is appended.
                     */
                    if (!definition.toString().equals("")) {
                        definition.append(" ");
                    }
                    line = input.nextLine();
                }
                /*
                 * Convert definition to a string called defStr then trim the extra
                 * space off the end of defStr. Add <word, defStr> to wordDefs.
                 */
                String defStr = definition.toString();
                defStr = defStr.substring(0, defStr.length() - 1);
                wordDefs.add(word, defStr);
            }
        }
    }

    /**
     * Output headers for html page.
     * @param title
     *      term that the page is for
     * @param out
     *      output stream
     * @requires
     *      out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    public static void outputHeaders(String title, SimpleWriter out) {
        /*
         * Print opening tags of html elements
         */
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + title + "</title>");
        out.println("</head>");
        out.println("<body>");
    }

    /**
     * Output footers for html page.
     * @param out
     *      output stream
     * @requires
     *      out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    public static void outputFooters(SimpleWriter out) {
        /*
         * Print closing tags of html elements
         */
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * For each value in glossary, finds each key from glossary that appears in
     * that value and replaces it with a hyperlinked version of that string.
     * (If there is a word such as "his" in the terms of the the file provided,
     * and there is a word such as "hiss" in one of the definitions, this method
     * will incorrectly convert "hiss" into a hyperlink to "his". This is a very
     * complicated issue to solve and thus this method does not account for those
     * kinds of fringe situations)
     * @param glossary
     *          map for which to update definitions to hyperlinks
     * @updates glossary
     * @ensures [any string in a value in {@code glossary} that is equivalent
     * to one of the keys in {@code glossary} (case insenstive for the first character),
     * a plural of one of the keys, or one of the keys followed by "," is replaced
     * with that string as a hyperlink]
     */
    public static void createHyperLinksInDefinitions(Map<String, String> glossary) {
        /*
         * Declare a temporary map for editing the contents of glossary.
         */
        Map<String, String> temp = glossary.newInstance();
        /*
         * For each pair of terms and definitions in glossary, find all terms
         * from the glossary that appear in that pairs definition and replace them
         * with hyperlinks to the respective terms page.
         */
        for (Map.Pair<String, String> pair : glossary) {
            String term = pair.key();
            String definition = pair.value();
            /*
             * Declare a set to hold all the terms from glossary that appear in
             * definition.
             */
            Set<String> termsInDef = new Set1L<>();
            /*
             * For each pair in glossary, grab the key and check if definition
             * contains that key. If so, add that key to termsInDef. Do the same
             * for the key with the character at index 0 as uppercase.
             */
            for (Map.Pair<String, String> wordAndDef : glossary) {
                String word = wordAndDef.key();
                /*
                 * Create a string called wordCapStr that holds the value of the
                 * current key but where the first letter is capitalized.
                 */
                StringBuilder wordCap = new StringBuilder();
                wordCap.append(Character.toUpperCase(word.charAt(0)));
                wordCap.append(word.substring(1));
                String wordCapStr = wordCap.toString();
                /*
                 * Create a string called wordLwrStr that holds the value of the
                 * current key but where the first letter is lower case.
                 */
                StringBuilder wordLwr = new StringBuilder();
                wordLwr.append(Character.toLowerCase(word.charAt(0)));
                wordLwr.append(word.substring(1));
                String wordLwrStr = wordLwr.toString();
                /*
                 * For {@code word}, {@code wordCapStr}, and {@code wordLwrStr}
                 * check if {@code definition} contains that string, if so, check if
                 * {@code termsInDef} contains that string, if not, add that string
                 * to termsInDef.
                 */
                if (definition.contains(word)) {
                    if (!termsInDef.contains(word)) {
                        termsInDef.add(word);
                    }
                }
                if (definition.contains(wordCapStr)) {
                    if (!termsInDef.contains(wordCapStr)) {
                        termsInDef.add(wordCapStr);
                    }
                }
                if (definition.contains(wordLwrStr)) {
                    if (!termsInDef.contains(wordLwrStr)) {
                        termsInDef.add(wordLwrStr);
                    }
                }
            }
            /*
             * For each term in termsInDef, replace all instances of that term
             * in definition with a hyperlink to that
             */
            for (String s : termsInDef) {
                int length = (definition.length() - s.length());
                /*
                 * Declare a string to hold the value of s as a hyperlink.
                 */
                String sLink = "<a href=" + s + ".html>" + s + "</a>";
                /*
                 * If the substring of definition from 0 to {@code length} is equal
                 * to the value of s, then set definition to that substring plus
                 * the concatenation of s as a hyperlink.
                 */
                if (definition.substring(length).equals(s)) {
                    definition = definition.substring(0, length) + sLink;
                }
                /*
                 * Replace all instances of {@code s + ","}, {@code s + "s"}, and
                 * {@code s + " "} with the s as a hyperlink with the added character
                 * accounted for.
                 */
                sLink = "<a href=" + s + ".html>" + s + "</a>,";
                definition = definition.replaceAll(s + ",", sLink);
                sLink = "<a href=" + s + ".html>" + s + "s</a>";
                definition = definition.replaceAll(s + "s", sLink);
                sLink = "<a href=" + s + ".html>" + s + "</a> ";
                definition = definition.replaceAll(s + " ", sLink);
            }
            temp.add(term, definition);
        }
        /*
         * Transfer the newly created map with hyperlinks to glossary.
         */
        glossary.transferFrom(temp);
    }

    /**
     * Processes one term, creating its specific html page.
     * @param termAndDef
     *          map pair to process
     * @param folderName
     *          string that represents where the output file should be stored
     * @requires
     *      out.is_open
     * @ensures out.content = #out.content *
     *   [an HTML page with the key of {@code termAndDef} as the title and header
     *   and the value of {@code termAndDef} as the paragraph]
     */
    public static void processTerm(Map.Pair<String, String> termAndDef,
            String folderName) {
        /*
         * Declare a string for the key and a string for the value of the given pair.
         */
        String term = termAndDef.key();
        String definition = termAndDef.value();
        /*
         * Open an output stream to a file in the given folder.
         */
        SimpleWriter out = new SimpleWriter1L(folderName + "/" + term + ".html");
        outputHeaders(term, out);
        /*
         * Print the header and definition followed by a thick line.
         */
        out.println("<h1 style=color:red><b><i>" + term + "</i></b></h1>");
        out.println("<p style=margin-left:40px>" + definition + "</p>");
        out.println("<hr style=\"height:2px;border-width:0;color:gray;"
                + "background-color:gray\">");
        /*
         * Declare string variable for the link to the index page then print
         * "Return to index" with the link embedded in the string "index";
         */
        String indexLink = "index.html";
        out.println("<p>Return to <a href=" + indexLink + ">index</a></p>");
        /*
         * Output footers then close the output stream.
         */
        outputFooters(out);
        out.close();
    }

    /**
     * Creates an alphabetized queue containing each pair from a given
     * Map<String, String>.
     * @param glossary
     *        the map to be used to create sorted queue of the map pairs
     * @return alphabetized
     *        a queue containing the elements of {@code glossary} in
     *        alphabetical order
     * @ensures [{@code alphabetized} is a queue containing all of the elements
     *          from {@code glossary} in alphabetical order (a-z)]
     */
    public static Queue<Map.Pair<String, String>>
    createSortedQueue(Map<String, String> glossary) {
        /*
         * Declare a queue then for each pair in glossary, add that pair to
         * the queue.
         */
        Queue<Map.Pair<String, String>> sorted = new Queue1L<>();
        for (Map.Pair<String, String> term : glossary) {
            sorted.enqueue(term);
        }
        /*
         * Declare a comparator then sort {@code sorted} using that comparator and
         * return {@code sorted}.
         */
        Comparator<Map.Pair<String, String>> order = new Alphabetize();
        sorted.sort(order);
        return sorted;
    }

    /**
     * Creates an index page for all the terms in the glossary.
     * @param glossary
     *          map with words and definitions
     * @param out
     *          output stream
     * @param folderName
     *          string representing where the output files should be stored
     * @requires
     *      out.is_open && [{@code folderName} exists]
     * @ensures out.content = #out.content *
     *   [an HTML page consisting of a title, header, and unordered list of the
     *   string values of the keys in {@code glossary} as hyperlinks]
     */
    public static void createIndexPage(Map<String, String> glossary,
            SimpleWriter out, String folderName) {
        /*
         * Declare string for the title of the index page then print headers.
         */
        String title = "Glossary";
        outputHeaders(title, out);
        /*
         * Print the title of the page as a header followed by a thick line and
         * a title for the following unordered list.
         */
        out.println("<h1>" + title + "</h1>");
        out.println("<hr style=\"height:2px;border-width:0;color:gray;"
                + "background-color:gray\">");
        out.println("<p><b>Index</b></p>");
        /*
         * Call createSortedQueue to create an alphabetized queue of the pairs
         * in glossary.
         */
        Queue<Map.Pair<String, String>> alphabetized = createSortedQueue(glossary);
        /*
         * Print opening tag for an unordered list, then for each pair in the given
         * queue, call processTerm to create an html page for that term and print a
         * link to that newly created page with the term as the title of that page.
         */
        out.println("<ul>");
        int length = alphabetized.length();
        for (int i = 0; i < length; i++) {
            Map.Pair<String, String> termAndDef = alphabetized.dequeue();
            processTerm(termAndDef, folderName);
            String term = termAndDef.key();
            String pageLink = term + ".html";
            out.println("<li><a href=" + pageLink + ">" + term
                    + "</a></h1>");
        }
        /*
         * Print closing tag for unordered list then print footers.
         */
        out.println("</ul>");
        outputFooters(out);
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Prompt user for the name of an input file of the proper structure, then
         * prompt the suer for the name/file path of a folder to save output files
         * within.
         */
        out.print("Please enter the name of the input file: ");
        String inFileName = in.nextLine();
        SimpleReader inFile = new SimpleReader1L(inFileName);
        out.print("Please enter the name of the folder to save output files in: ");
        String outFolder = in.nextLine();
        /*
         * Declare map to store the words and definitions from the given file then
         * call getWordsAndDefinitions to fill the map.
         */
        Map<String, String> glossary = new Map1L<>();
        getWordsAndDefinitions(glossary, inFile);
        /*
         * Call to createHyperLinksInDefinitions to create hyperlinks for every
         * key from glossary that appears in a value in glossary.
         */
        createHyperLinksInDefinitions(glossary);
        /*
         * Open a new output stream to an html file named index in the given folder,
         * then call createIndexPage passing the map, output stream, and folder name.
         */
        SimpleWriter outFile = new SimpleWriter1L(outFolder + "/index.html");
        createIndexPage(glossary, outFile, outFolder);
        /*
         * Close input and output streams
         */
        in.close();
        out.close();
        outFile.close();
    }
}
