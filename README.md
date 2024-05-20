Java application to parse a dictionary file and identify groups of words with the highest number of anagrams.
MostAnagramsFinder.java shows the code.
Command line args:
args[0] is the path to the file you wish to parse.
args[0] must be a .txt file.
I've included dictionary.txt and words.txt to use as a demo.
args[1] is the data structure the user wishes to use to sort and organize the words.
args[1] must be either string 'rbt' for a red-black tree, 
'bst' for a binary search tree,
or 'hash' for a hash map.
BSTreeMap, RBTreeMap, and MyHashMap are the custom map implementations. MyMap is the generic. 
