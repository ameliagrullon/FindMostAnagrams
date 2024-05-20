import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Class for parsing through a .txt file and finding group(s) with the most anagrams.
 * @author Amelia Grullon
 * @version 1.0.2 May 7, 2024
 */

public class MostAnagramsFinder {


    /**
     * Main method of the MostAnagramsFinder class.
     * Processes provided dictionary file and data structure, outputs groups of words with most anagrams.
     * @param args - args[0] = path to dictionary file
     *             args[1] = Type of data structure to use for storing and organizing words.
     */
    public static void main(String[] args) {
        //parsing command line arguments
        //if the number of args supplied is incorrect, program prints usage message
        if (args.length != 2) {
            System.err.println("Usage: java MostAnagramsFinder <dictionary file> <bst|rbt|hash>");
            System.exit(1);
        }

        //if number of command line args is correct, program verifies that the
        //dictionary file exists
        String dictionaryFile = args[0];
        File file = new File(dictionaryFile);
        if (args.length == 2) {
            if (!file.exists()) {
                System.err.println("Error: Cannot open file '" + args[0] + "' for input.");
                System.exit(1);
            }
        }

        //parse command line for which data structure the user wishes to use
        //if args[1] isn't any of the possible options, exit in failure
        //if args[1] is a data structure, instantiate map
        String dataStructure = args[1];
        MyMap<String, MyList<String>> map = null;

        if(!Arrays.asList("bst","rbt","hash").contains(dataStructure)){
            System.err.println("Error: Invalid data structure '" + args[1] + "' received.");
            System.exit(1);
        } else {
            map = createMap(dataStructure);
        }

        //attempt to populate map.
        //if failed, return error message.
        try {
            fillMap(dictionaryFile, map);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred reading '" + args[0] + "'.");
            System.exit(1);
        }

        //call function that displays output.
        findMostAnagrams(map);

    }


    /**
     * instantiates map based on data structure chosen by user
     * @param dataStructure the chosen data structure the user decides
     * @return new Map type correlated with data structure or null if user did not type in bst,hash,rbt
     */
    private static MyMap<String, MyList<String>> createMap(String dataStructure) {
        //uses switch-case to designate what kind of map should be created based on input data structure
        switch (dataStructure) {
            case "bst":
                return new BSTreeMap<>();
            case "rbt":
                return new RBTreeMap<>();
            case "hash":
                return new MyHashMap<>();
            default:
                return null;
        }

    }

    /**
     * Populates map with key-value mappings of words (lowercase and alphabetical) and their anagrams
     * @param file string from the dictionary file that will be parsed
     * @param map map of either type bst,rbt,or hash that will be populated
     * @throws IOException
     */
    private static void fillMap(String file, MyMap<String, MyList<String>> map) throws IOException {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                String temp = line.trim().toLowerCase();
                String sortedKey = sortChars(temp);

                MyList<String> anagramsList = map.get(sortedKey);
                if (anagramsList == null) {
                    anagramsList = new MyLinkedList<>();
                    map.put(sortedKey, anagramsList);
                }
                //adds the word in its original form, ensuring those with capitals are preserved
                anagramsList.add(line);
            }
        }
    }

    /**
     * Conducts insertion sort on the CHARACTERS in a WORD lexicographically, works with sortChars to populate map
     * @param arr: the input word in the form of a char array
     */
    private static void insertionSort (char[] arr){
        for (int i = 1; i < arr.length; i++) {
            char key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;

        }
    }

    /**
     * Finalizes creation of key mapping for an input word. Works with insertionsort(char[arr]
     * to ensure that a word becomes its' key by being completely lowercase and in alphabetical order.
     * @param line: word that is taken in to parse into key
     * @return String of the key to be inserted in fillMap
     */
    private static String sortChars(String line){
        //finish creation of key: make lowercase and sort letters alphabetically
        char[] arr = line.toLowerCase().toCharArray();
        insertionSort(arr);
        return new String(arr);
    }

    /**
     * Performs insertionSort on the groups of anagrams to ensure that the
     * words in each group are in alphabetical order
     * with capitalized letters first
     * @param group: list of anagrams in ONE group
     */

    private static void insertionSort(MyList<String> group) {
        for (int i = 1; i < group.size(); i++) {
            String line = group.get(i);
            int j = i - 1;
            while (j >= 0 && group.get(j).compareTo(line) > 0) {
                group.set(j + 1, group.get(j));
                j = j - 1;
            }
            group.set(j + 1, line);
        }
    }

    /**
     * Performs insertionSort on the separate groups of anagrams to ensure that the
     * GROUPS are in alphabetical order relative to each other
     * @param groups: lists of different anagram sets
     */
    private static void groupSort(MyList<MyList<String>> groups) {
        for (int i = 1; i < groups.size(); i++) {
            MyList<String> currentGroup = groups.get(i);
            int j = i - 1;

            // Compare current group's first word with previous groups' first words
            while (j >= 0 && compareFirstWords(currentGroup, groups.get(j)) < 0) {
                groups.set(j + 1, groups.get(j));
                j--;
            }

            groups.set(j + 1, currentGroup);
        }
    }

    /**
     * sortAnagramsGroups helper method:
     * Comparator for comparing groups based on the first word lexicographically
     * @param group1 first group being compared in the while loop
     * @param group2 second group being compared in the while loop
     * @return which group should go ahead of the other based on alphebetization
     */
    private static int compareFirstWords(MyList<String> group1, MyList<String> group2) {
        String word1 = group1.get(0);
        String word2 = group2.get(0);
        return word1.compareToIgnoreCase(word2);
    }


    /**
     * Compares anagram groups to find which group has the highest number of anagrams.
     * Outputs the group, the number of anagrams it contains, and the group itself in the form of a list.
     * In the case of a tie where several groups have the max number of anagrams,
     * the number of groups is displayed, along with their anagram count and the group themselves.
     * @param map - map being parsed to find anagram groups
     */
    private static void findMostAnagrams(MyMap<String, MyList<String>> map){
        int maxAnagramCount = 0; //will become Group count in output
        MyList<MyList<String>> maxAnagramGroups = new MyLinkedList<>(); //will become lists displayed in output

        Iterator<Entry<String, MyList<String>>> entryIterator = map.iterator();
        while (entryIterator.hasNext()) {
            Entry<String, MyList<String>> entry = entryIterator.next();
            MyList<String> anagramsList = entry.value;

            //logic to find group(s) with most anagrams, stored in maxAnagramGroups
            if (anagramsList.size() > 1) {
                int currentAnagramCount = anagramsList.size();

                if(currentAnagramCount > maxAnagramCount){
                    maxAnagramCount = currentAnagramCount;
                    maxAnagramGroups.clear();
                    maxAnagramGroups.add(anagramsList);
                } else if (currentAnagramCount == maxAnagramCount){
                    maxAnagramGroups.add(anagramsList);
                }
            }
        }

        //sorts the different max anagram groups alphabetically
        groupSort(maxAnagramGroups);


        //1/2 of final output
        if(maxAnagramCount == 0){
            System.out.println("No anagrams found.");
        } else {
            System.out.println("Groups: " + maxAnagramGroups.size() + ", Anagram count: " + maxAnagramCount);
        }


        Iterator<MyList<String>> iterator = maxAnagramGroups.iterator();
        //adds the max anagram groups to what will be displayed in the output
        while(iterator.hasNext()){
            MyList<String> group = iterator.next();
            //sorts the words in each group
            insertionSort(group);


            System.out.print("[");
            Iterator<String> wordIterator = group.iterator();
            while(wordIterator.hasNext()){
                System.out.print(wordIterator.next());
                if(wordIterator.hasNext()){
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }

    }


}


