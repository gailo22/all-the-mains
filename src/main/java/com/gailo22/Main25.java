package com.gailo22;


public class Main25 {
	
	static class Node {
		private Node leftChild, rightChild;
		
		public Node(Node leftChild, Node rightChild) {
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
		
		public Node getLeftChild() {
			return this.leftChild;
		}
		
		public Node getRightChild() {
			return this.rightChild;
		}
		
		public int height() {
			Node root = this;
			// don't count the root
			if (root.getLeftChild() == null && root.getRightChild() == null) return 0;
			return Math.max(height(root.getLeftChild()), height(root.getRightChild()));
			
			// count the root
			// return height(root);
		}
		
		private int height(Node node) {
			if (node == null) return 0;
			
			return 1 + Math.max(height(node.getLeftChild()), height(node.getRightChild()));
		}
	}
	
	public static void main(String[] args) {
		Node leaf1 = new Node(null, null);
		Node leaf2 = new Node(null, null);
		Node node = new Node(leaf1, null);
		Node root = new Node(node, leaf2);

		System.out.println(root.height());
	}
}
