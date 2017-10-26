package com.gailo22;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gailo22.Main40.Graph;

public class Main42 {
	
	static class SymbolGraph {
		
		private Map<String, Integer> nodes;
		private Map<Integer, String> keys;
		private Graph G;
		
		public SymbolGraph(List<String> input) {
			nodes = new HashMap<>();
			keys = new HashMap<>();
			
			for (int i=0; i<input.size(); i++) {
				String[] split = input.get(i).split(" ");
				for (int j=0; j<split.length; j++) {
					if (!nodes.containsKey(split[j])) {
						nodes.put(split[j], nodes.size());
					}
				}
			}
			
			for (String name : nodes.keySet()) {
				keys.put(nodes.get(name), name);
			}
			
			G = new Graph(nodes.size());
			for (int i=0; i<input.size(); i++) {
				String[] split = input.get(i).split(" ");
				int v = nodes.get(split[0]);
				int w = nodes.get(split[1]);
				G.addEdge(v, w);
			}
		}
		
		public boolean contains(String s) {
			return nodes.containsKey(s);
		}
		
		public int index(String s) {
			return nodes.get(s);
		}
		
		public String name(int v) {
			return keys.get(v);
		}
		
		public Graph G() {
			return G;
		}
		
	}
	
	public static void main(String[] args) {
		//           A
		//          / \ 
		//         B   D
		//         \    \
		//          E -- C
		//          |
		//          F 
		List<String> input = Arrays.asList("A B", "C D", "E F", "A D", "C E", "B E");
		SymbolGraph sg = new SymbolGraph(input);
        Graph G = sg.G();
        int s = sg.index("E");
        System.out.println(sg.name(s) + "->");
        for (int v : G.adj(s)) {
            System.out.println("   " + sg.name(v));
        }
	}

}
