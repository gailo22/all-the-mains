package com.gailo22;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Sample Graph implementation
 */
public class Main33 {
	
	static class Graph<E> {
		Map<E, Node<E>> nodes = new HashMap<>();
		
		public boolean addNode(E e) {
			if (nodes.containsKey(e)) return false;
			
			nodes.put(e, new Node<>(e));
			return true;
		}
		
		public Node<E> getNode(E e) {
			if (!nodes.containsKey(e)) 
				throw new RuntimeException(String.format("Node [%s] does not exists", e));
			
			return nodes.get(e);
		}
		
		public void addEdge(E source, E destination) {
			Node<E> s = getNode(source);
			Node<E> d = getNode(destination);
			s.neighbors.add(d);
		}
	}
	
	static class Node<E> {
		E elem;
		List<Node<E>> neighbors = new LinkedList<>();
		
		public Node(E elem) {
			this.elem = elem;
		}
		
		public E getElem() {
			return elem;
		}
		
		public List<Node<E>> getNeighbors() {
			return neighbors;
		}
	}
	
	public static void main(String[] args) {
		//        A
		//       / \
		//      B   C -- D
		//      | /
		//      E
		Graph<String> g = new Graph<>();
		g.addNode("A");
		g.addNode("B");
		g.addNode("C");
		g.addNode("D");
		g.addNode("E");
		
		g.addEdge("A", "B");
		g.addEdge("A", "C");
		g.addEdge("C", "D");
		g.addEdge("B", "E");
		g.addEdge("C", "E");
		
		dfs(g, "A");
		System.out.println();
		bfs(g, "A");
	}

	private static void dfs(Graph<String> g, String start) {
		Set<String> visited = new HashSet<>();
		Node<String> startNode = g.getNode(start);
		dfs(startNode, visited);
	}
	
	private static void dfs(Node<String> startNode, Set<String> visited) {
		String elem = startNode.getElem();
		visited.add(elem);
		
		System.out.print(elem + " -> ");
		
		for (Node<String> neighbor : startNode.getNeighbors()) {
			if (!visited.contains(neighbor.getElem())) {
				dfs(neighbor, visited);
			}
		}
	}

	private static void bfs(Graph<String> g, String start) {
		Set<String> visited = new HashSet<>();
		Queue<String> q = new LinkedList<>();
		
		q.add(start);
		
		while (!q.isEmpty()) {
			String elem = q.remove();
			
			if (visited.contains(elem)) continue;
			visited.add(elem);
			
			System.out.print(elem + " -> ");
			
			Node<String> node = g.getNode(elem);
			for (Node<String> neighbor : node.getNeighbors()) {
				if (!visited.contains(neighbor.getElem())) {
					q.add(neighbor.getElem());
				}
			}
		}
	}

}
