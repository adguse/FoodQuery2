package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of a B+ tree to allow efficient access to many different
 * indexes of a large data set. BPTree objects are created for each type of
 * index needed by the program. BPTrees provide an efficient range search as
 * compared to other types of data structures due to the ability to perform
 * log_m N lookups and linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu), Alexander Guse (adguse@wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food
 *        item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes
	// for internal nodes of the tree
	private int branchingFactor;

	/**
	 * Public constructor
	 * 
	 * @param branchingFactor
	 */
	public BPTree(int branchingFactor) {
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
		}
		this.branchingFactor = branchingFactor;
		this.root = new LeafNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void insert(K key, V value) {
		root.insert(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (!comparator.contentEquals(">=") && !comparator.contentEquals("==") && !comparator.contentEquals("<="))
			return new ArrayList<V>();
		List<V> search = root.rangeSearch(key, comparator);
		return search;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}

	/**
	 * This abstract class represents any type of node in the tree This class is a
	 * super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {

		// List of keys
		List<K> keys;

		/**
		 * Package constructor
		 */
		Node() {
			this.keys = new ArrayList<K>();
		}

		/**
		 * Inserts key and value in the appropriate leaf node and balances the tree if
		 * required by splitting
		 * 
		 * @param key
		 * @param value
		 */
		abstract void insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();

		/*
		 * (non-Javadoc)
		 * 
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();

		public String toString() {
			return keys.toString();
		}

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree. This class is a concrete
	 * sub class of the abstract Node class and provides implementation of the
	 * operations required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode() {
			super();
			this.children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			return this.keys.get(0);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			return (children.size() > branchingFactor);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		void insert(K key, V value) {
			// Binary search to find correct child node, and recursive call insert
			int index = Collections.binarySearch(keys, key);
			int childrenIndex = index >= 0 ? index : -index - 1;
			Node child = children.get(childrenIndex);
			child.insert(key, value);

			// Handles if child node overflowed
			if (child.isOverflow()) {
				Node sibling = child.split(); // Sibling to the left

				// Finds correct index of new child and adds to children List
				int index2 = Collections.binarySearch(keys, key);
				int childIndex2 = index2 >= 0 ? index2 : -index2 - 1;
				keys.add(childIndex2, child.getFirstLeafKey());

				// Removes correct key if split was with an InteralNode
				if (child instanceof BPTree.InternalNode) {
					child.keys.remove(0);
				}

				children.add(childIndex2, sibling);

			}
			// Handles if root not was overflowed
			if (root.isOverflow()) {
				Node sibling = split(); // Sibling to the left
				InternalNode parent = new InternalNode(); // New parent node

				// Adds sibling and current node to children List, clears after split
				parent.keys.add(this.getFirstLeafKey());
				this.keys.subList(0, 1).clear();
				parent.children.add(sibling);
				parent.children.add(this);
				root = parent;
			}
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			InternalNode sibling = new InternalNode(); // Create sibling node to the left

			int indexStart = 0; // Beginning index of keys to split
			int indexEnd = (keys.size()) / 2; // End index of keys to split

			// Transfers correct keys and children to new Sibling, removes the values that
			// were moved
			sibling.keys.addAll(keys.subList(indexStart, indexEnd));
			sibling.children.addAll(children.subList(indexStart, indexEnd + 1));
			keys.subList(indexStart, indexEnd).clear();
			children.subList(indexStart, indexEnd + 1).clear();

			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			List<V> searched = null; // Creates new List of values to be returned
			Iterator<K> itr = keys.iterator(); // Iterates through keys of node

			// This will loop through the keys and recursive call with the corrent child
			// node
			int childIndex = 0;
			for (childIndex = 0; childIndex < this.keys.size(); childIndex++) {
				K next = this.keys.get(childIndex); // Key being looked at
				if (key.compareTo(next) == -1) {
					Node child = this.children.get(childIndex);
					searched = child.rangeSearch(key, comparator);
					return searched;
				} else if (key.compareTo(next) == 0) {
					if ((this.keys.size() != childIndex + 1) && (this.keys.get(childIndex + 1).equals(key))) {
						continue;
					}
					Node child = this.children.get(childIndex + 1);
					searched = child.rangeSearch(key, comparator);
					return searched;
				}
			}

			// Handles if the key is greater then all keys
			Node child = this.children.get(childIndex);
			searched = child.rangeSearch(key, comparator);
			return searched;
		}

	} // End of class InternalNode

	/**
	 * This class represents a leaf node of the tree. This class is a concrete sub
	 * class of the abstract Node class and provides implementation of the
	 * operations that required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		LeafNode previous;

		/**
		 * Package constructor
		 */
		LeafNode() {
			super();
			values = new ArrayList<V>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			return this.keys.get(0);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			if (values.size() > branchingFactor - 1) {
				return true;
			}
			return false;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		void insert(K key, V value) {
			// Binary search to find the correct place to insert the key/value pair
			int index = Collections.binarySearch(keys, key);
			int valueIndex = index >= 0 ? index : -index - 1;
			keys.add(valueIndex, key);
			values.add(valueIndex, value);

			// Handles the first time when a leafNode is the root and it overflows
			if (root.isOverflow()) {
				Node sibling = split(); // Node of left sibling
				InternalNode parent = new InternalNode(); // Node of new parent
				parent.keys.add(this.getFirstLeafKey());
				parent.children.add(sibling);
				parent.children.add(this);
				root = parent;
			}

		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			LeafNode sibling = new LeafNode(); // New sibling node to be created
			int indexStart = 0; // Starting index of keys and values that are split
			int indexEnd = keys.size() / 2; // Ending index of keys and values that are split

			// All correct keys and values will be moved to the sibling node and removed
			// from the current node
			sibling.keys.addAll(keys.subList(indexStart, indexEnd));
			sibling.values.addAll(values.subList(indexStart, indexEnd));
			keys.subList(indexStart, indexEnd).clear();
			values.subList(indexStart, indexEnd).clear();

			// Reassign next and previous fields for current and sibling node
			if (this.previous != null) {
				sibling.previous = this.previous;
				this.previous.next = sibling;
				sibling.next = this;
				this.previous = sibling;
			} else {
				sibling.previous = this.previous;
				sibling.next = this;
				this.previous = sibling;
			}

			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(Comparable, String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			List<V> built = new LinkedList<V>(); // List of values searched for

			// Switch to find which operator the user asked for
			switch (comparator) {
			// "<=" case finds the first key that is <= the given key, then builds this
			// going through each previous node. Will recursively call this method on
			// previous nodes. Builds a list with the correct values.
			case "<=":
				for (int i = keys.size() - 1; i >= 0; i--) {
					if (keys.get(i).compareTo(key) <= 0) {
						built.add(values.get(i));
					}
				}
				if (this.previous != null) {
					Node prev = this.previous;
					built.addAll(prev.rangeSearch(key, comparator));
				}
				break;
			// "==" case finds the first key that is == the given key, then builds the list
			// going through each previous node. Will recursively call this method on
			// previous nodes. Terminates once key is found that doesn't equal the given
			// key. Builds a list will the correct values.
			case "==":
				for (int i = keys.size() - 1; i >= 0; i--) {
					if (key.compareTo(keys.get(i)) == 0) {
						built.add(values.get(i));
					}
					if (key.compareTo(keys.get(i)) == 1) {
						return built;
					}
				}
				if (this.previous != null) {
					Node prev = this.previous;
					built.addAll(prev.rangeSearch(key, comparator));
				}
				break;
			// ">=" case will first get all keys == the given key, then continues forward
			// looking at the next nodes. Builds a list will the correct values.
			case ">=":
				if (keys.get(0).compareTo(key) == 0) {
					if (this.previous != null) {
						built.addAll(this.previous.rangeSearch(key, "=="));
					}
				}
				for (int i = 0; i < this.keys.size(); i++) {
					if (keys.get(i).compareTo(key) >= 0) {
						built.add(values.get(i));
					}
				}
				if (this.next != null) {
					Node next = this.next;
					built.addAll(next.rangeSearch(key, comparator));
				}
				break;
			}// changes
			return built;
		}

	} // End of class LeafNode

	/**
	 * Contains a basic test scenario for a BPTree instance. It shows a simple
	 * example of the use of this class and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Double, Double> bpTree = new BPTree<>(3);

		// create a pseudo random number generator
		Random rnd1 = new Random();

		// some value to add to the BPTree
		Double[] dd = { 10d, 9d, 8d, 14d, 12d, 3d, 7d, 11d };

		// build an ArrayList of those value and add to BPTree also
		// allows for comparing the contents of the ArrayList
		// against the contents and functionality of the BPTree
		// does not ensure BPTree is implemented correctly
		// just that it functions as a data structure with
		// insert, rangeSearch, and toString() working.
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < dd.length; i++) {
			Double j = dd[i];
			list.add(j);
			bpTree.insert(j, j);
			System.out.println("\n\nTree structure:\n" + bpTree.toString());
		}
		// System.out.println(bpTree.toString());
		List<Double> filteredValues = bpTree.rangeSearch(3d, ">=");
		System.out.println("Filtered values: " + filteredValues.toString());
	}

} // End of class BPTree
	// insert fine
