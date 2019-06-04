package com.gailo22;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main90 {

    public static void main(String[] args) {

    }
}

class MergeNames {

    public static String[] uniqueNames(String[] names1, String[] names2) {
        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(names1));
        set.addAll(Arrays.asList(names2));

        return set.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] names1 = new String[]{"Ava", "Emma", "Olivia"};
        String[] names2 = new String[]{"Olivia", "Sophia", "Emma"};
        System.out.println(String.join(", ", MergeNames.uniqueNames(names1, names2))); // should print Ava, Emma, Olivia, Sophia
    }
}

class Palindrome {
    public static boolean isPalindrome(String word) {
        if (word == null || word.length() == 0) return false;

        int i = 0;
        int j = word.length() - 1;
        String lowerCaseWord = word.toLowerCase();
        while (i < j) {
            if (lowerCaseWord.charAt(i++) != lowerCaseWord.charAt(j--)) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println(Palindrome.isPalindrome("Deleveled"));
    }
}


class Node90 {
    public int value;
    public Node90 left, right;

    public Node90(int value, Node90 left, Node90 right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}

class BinarySearchTree {
    public static boolean contains(Node90 root, int value) {
        if (root == null) return false;

        if (root.value > value) {
            return contains(root.left, value);
        } else if (root.value < value) {
            return contains(root.right, value);
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        Node90 n1 = new Node90(1, null, null);
        Node90 n3 = new Node90(3, null, null);
        Node90 n2 = new Node90(2, n1, n3);

        System.out.println(contains(n2, 3));
    }
}

class Song {
    private String name;
    private Song nextSong;

    public Song(String name) {
        this.name = name;
    }

    public void setNextSong(Song nextSong) {
        this.nextSong = nextSong;
    }

    public boolean isRepeatingPlaylist() {
        Set<String> names = new HashSet<>();
        Song song = this;
        names.add(song.name);
        while (song.nextSong != null) {
            String nextName = song.nextSong.name;
            if (names.contains(nextName)) return true;
            names.add(nextName);
            song = song.nextSong;
        }
        return false;
    }

    public static void main(String[] args) {
        Song first = new Song("Hello");
        Song second = new Song("Eye of the tiger");

        first.setNextSong(second);
        second.setNextSong(first);

        System.out.println(first.isRepeatingPlaylist());
    }
}

class UserInput {

    public static class TextInput {
        StringBuilder s = new StringBuilder();

        public void add(char c) {
            s.append(c);
        }

        public String getValue() {
            return s.toString();
        }
    }

    public static class NumericInput extends TextInput {

        public void add(char c) {
            if (Character.isDigit(c)) {
                super.add(c);
            }
        }
    }

    public static void main(String[] args) {
        //TextInput input = new NumericInput();
        //input.add('1');
        //input.add('a');
        //input.add('0');
        //System.out.println(input.getValue());
    }
}


class TwoSum {
    public static int[] findTwoSum(int[] list, int sum) {
        int[] result = new int[2];
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < list.length; i++) {
            map.put(list[i], i);
        }
        for (int i = 0; i < list.length; i++) {
            int target = sum - list[i];
            if (map.containsKey(target) && map.get(target) != i) {
                result[0] = i;
                result[1] = map.get(target);
                return result;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        int[] indices = findTwoSum(new int[]{3, 1, 5, 7, 5, 9}, 10);
        if (indices != null) {
            System.out.println(indices[0] + " " + indices[1]);
        }
    }
}

class SortedSearch {
    public static int countNumbers(int[] sortedArray, int lessThan) {
        //int i = Arrays.binarySearch(sortedArray, lessThan);
        //return i < 0 ? (-i) - 1 : i;

        // return (int) Arrays.stream(sortedArray).filter(x -> x < lessThan).count(); // slow

        if (sortedArray == null || sortedArray.length == 0) return 0;
        if (sortedArray[0] >= lessThan) return 0;

        return countNumbers(sortedArray, 0, sortedArray.length - 1, lessThan);
    }

    static int countNumbers(int[] sortedArray, int start, int end, int lessThan) {
        if (start <= end) {
            int mid = start + (end - start) / 2;
            if (sortedArray[mid] > lessThan) {
                return countNumbers(sortedArray, start, mid - 1, lessThan);
            } else if (sortedArray[mid] < lessThan) {
                return countNumbers(sortedArray, mid + 1, end, lessThan);
            } else {
                return mid;
            }
        }

        return start;
    }

    public static void main(String[] args) {
        System.out.println(SortedSearch.countNumbers(new int[]{1, 3, 5, 7}, 4));
    }
}

class TrainComposition {

    private Deque<Integer> trains = new ArrayDeque<>();

    public void attachWagonFromLeft(int wagonId) {
        trains.addFirst(wagonId);
    }

    public void attachWagonFromRight(int wagonId) {
        trains.addLast(wagonId);
    }

    public int detachWagonFromLeft() {
        return trains.removeFirst();
    }

    public int detachWagonFromRight() {
        return trains.removeLast();
    }

    public static void main(String[] args) {
        TrainComposition tree = new TrainComposition();
        tree.attachWagonFromLeft(7);
        tree.attachWagonFromLeft(13);
        System.out.println(tree.detachWagonFromRight()); // 7
        System.out.println(tree.detachWagonFromLeft()); // 13
    }
}

class Path {
    private String path;

    public Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void cd(String newPath) {
        Stack<String> stack = new Stack<>();
        String[] split = path.split("/");
        for (String p : split) {
            if (!"".equals(p)) {
                stack.push(p);
            }
        }

        String[] split2 = newPath.split("/");
        for (String p : split2) {
            if ("".equals(p)) {
                continue;
            }

            if ("..".equals(p)) {
                stack.pop();
            } else {
                stack.push(p);
            }
        }

        if (stack.isEmpty()) {
            path = "/";
            return;
        }

        Deque<String> result = new ArrayDeque<>();
        while (!stack.isEmpty()) {
            result.addFirst(stack.pop());
        }

        path = "/" + String.join("/", result);
    }

    public static void main(String[] args) {
        Path path = new Path("/a/b/c/d");
        path.cd("../x");
        System.out.println(path.getPath());
    }
}

class DecoratorStream extends OutputStream {
    private OutputStream stream;
    private String prefix;

    public DecoratorStream(OutputStream stream, String prefix) {
        super();
        this.stream = stream;
        this.prefix = prefix;
    }

    @Override
    public void write(int b) throws IOException {
        byte[] result = new byte[4];

        result[0] = (byte) (b >> 24);
        result[1] = (byte) (b >> 16);
        result[2] = (byte) (b >> 8);
        result[3] = (byte) (b);

        write(result, 0, 4);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (prefix != null) {
            stream.write(prefix.getBytes(StandardCharsets.UTF_8));
            prefix = null;
        }
        stream.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public static void main(String[] args) throws IOException {
        byte[] message = new byte[]{0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x2c, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x21};
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DecoratorStream decoratorStream = new DecoratorStream(baos, "First line: ");
            decoratorStream.write(message);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), "UTF-8"))) {
                System.out.println(reader.readLine());  //should print "First line: Hello, world!"
            }
        }
    }
}
