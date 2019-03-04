package com.gailo22;

import io.atlassian.fugue.Pair;

import java.util.*;

import static com.gailo22.Main85.Util.swap;

public class Main85 {

    public static void main(String[] args) {

        int[] a = new int[]{1, 2, 3, 4, 5};
        int[] b = new int[]{1, 2, 8, 4, 5};

        List<Integer> intersects = Other.intersects(a, b);
        System.out.println(intersects);

        int[] ints = new int[]{3, 1, 2, 5, 4};
        BubbleSort.bubbleSort(ints);
        System.out.println(Arrays.toString(ints));

        int[] sortedInts = new int[]{1, 2, 3, 4, 5};
        System.out.println(BinarySearch.binarySearch(5, sortedInts));

    }

    static class DijkstraWithPQ {
        private class Edge {
            int v;
            int weight;

            public Edge(int v, int weight) {
                this.v = v;
                this.weight = weight;
            }
        }

        private class Vertex {
            int u;
            int distance;
            boolean isVisited;

            public Vertex(int u, int distance) {
                this.u = u;
                this.distance = distance;
            }
        }

        private class Path {
            int from, to, distance;
            List<Integer> path;

            public Path(int from, int to, int distance, List<Integer> path) {
                this.from = from;
                this.to = to;
                this.distance = distance;
                this.path = path;
            }
        }

        private class Node implements Comparable<Node> {
            int u, distance;

            public Node(int u, int distance) {
                this.u = u;
                this.distance = distance;
            }

            @Override
            public int compareTo(Node o) {
                return (int) Math.signum(distance - o.distance);
            }
        }

        List<Edge>[] adj;

        public DijkstraWithPQ(int nodes) {
            this.adj = new ArrayList[nodes];
            for (int i = 0; i < nodes; i++) {
                this.adj[i] = new ArrayList<>();
            }
        }

        public void addEdge(int u, int v, int weight) {
            adj[u].add(new Edge(v, weight));
        }

        public Path[] dijkstra(int source) {
            Vertex[] vertices = new Vertex[adj.length];
            int[] parent = new int[adj.length];
            for (int i = 0; i < adj.length; i++) {
                Vertex v = new Vertex(i, Integer.MAX_VALUE);
                vertices[i] = v;
                parent[i] = -1;
            }

            vertices[source].distance = 0;
            PriorityQueue<Node> pq = new PriorityQueue<>();
            pq.add(new Node(source, 0));

            while (!pq.isEmpty()) {
                Node v = pq.remove();
                if (!vertices[v.u].isVisited) {
                    vertices[v.u].isVisited = true;
                    for (Edge e : adj[v.u]) {
                        Vertex next = vertices[e.v];
                        if (v.distance + e.weight < next.distance) {
                            next.distance = v.distance + e.weight;
                            parent[next.u] = v.u;
                            pq.add(new Node(next.u, next.distance));
                        }
                    }
                }
            }

            Path[] paths = new Path[adj.length];
            for (int i = 0; i < adj.length; i++) {
                List<Integer> path = new ArrayList<>();
                Path p = new Path(source, i, vertices[i].distance, path);
                int j = i;
                while (parent[j] != -1) {
                    path.add(0, j);
                    j = parent[j];
                }
                paths[i] = p;
            }

            return paths;
        }

    }

    static class AdjacencyMatrixGraph {
        int[][] adj;

        public AdjacencyMatrixGraph(int nodes) {
            this.adj = new int[nodes][nodes];
        }

        public void addEdge(int u, int v) {
            this.adj[u][v] = 1;
        }
    }

    static class AdjacencyListWeightedGraph {

        private class Edge {
            int u, v, weight;

            public Edge(int u, int v, int weight) {
                this.u = u;
                this.v = v;
                this.weight = weight;
            }
        }

        ArrayList<Edge>[] adj;

        public AdjacencyListWeightedGraph(int nodes) {
            this.adj = new ArrayList[nodes];
            for (int i = 0; i < nodes; i++) {
                this.adj[i] = new ArrayList<>();
            }
        }

        public void addEdge(int u, int v, int weight) {
            adj[u].add(new Edge(u, v, weight));
        }
    }

    static class AdjacencyListGraph {
        ArrayList<Integer>[] adj;

        public AdjacencyListGraph(int nodes) {
            this.adj = new ArrayList[nodes];
            for (int i = 0; i < nodes; i++) {
                this.adj[i] = new ArrayList<>();
            }
        }

        public void addEdge(int u, int v) {
            adj[u].add(v);
        }
    }

    static class SimpleBinaryTree<K, V> implements BinaryTree<K, V> {
        private BinaryTreeNode<K, V> root;

        @Override
        public void put(K key, V value) {
            if (root == null) {
                root = new BinaryTreeNode<>(key, value);
            } else {
                put(key, value, root);
            }
        }

        private void put(K key, V value, BinaryTreeNode<K, V> root) {
            // TODO:
        }

        @Override
        public Optional<V> get(K key) {
            return Optional.ofNullable(root).flatMap(n -> get(key, n));
        }

        private Optional<V> get(K key, BinaryTreeNode<K,V> node) {
            if (((Comparable) key).compareTo(node.getKey()) == 0) {
                return Optional.of(node.value);
            } else if (((Comparable) key).compareTo(node.getKey()) < 0) {
                return node.getLeft().flatMap(n -> get(key, n));
            } else {
                return node.getRight().flatMap(n -> get(key, n));
            }
        }

        public void rightRotate(BinaryTreeNode<K, V> nodeX, BinaryTreeNode<K, V> parent) {
            BinaryTreeNode<K, V> nodeY = nodeX.getLeft().get();
            nodeX.setLeft(nodeY.getRight().orElse(null));
            if (parent == null) {
                this.root = nodeY;
            } else if (parent.getRight().filter(n -> n == nodeX).isPresent()) {
                parent.setRight(nodeY);
            } else {
                parent.setLeft(nodeY);
            }
            nodeY.setRight(nodeX);
        }

        public Optional<K> minKey() {
            return Optional.ofNullable(root).map(this::minKey);
        }

        public K minKey(BinaryTreeNode<K, V> node) {
            return node.getLeft().map(this::minKey).orElse(node.getKey());
        }
    }

    interface BinaryTree<K, V> {
        void put(K key, V value);

        Optional<V> get(K key);
    }

    static class BinaryTreeNode<K, V> {
        private BinaryTreeNode<K, V> left;
        private BinaryTreeNode<K, V> right;
        private K key;
        private V value;

        public BinaryTreeNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Optional<BinaryTreeNode<K, V>> getLeft() {
            return Optional.ofNullable(left);
        }

        public Optional<BinaryTreeNode<K, V>> getRight() {
            return Optional.ofNullable(right);
        }

        public void setLeft(BinaryTreeNode<K, V> left) {
            this.left = left;
        }

        public void setRight(BinaryTreeNode<K, V> right) {
            this.right = right;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    static class ChainedHashtable<K, V> implements HashTable<K, V> {
        private final HashProvider<K> hashProvider;
        private java.util.LinkedList<Pair<K, V>>[] array;

        public ChainedHashtable(int capacity, HashProvider<K> hashProvider) {
            array = new java.util.LinkedList[capacity];
            for (int i = 0; i < capacity; i++) {
                array[i] = new java.util.LinkedList<>();
            }
            this.hashProvider = hashProvider;
        }

        @Override
        public void push(K key, V value) {
            int hashValue = hashProvider.hashKey(key, array.length);
            array[hashValue].addFirst(new Pair<>(key, value));
        }

        @Override
        public Optional<V> get(K key) {
            int hashValue = hashProvider.hashKey(key, array.length);
            return array[hashValue].stream()
                    .filter(keyValue -> keyValue.left().equals(key))
                    .findFirst()
                    .map(Pair::right);
        }

        @Override
        public void remove(K key) {
            int hashValue = hashProvider.hashKey(key, array.length);
            array[hashValue].removeIf(p -> p.left().equals(key));
        }
    }

    static class MultiplicationHashing implements HashProvider<Integer> {
        private double k;

        public MultiplicationHashing(double k) {
            this.k = k;
        }

        @Override
        public int hashKey(Integer key, int tableSize) {
            return (int) (tableSize * (k * key % 1));
        }
    }

    interface HashProvider<K> {
        int hashKey(K key, int tableSize);
    }

    interface HashTable<K, V> {
        void push(K key, V value);

        Optional<V> get(K key);

        void remove(K key);
    }

    static class Stack<V> {
        private LinkedListNode<V> head;

        public void push(V item) {
            head = new LinkedListNode<>(item, head);
        }

        public Optional<V> pop() {
            Optional<LinkedListNode<V>> node = Optional.ofNullable(this.head);
            head = node.flatMap(LinkedListNode::getNext).orElse(null);
            return node.map(LinkedListNode::getValue);
        }
    }

    static class StackArray<V> {
        private V[] array;
        private int headPtr = 0;

        public StackArray(int capacity) {
            array = (V[]) new Object[capacity];
        }

        public void push(V item) {
            array[headPtr++] = item;
        }

        public Optional<V> pop() {
            if (headPtr > 0) return Optional.of(array[--headPtr]);
            else return Optional.empty();
        }
    }

    static class Queue<V> {
        private DblLinkedListNode<V> head;
        private DblLinkedListNode<V> tail;

        public Queue() {
            this.head = null;
            this.tail = null;
        }

        public void enqueue(V item) {
            DblLinkedListNode<V> node = new DblLinkedListNode<>(item, null, tail);
            Optional.ofNullable(tail).ifPresent(n -> n.setNext(node));
            tail = node;
            if (head == null) head = node;
        }

        public Optional<V> dequeue() {
            Optional<DblLinkedListNode<V>> node = Optional.ofNullable(this.head);
            head = node.flatMap(DblLinkedListNode::getNext).orElse(null);
            Optional.ofNullable(head).ifPresent(n -> n.setPrev(null));
            if (head == null) tail = null;
            return node.map(DblLinkedListNode::getValue);
        }
    }

    static class LinkedList<V> {
        private LinkedListNode<V> head;

        public LinkedList() {
            head = null;
        }

        public void setFront(V item) {
            this.head = new LinkedListNode<>(item, head);
        }

        public void deleteFront() {
            Optional<LinkedListNode<V>> firstNode = Optional.ofNullable(this.head);
            this.head = firstNode.flatMap(LinkedListNode::getNext).orElse(null);
            firstNode.ifPresent(n -> n.setNext(null));
        }

        public Optional<LinkedListNode<V>> find(V item) {
            Optional<LinkedListNode<V>> node = Optional.ofNullable(this.head);
            while (node.filter(n -> n.getValue() != item).isPresent()) {
                node = node.flatMap(LinkedListNode::getNext);
            }
            return node;
        }

        public void addAfter(LinkedListNode<V> aNode, V item) {
            aNode.setNext(new LinkedListNode<>(item, aNode.getNext().orElse(null)));
        }
    }

    static class DblLinkedListNode<V> {
        private V value;
        private DblLinkedListNode<V> next;
        private DblLinkedListNode<V> prev;

        public DblLinkedListNode(V value, DblLinkedListNode<V> next, DblLinkedListNode<V> prev) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }

        public Optional<DblLinkedListNode<V>> getNext() {
            return Optional.ofNullable(next);
        }

        public Optional<DblLinkedListNode<V>> getPrev() {
            return Optional.ofNullable(prev);
        }

        public V getValue() {
            return value;
        }

        public DblLinkedListNode<V> setValue(V value) {
            this.value = value;
            return this;
        }

        public DblLinkedListNode<V> setNext(DblLinkedListNode<V> next) {
            this.next = next;
            return this;
        }

        public DblLinkedListNode<V> setPrev(DblLinkedListNode<V> prev) {
            this.prev = prev;
            return this;
        }
    }

    static class LinkedListNode<V> {
        private V value;
        private LinkedListNode<V> next;

        public LinkedListNode(V value, LinkedListNode<V> next) {
            this.value = value;
            this.next = next;
        }

        public Optional<LinkedListNode<V>> getNext() {
            return Optional.ofNullable(next);
        }

        public V getValue() {
            return value;
        }

        public LinkedListNode<V> setValue(V value) {
            this.value = value;
            return this;
        }

        public LinkedListNode<V> setNext(LinkedListNode<V> next) {
            this.next = next;
            return this;
        }
    }

    static class MergeSort {
        public void mergeSort(int[] array, int start, int end) {
            if (start < end) {
                int middle = (end - start) / 2 + start;
                mergeSort(array, start, middle);
                mergeSort(array, middle + 1, end);
                merge(array, start, middle, end);
            }
        }

        private void merge(int[] array, int start, int middle, int end) {
            int i = start;
            int j = middle + 1;
            int[] arrayTemp = new int[end - start + 1];
            for (int k = 0; k < arrayTemp.length; k++) {
                if (i <= middle && (j > end || array[i] == array[j])) {
                    arrayTemp[k] = array[i];
                    i++;
                } else {
                    arrayTemp[k] = array[j];
                    j++;
                }
            }
            System.arraycopy(arrayTemp, 0, array, start, arrayTemp.length);
        }
    }

    static class QuickSort {
        public void sort(int[] numbers) {
            sort(numbers, 0, numbers.length - 1);
        }

        private void sort(int[] numbers, int start, int end) {
            if (start < end) {
                int p = partition(numbers, start, end);
                sort(numbers, start, p - 1);
                sort(numbers, p + 1, end);
            }
        }

        private int partition(int[] numbers, int start, int end) {
            int pivot = numbers[end];
            int x = start - 1;
            for (int i = start; i < end; i++) {
                if (numbers[i] < pivot) {
                    x++;
                    swap(numbers, x, i);
                }
            }
            swap(numbers, x + 1, end);
            return x + 1;
        }
    }


    static class BinarySearch {

        public static boolean binarySearch(int x, int[] sortedInts) {
            return binarySearch(x, sortedInts, 0, sortedInts.length);
        }

        private static boolean binarySearch(int x, int[] sortedInts, int start, int end) {
            if (start < end) {
                int mid = (end - start) / 2 + start;
                if (sortedInts[mid] == x) return true;
                if (sortedInts[mid] > x) {
                    return binarySearch(x, sortedInts, start, mid - 1);
                }
                return binarySearch(x, sortedInts, mid + 1, end);
            }

            return false;

        }
    }

    static class BubbleSort {
        public static void bubbleSort(int[] ints) {
            for (int i = 1; i < ints.length; i++) {
                for (int j = 0; j < ints.length - i; j++) {
                    if (ints[j] > ints[j + 1]) {
                        swap(ints, j, j + 1);
                    }
                }
            }
        }
    }

    static class Other {
        private static List<Integer> intersects(int[] a, int[] b) {
            List<Integer> result = new ArrayList<>();
            for (int x : a) {
                for (int y : b) {
                    if (x == y) result.add(x);
                }
            }
            return result;
        }

        private static int toDecimal(String s) {
            int result = 0;
            int len = s.length();
            for (int i = 0; i < len; i++) {
                if (s.charAt(len - (i + 1)) == '1') {
                    result += Math.pow(2, i);
                }
            }
            return result;
        }
    }

    static class Util {
        public static void swap(int[] arr, int i, int j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}
