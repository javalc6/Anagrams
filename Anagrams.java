/**
The class Anagrams implements utility methods to look for anagrams of a given index of words.

To run a demo test: java Anagrams <index filename>
The index file shall contain one word for each row.

06-12-2022 version 0.1: first release
07-12-2022 version 0.2: added methods merge() and equals()
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

    private final HashMap<String, Set<String>> anagrams;

    public Anagrams() {
        anagrams = new HashMap<>();
    }

    /** Constructor to build anagrams datastructure using a List of String
     * The list of strings shall contain one word for each string.
     *
     * @param List of String
     */
    public Anagrams(List<String> list) {
        HashMap<String, String> hm = new HashMap<>();
        for (String word: list) {
            word = word.trim();
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            hm.put(word, new String(chars));
        }
        anagrams = processHashMap(hm);
    }

    /** Constructor to parse an index file to build anagrams datastructure.
     * The index file shall contain one word for each row.
     *
     * @param filename of the index file
     */
    public Anagrams(String filename) throws IOException {
        HashMap<String, String> hm = new HashMap<>();
        LineNumberReader in = new LineNumberReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
        String st;
        while ((st = in.readLine()) != null) {
            String word = st.trim();
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            hm.put(word, new String(chars));
        }
        in.close();
        anagrams = processHashMap(hm);
    }

    /** Returns the number of sets of anagrams.
     * @return number of sets
     */
    public int size() {
        return anagrams.size();
    }

    /** Returns a set of anagrams related to the given word
     *
     * @param word to look for anagrams
     *
     * @return set of anagrams related to word or null if no set was found
     */
    public Set<String> getAnagrams(String word) {
        char[] chars = word.toCharArray();
        Arrays.sort(chars);
        return anagrams.get(new String(chars));
    }

    /** Returns an unmodifiable map of all anagrams
     *
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

    /**
     * Compares this object to the specified object.  The result is true
     * if and only if the argument is not null and is an Anagrams
     * object that represents the same set of anagrams of this object.
     *
     * @param  anObject, the object to compare
     *
     * @return  true if the given object represents an Anagrams
     *          equivalent to this object, false otherwise
     */
    public boolean equals(Object anObject) {
        if (this == anObject)
            return true;
        if (anObject instanceof Anagrams) {
            Anagrams anotherAnagrams = (Anagrams)anObject;
            return anagrams.equals(anotherAnagrams.anagrams);
        }
        return false;
    }

    /**
     * Merge the anagrams of anotherAnagrams
     *
     * @param  anotherAnagrams, the anagrams to merge
     */
    public void merge(Anagrams anotherAnagrams) {
        anotherAnagrams.anagrams.forEach((key, value) -> {
            Set<String> set = anagrams.get(key);
            if (set == null) {//no match
                anagrams.put(key, new HashSet<>(value));//note that value is cloned to new Set
            } else {//match
                set.addAll(value);
            }
        });
    }

    /** Export all anagram sets to file
     * The output file will contain one row for each word with anagrams, each row has the following format: canonical:anagram1|anagram2|...
     *
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

    //internal processing method that returns anagrams as HashMap
    private HashMap<String, Set<String>> processHashMap(HashMap<String, String> hm) {
        HashMap<String, Set<String>> anagrams = new HashMap<>();
        Map<String, String> sorted_index = sortMapByValue(hm);

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
        if (pred != null) {//check for left-over
            if (set.size() > 1)//check if there are at least two words
                anagrams.put(pred, set);
        }
        return anagrams;
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
                System.out.println("Demo test of Anagrams class methods");
                System.out.println("-----------------------------------");

                Anagrams anagrams = new Anagrams(Arrays.asList(new String[]{"file", "life", "none"}));
                Anagrams anagrams2 = new Anagrams();
                anagrams2.merge(anagrams);
                System.out.println("equality:"+anagrams2.equals(anagrams)+", expected value: true");
                anagrams2.merge(new Anagrams(Arrays.asList(new String[]{"polo", "loop", "pool"})));
                System.out.println("equality:"+anagrams2.equals(anagrams)+", expected value: false");

                System.out.println();
                System.out.println("Print all elements in anagrams2:");
                Map<String, Set<String>> anagramsMap = anagrams2.getMap();
                anagramsMap.forEach((key, value) -> System.out.println("key: " + key + ", value: " + value));

                System.out.println();
                anagrams = new Anagrams(args[0]);
                System.out.println("File " + args[0] + " contains " + anagrams.size() + " different sets of anagrams");

                System.out.println("Anagrams of word 'file' :" + anagrams.getAnagrams("file"));

                System.out.println();
                System.out.println("Exporting anagrams to file");
                anagrams.exportFile("anagrams.txt");

            } catch(IOException e) {
                System.err.println(e);
            }
        else {
            System.err.println("usage: java Anagrams <index filename>");
        }
    }

}
