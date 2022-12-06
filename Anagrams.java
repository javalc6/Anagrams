/**
The class Anagrams implements utility methods to look for anagrams of a given index of words.

To run a demo test: java Anagrams <index filename>
The index file shall contain one word for each row.

06-12-2022 version 0.1: first release
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Anagrams {

    private final HashMap<String, Set<String>> anagrams = new HashMap<>();

    /** Returns the number of sets of anagrams.
     * @return number of sets
     */
    public int size() {
        return anagrams.size();
    }

    /** Returns a set of anagrams related to the given word
     * @param word to look for anagrams
     * @return set of anagrams related to word or null if no set was found
     */
    public Set<String> getAnagrams(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return anagrams.get(new String(chars));
    }

    /** Returns an unmodifiable map of all anagrams
     * @return unmodifiable map of all anagrams
     */
    public Map<String, Set<String>> getMap() {
        return Collections.unmodifiableMap(anagrams);
    }

    /** Clear all stored anagrams
     */
    public void clear() {
        anagrams.clear();
    }

    /** Parse an index file to build anagrams datastructure.
     * The index file shall contain one word for each row.
     * @param filename of the index file
     */
    public void initFromFile(String filename) throws IOException {
        if (anagrams.size() > 0)
            anagrams.clear();
        HashMap<String, String> tm = new HashMap<>();
        LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
        String st;
        while ((st = in.readLine()) != null) {
            String word = st.trim();
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            tm.put(word, new String(chars));
        }
        in.close();

        Map<String, String> sorted_index = sortMapByValue(tm);

        String pred = null; Set<String> set = new HashSet<>();
        for (Map.Entry<String, String> entry: sorted_index.entrySet()) {
            String canonical = entry.getValue();
            if (pred != null) {
                if (canonical.equals(pred)) {
                    set.add(entry.getKey());
                } else {
                    if (set.size() > 1)//check if there at least two words
                        anagrams.put(pred, set);
                    set = new HashSet<>();
                    set.add(entry.getKey());//first element
                }
            } else {//first element
                set.add(entry.getKey());
            }
            pred = canonical;
        }
    }

    /** Export all anagram sets to file
     * The output file will contain one row for each word with anagrams, each row has the following format: canonical:anagram1|anagram2|...
     * @param filename of the output text file
     */
    public void exportFile(String filename) throws IOException {
        PrintWriter output = new PrintWriter(filename, StandardCharsets.UTF_8);
        anagrams.forEach((key, value) -> {
            output.print(key + ":");
            boolean first = true;
            for (String word: value) {
                if (first)
                    first = false;
                else output.print("|");
                output.print(word);
            }
            output.println();
        });

        output.close();
    }

    // sortMapByValue returns a map sorted by values (from high to low values)
    private static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    //demo of this class, usage: java Anagrams <index filename>
    public static void main(String[] args) {
        if (args.length == 1)
            try {
                Anagrams anagrams = new Anagrams();
                anagrams.initFromFile(args[0]);

                System.out.println("File " + args[0] + " contains " + anagrams.size() + " different sets of anagrams");

                System.out.println("Anagrams of 'file' :" + anagrams.getAnagrams("file"));

                System.out.println("Exporting anagrams to file");
                anagrams.exportFile("anagrams.txt");

                System.out.println("Print all elements in the anagrams:");
                Map<String, Set<String>> anagramsMap = anagrams.getMap();
                anagramsMap.forEach((key, value) -> System.out.println("key: " + key + ", value: " + value));

            } catch(IOException e) {
                System.err.println(e);
            }
        else {
            System.err.println("usage: java Anagrams <index filename>");
        }
    }
}
