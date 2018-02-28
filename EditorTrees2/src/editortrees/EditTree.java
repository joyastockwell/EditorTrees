package editortrees;

import editortrees.Node.Wrap;

/**
 * 
 * A height-balanced binary tree with rank that could be the basis for a text
 * editor.
 *
 * @author Joy Stockwell, Sterling Hayden, and Brison Mondry. Created January 4,
 *         2018.
 */
public class EditTree {

	private Node root;
	private int rotationCount;

	/**
	 * Constructs an empty tree
	 */
	public EditTree() {
		this.root = Node.getNull();
		this.rotationCount = 0;
	}

	/**
	 * Construct a single-node tree
	 * 
	 * @param ch,
	 *            the element in the single node
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
		this.rotationCount = 0;
	}

	/**
	 * Makes a copy of tree e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e,
	 *            tree to be copied
	 */
	public EditTree(EditTree e) {
		this.rotationCount = 0;
		if (e.root instanceof Null_Node) {
			this.root = Node.getNull();
		} else
			this.root = new Node(e.root);
	}

	/**
	 * Creates an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the size of the tree (note that repeatedly calling insert()
	 * would be O(N log N), so addFromString is called instead. Note that
	 * toString() is inorder.
	 * 
	 * @param s,
	 *            the string that the created tree will output when its
	 *            toString() is called
	 */
	public EditTree(String s) {
		this.rotationCount = 0;
		this.root = Node.addFromString(s);
	}

	/**
	 * Creates an EditTree whose root is the specified node, complete with any
	 * subtree the specified node might have
	 *
	 * @param node,
	 *            the node that is the root of this EditTree
	 */
	public EditTree(Node node) {
		this.rotationCount = 0;
		this.root = node;
	}

	/**
	 * Returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.rotationCount;
	}

	/**
	 * Returns the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.root.toString(sb);
		return sb.toString();
	}

	/**
	 * This one asks for more info from each node. You can write it like the
	 * arraylist-based toString() method from the BinarySearchTree assignment.
	 * However, the output isn't just the elements, but the elements, ranks, and
	 * balance codes.For the tree with root b and children a and c, it should
	 * return the string: [b1=, a0=, c0=]
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		this.root.toDebugString(sb);
		if (sb.length() > 2)
			sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Adds a node whose element is ch to the end of the EditTree.
	 * 
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		Wrap wrap = this.root.add(ch);
		this.root = wrap.n;
		this.rotationCount += wrap.rotations;
	}

	/**
	 * Adds a node whose element is ch to the specified place in the EditTree
	 * 
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             if pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		Wrap wrap = this.root.addAt(ch, pos);
		this.root = wrap.n;
		this.rotationCount += wrap.rotations;
	}

	/**
	 * Gets the character in the node at the specified position.
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		return this.root.get(pos);
	}

	/**
	 * Gets the height of the tree.
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return this.root.height();
	}

	/**
	 * Returns the number of nodes in this tree, not counting the NULL_NODE
	 * 
	 * @return the number of nodes in this tree, not counting the NULL_NODE
	 */
	public int size() {
		return this.root.size();
	}

	/**
	 * Removes a node and returns the value that used to be stored in it
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// We replace it with the
		// *successor*.
		Wrap wrap = this.root.delete(pos);
		this.root = wrap.n;
		this.rotationCount += wrap.rotations;
		return wrap.delVal;
	}

	/**
	 * Retrieves a string from the characters in the nodes at the specified
	 * locations. This method operates in O(length*log N), where N is the size
	 * of this tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		StringBuilder sb = new StringBuilder();
		this.root.get(sb, pos, pos + length - 1);
		return sb.toString();
	}

	/**
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		// checks this != other
		if (other == this) {
			throw new IllegalArgumentException();
		}
		if (this.root instanceof Null_Node) {
			this.root = other.root;
			other.root = Node.getNull();
			return;
		}
		Wrap p = this.root.concatenate(other.root);
		// makes other empty
		other.root = Node.getNull();
		this.rotationCount += p.rotations;
		this.root = p.n;
	}

	/**
	 * Finds the index of a specified string in this EditorTree
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return this.toString().indexOf(s);
	}

	/**
	 * Returns the position in this tree of the first occurrence of s that does
	 * not occur before position pos
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		return this.toString().indexOf(s, pos);
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

	/**
	 * An inefficient algorithm for determining the height of the tree
	 * 
	 * @return height
	 */
	public int slowHeight() {
		return this.root.slowHeight();
	}

	/**
	 * An inefficient algorithm for determining the number of nodes in the tree
	 * 
	 * @return size
	 */
	public int slowSize() {
		return this.root.slowSize();
	}
	
}
