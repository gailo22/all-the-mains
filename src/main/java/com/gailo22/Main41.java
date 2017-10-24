package com.gailo22;

import java.util.LinkedList;
import java.util.List;

public class Main41 {

	static class EdgeWeightedGraph {
		private final int V;
		private int E;
		private List<Edge>[] adj;

		public EdgeWeightedGraph(int V) {
			this.V = V;
			this.E = 0;
			this.adj = (LinkedList<Edge>[]) new LinkedList[V];
			for (int i = 0; i < V; i++) {
				adj[i] = new LinkedList<>();
			}
		}

		public EdgeWeightedGraph(int V, int E) {
			this(V);
			if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
			for (int i = 0; i < E; i++) {
				int v = (int) (Math.random() * V);
				int w = (int) (Math.random() * V);
				double weight = Math.round(100 * Math.random()) / 100.0;
	            Edge e = new Edge(v, w, weight);
	            addEdge(e);
			}
		}

		public void addEdge(Edge e) {
			int v = e.either();
			int w = e.other(v);
			adj[v].add(e);
			adj[w].add(e);
			E++;
		}

		public List<Edge> adj(int v) {
			return adj[v];
		}

		public int V() {
			return V;
		}

		public int E() {
			return E;
		}
		
		public Iterable<Edge> edges() {
			List<Edge> list = new LinkedList<Edge>();
			for (int v = 0; v < V; v++) {
				int selfLoops = 0;
				for (Edge e : adj(v)) {
					if (e.other(v) > v) {
						list.add(e);
					}
					// only add one copy of each self loop
					else if (e.other(v) == v) {
						if (selfLoops % 2 == 0)
							list.add(e);
						selfLoops++;
					}
				}
			}
			return list;
		}

		@Override
		public String toString() {
			String NEWLINE = System.getProperty("line.separator");
	        StringBuilder s = new StringBuilder();
	        s.append(V + " " + E + NEWLINE);
	        for (int v = 0; v < V; v++) {
	            s.append(v + ": ");
	            for (Edge e : adj[v]) {
	                s.append(e + "  ");
	            }
	            s.append(NEWLINE);
	        }
	        return s.toString();
		}
	}

	static class Edge implements Comparable<Edge> {
		private final int v;
		private final int w;
		private final double weight;

		public Edge(int v, int w, double weight) {
			this.v = v;
			this.w = w;
			this.weight = weight;
		}

		public double weight() {
			return weight;
		}

		public int either() {
			return v;
		}

		public int other(int vertex) {
			if (vertex == v) return v;
			else if (vertex == w) return w;
			else throw new RuntimeException("Illegal endpoint");
		}

		public int compareTo(Edge that) {
			if (this.weight() < that.weight()) return -1;
			else if (this.weight() > that.weight()) return +1;
			else return 0;
		}

		public String toString() {
			return String.format("%d-%d %.2f", v, w, weight);
		}
	}

	public static void main(String[] args) {
		EdgeWeightedGraph G = new EdgeWeightedGraph(5, 10);
		System.out.println(G);
	}

}
