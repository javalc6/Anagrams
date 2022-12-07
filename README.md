# Anagrams
The Java class Anagrams implements utility methods to look for anagrams of a given index of words.
It is designed to be used from other programs. It includes standalone demo of features.

There are several algorithms to check anagrams. This class use a fast algorithm based on sorting the characters of each string and comparing the sorted characters.
Considering strings s1 and s2, here is the algorithm to chech if s1 and s2 are anagrams of each other:
```
    char[] chars1 = s1.toCharArray();
    Arrays.sort(chars1);
    char[] chars2 = s2.toCharArray();
    Arrays.sort(chars2);
    System.out.println("s1 and s2 are anagrams of each other: " + Arrays.equals(chars1, chars2));
```

# Compile
Run following command to build the application:
```
javac Anagrams.java
```

# Run
Just use the following command to run the demo:
```
java Anagrams index_en.txt
```

