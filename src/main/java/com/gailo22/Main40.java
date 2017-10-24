package com.gailo22;

import java.util.LinkedList;
import java.util.List;

public class Main40 {

	static class Graph {
		private final int V;
		private int E;
		private List<Integer>[] adj;

		public Graph(int V) {
			this.V = V;
			this.E = 0;
			this.adj = (LinkedList<Integer>[]) new LinkedList[V];
			for (int i = 0; i < V; i++) {
				adj[i] = new LinkedList<>();
			}
		}

		public Graph(int V, int E) {
			this(V);
			if (E < 0)
				throw new RuntimeException("Number of edges must be nonnegative");
			for (int i = 0; i < E; i++) {
				int v = (int) (Math.random() * V);
				int w = (int) (Math.random() * V);
				addEdge(v, w);
			}
		}

		public void addEdge(int v, int w) {
			adj[v].add(w);
			adj[w].add(v);
			E++;
		}

		public List<Integer> adj(int v) {
			return adj[v];
		}

		public int V() {
			return V;
		}

		public int E() {
			return E;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			String NEWLINE = System.getProperty("line.separator");
			s.append(V + " vertices, " + E + " edges " + NEWLINE);
			for (int v = 0; v < V; v++) {
				s.append(v + ": ");
				for (int w : adj[v]) {
					s.append(w + " ");
				}
				s.append(NEWLINE);
			}
			return s.toString();
		}
	}

	public static void main(String[] args) {
		Graph G = new Graph(5, 10);
		System.out.println(G);
	}

}
