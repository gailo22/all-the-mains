package com.gailo22;

import java.util.LinkedList;
import java.util.List;

public class Main43 {
	
	static class EdgeWeightedDigraph {
		private final int V;
		private int E;
		private List<DirectedEdge>[] adj;
		
		public EdgeWeightedDigraph(int V) {
			if (V < 0) throw new RuntimeException("Number of vertices must be nonnegative");
			this.V = V;
			this.E = 0;
			this.adj = (LinkedList<DirectedEdge>[]) new LinkedList[V];
			for (int i=0; i<V; i++) {
				this.adj[i] = new LinkedList<>();
			}
		}
		
		public EdgeWeightedDigraph(int V, int E) {
			this(V);
			if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
			for (int i=0; i<E; i++) {
				int v = (int) (Math.random() * V);
				int w = (int) (Math.random() * V);
				double weight = Math.round(100 * Math.random()) / 100.0;
				DirectedEdge e = new DirectedEdge(v, w, weight);
				addEdge(e);
			}
		}
		
		public int V() {
			return V;
		}
		
		public int E() {
			return E;
		}
		
		public void addEdge(DirectedEdge e) {
			int v = e.from();
			adj[v].add(e);
			E++;
		}
		
		public List<DirectedEdge> adj(int v) {
			return adj[v];
		}
		
		public List<DirectedEdge> edges() {
			List<DirectedEdge> list = new LinkedList<>();
			for (int v=0; v<V; v++) {
				for (DirectedEdge e : adj(v)) {
					list.add(e);
				}
			}
			return list;
		}
		
		public int outdegree(int v) {
			return adj[v].size();
		}
		
		public String toString() {
	        String NEWLINE = System.getProperty("line.separator");
	        StringBuilder s = new StringBuilder();
	        s.append(V + " " + E + NEWLINE);
	        for (int v = 0; v < V; v++) {
	            s.append(v + ": ");
	            for (DirectedEdge e : adj[v]) {
	                s.append(e + "  ");
	            }
	            s.append(NEWLINE);
	        }
	        return s.toString();
	    }
	}
	
	static class DirectedEdge {
		private final int v;
		private final int w;
		private final double weight;
		
		public DirectedEdge(int v, int w, double weight) {
			this.v = v;
			this.w = w;
			this.weight = weight;
		}
		
		public int from() {
			return v;
		}
		
		public int to() {
			return w;
		}
		
		public double weight () {
			return weight;
		}

	    public String toString() {
	        return v + "->" + w + " " + String.format("%5.2f", weight);
	    }
		
	}
	
	public static void main(String[] args) {
	    DirectedEdge e = new DirectedEdge(12, 23, 3.14);
        System.out.println(e);
        
        System.out.println("---------");
        
        EdgeWeightedDigraph g = new EdgeWeightedDigraph(5, 10);
        System.out.println(g);
	}

}
