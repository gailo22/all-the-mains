package com.gailo22;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main83 {

    public static void main(String[] args) {
        Node root = new Node("root");
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        Node d = new Node("d");
        Node e = new Node("e");
        Node f = new Node("f");

        a.getNodes().add(d);
        a.getNodes().add(e);
        c.getNodes().add(f);

        root.getNodes().add(a);
        root.getNodes().add(b);
        root.getNodes().add(c);

        print(root);
//        └── root
//            ├── a
//            │   ├── d
//            │   └── e
//            ├── b
//            └── c
//                └── f

    }

    private static void printNodes(Node root) {
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);

        System.out.println(root.getName());
        while (!queue.isEmpty()) {
            Node node = queue.pop();
            node.getNodes().forEach(x -> {
                System.out.printf("%s ", x.getName());
                queue.add(x);
            });

            System.out.println("");
        }
    }

    private static void print(Node node) {
        print(node, "", true);
    }

    private static void print(Node node, String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + node.getName());
        List<Node> children = node.getNodes();
        for (int i = 0; i < children.size() - 1; i++) {
            print(children.get(i), prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            print(children.get(children.size() - 1), prefix + (isTail ?"    " : "│   "), true);
        }
    }

}

@Data
class Node {
    private String name;
    private List<Node> nodes = new ArrayList<>();

    public Node(String name) {
        this.name = name;
    }

}
