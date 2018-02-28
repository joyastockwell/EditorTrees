package editortrees;

import java.util.NoSuchElementException;


// A node in a height-balanced binary tree with rank.

public class Node {
	class Wrap {
		// holds whether or not we need to check if the tree is balanced at the
		// current Node
		Change change;
		Node n;
		char delVal;
		int rotations;
		int inserted;

		public Wrap(char ch) {
			this.n = new Node(ch);
			this.rotations = 0;
			this.change = Change.INS;
		}

		public Wrap(Node n) {
			this.n = n;
			this.rotations = 0;
			this.change = Change.DEL;
		}
	}

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		@Override
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}

		public Code not() {
			switch (this) {
			case LEFT:
				return Code.RIGHT;
			case RIGHT:
				return Code.LEFT;
			case SAME:
				return Code.SAME;
			default:
				throw new IllegalStateException();
			}
		}
	}

	// Used to keep track of whether we inserted or deleted or whether we need
	// to keep checking balance
	enum Change {
		DEL, NONE, INS;
		public boolean check() {
			return (this != Change.NONE);
		}
	}

	char element;
	Node left, right;
	int rank;
	Code balance;
	// this is the same null node for every Node
	static Node ournullnode = new Null_Node('%');

	public Node(char ch) {
		this.element = ch;
		this.right = ournullnode;
		this.left = ournullnode;
		this.balance = Code.SAME;
		this.rank = 0;

	}

	public static Node addFromString(String st) {
		int length = st.length();
		if (length == 0) {
			return ournullnode;
		}
		int middle = (length) / 2;
		Node node = new Node(st.charAt(middle));
		String left = st.substring(0, middle);
		int powFirst = Node.log2(middle);
		int powSecond = Node.log2(length - middle - 1);

		if (powFirst > powSecond) {
			node.balance = Code.LEFT;
		} else {
			node.balance = Code.SAME;
		}

		node.rank = middle;
		node.left = Node.addFromString(left);
		if (!(middle == length - 1)) {
			node.right = Node.addFromString(st.substring(middle + 1));
		}
		return node;
	}

	/**
	 * takes the int log[2](n) of the integer passed
	 *
	 * @param the
	 *            integer to take the log of
	 * @return the log[2](n) of the integer, or -1 if negative
	 */
	public static int log2(int item) {
		if (item < 0) {
			return -1;
		}
		int i = item;
		int pow = 0;
		while ((i >>= 1) != 0) {
			pow++;
		}
		return pow;
	}

	/**
	 * constructor that copies a node passed including all fields.
	 *
	 * @param n
	 *            the node to copy
	 */
	public Node(Node n) {
		this.element = n.element;
		this.balance = n.balance;
		this.rank = n.rank;
		if (n.right instanceof Null_Node) {
			this.right = ournullnode;
		} else {
			this.right = new Node(n.right);
		}
		if (n.left instanceof Null_Node) {
			this.left = ournullnode;
		} else
			this.left = new Node(n.left);
	}

	public int height() {
		if (this.balance == Code.LEFT)
			return this.left.height() + 1;
		return this.right.height() + 1;
	}

	public int size() {
		return this.rank + this.right.size() + 1;
	}

	/**
	 * Goes down tree searching for place to insert ch until place is found.
	 * Then goes back up, looking at enums. If it sees on the way up that flip
	 * is necessary, executes rotation.
	 * 
	 * Note that using add does not produce a tree sorted based on a comparable
	 * 
	 * @param ch
	 * @return the Wrap produced by Null_Node's add method
	 */
	public Wrap add(char ch) {
		Wrap w = this.right.add(ch);
		this.check(w, Code.RIGHT);
		return w;
	}

	/**
	 * Adds a node with the specified char at the specified location
	 * 
	 * @param ch
	 * @param pos
	 * @return a Wrap containing the new root of the subtree being worked on,
	 *         its parent, rotation count, and whether or not balance needs to
	 *         be checked after the Wrap is returned
	 */
	public Wrap addAt(char ch, int pos) {
		Wrap wrap;
		if (this.rank < pos) {
			wrap = this.right.addAt(ch, pos - this.rank - 1);
			this.check(wrap, Code.RIGHT);
		}
		// if passing to the left
		else {
			wrap = this.left.addAt(ch, pos);
			this.rank++;
			this.check(wrap, Code.LEFT);
		}
		return wrap;
	}

	/**
	 * takes a node away from the specified location. Note: redoes balance codes
	 * after rotations; the ones in the rotation functions are only correct if
	 * the rotation functions are being called for insertions
	 * 
	 * @param pos
	 * @return a Wrap with the new head of the subtree that has been modified
	 */
	public Wrap delete(int pos) {
		Wrap wrap;
		// go right if the position is greater than this' rank
		if (this.rank < pos) {
			wrap = this.right.delete(pos - this.rank - 1);
			this.check(wrap, Code.RIGHT);
		}
		// go left if the position is less than this' rank
		else if (this.rank > pos) {
			wrap = this.left.delete(pos);
			this.rank--;
			this.check(wrap, Code.LEFT);
		}
		// base case: if position == rank, we have found the node to delete
		else {
			if (this.right instanceof Null_Node) {
				if (this.left instanceof Null_Node) {
					wrap = new Wrap(Node.ournullnode);
				} else {
					wrap = new Wrap(this.left);
				}
				wrap.delVal = this.element;
				return wrap;
			}
			wrap = this.right.delete(0);
			this.check(wrap, Code.RIGHT);

			// switch this' element with wrap's element (its successor element)
			char retVal = this.element;
			this.element = wrap.delVal;
			wrap.delVal = retVal;
		}
		return wrap;
	}

	/**
	 * @return the Null_Node to which all the nodes in the tree point
	 */
	public static Node getNull() {
		return ournullnode;
	}

	/**
	 * Helper function to get an inorder string of this tree
	 * 
	 * @param sb
	 */
	public void toString(StringBuilder sb) {
		this.left.toString(sb);
		sb.append(this.element);
		this.right.toString(sb);
	}

	/****
	 * 
	 * helper for toDebugString
	 *
	 * @return
	 */
	public void toDebugString(StringBuilder sb) {
		sb.append(this.element);
		sb.append(this.rank);
		sb.append(this.balance.toString());
		sb.append(", ");
		this.left.toDebugString(sb);
		this.right.toDebugString(sb);
	}

	/**
	 * returns character at specified position
	 * 
	 * @param pos
	 * @return specified char
	 */
	public char get(int pos) {
		// if the position is greater than the rank of this node,
		// the desired char is on the right of this node
		if (this.rank < pos) {
			// subtract the rank of this to keep the position updated
			return this.right.get(pos - this.rank - 1);
		}
		// if the position is less than the rank of this node,
		// the desired char is on the left of this node
		else if (this.rank > pos) {
			// subtract the rank of this to keep the position updated
			return this.left.get(pos);
		}
		// if the rank is the position, return this node's character
		else
			return this.element;
	}

	/**
	 * makes a string out of the characters between (inclusive) the nodes whose
	 * ranks are specified
	 * 
	 * @param sb
	 * @param start
	 * @param end
	 */
	public void get(StringBuilder sb, int start, int end) {
		if (start < this.rank && this.rank < end) {
			this.left.get(sb, start, this.rank - 1);
			sb.append(this.element);
			this.right.get(sb, 0, end - this.rank - 1);
		} else if (start < this.rank) {
			if (this.rank == end) {
				this.left.get(sb, start, end - 1);
				sb.append(this.element);
			} else
				this.left.get(sb, start, end);
		} else if (this.rank < end) {
			if (this.rank == start) {
				sb.append(this.element);
				this.right.get(sb, 0, end - this.rank - 1);
			} else
				this.right.get(sb, start - this.rank - 1, end - this.rank - 1);
		} else {
			sb.append(this.element);
		}
	}

	public Wrap check(Wrap w, Code dir) {
		if (!w.change.check()) {
			this.set(dir, w.n);
			w.n = this;
			return w;
		}

		// d is the direction the subtree grew / direction it did not shrink
		Code d = (w.change == Change.INS) ? dir : dir.not();
		this.set(dir, w.n);
		Node b = this.get(d);

		// check if rotation is needed
		if (this.balance == d) {
			// rotate, deals with balance and rank when rotating
			if (b.balance == d.not()) {
				// double rotate
				this.doubleRotate(w, d.not());
			} else {
				// single rotate
				this.singleRotate(w, d.not());
			}
		}
		// check balance codes and rank if not rotating
		else {
			// check balance
			if (this.balance == d.not()) {
				this.balance = Code.SAME;
				if (w.change == Change.INS)
					w.change = Change.NONE;
			} else { // this.balance == Code.SAME
				this.balance = d;
				if (w.change == Change.DEL)
					w.change = Change.NONE;
			}
			// rank is dealt with in add or del
			// updates wrap and children
			w.n = this;
		}
		return w;
	}

	/**
	 * Performs a single Rotate in the direction given, modifying the wrap given
	 * as needed and setting all balances and ranks
	 * 
	 * @param w
	 * @param dir
	 */
	public void singleRotate(Wrap w, Code dir) {
		Node b = this.get(dir.not());
		// set children to rotate
		this.set(dir.not(), b.get(dir));
		b.set(dir, this);
		// set new balances
		if (w.change == Change.INS || b.balance == dir.not()) {
			this.balance = Code.SAME;
			b.balance = Code.SAME;
		} else {
			this.balance = dir.not();
			b.balance = dir;
			w.change = Change.NONE;
		}
		// correct wrap values
		w.n = b;
		if (w.change == Change.INS)
			w.change = Change.NONE;
		// check rank
		if (dir == Code.LEFT)
			b.rank += this.rank + 1;
		else
			this.rank -= b.rank + 1;
		// update rotations
		w.rotations++;
	}

	/**
	 * Performs a double rotate in the direction given, modifying the wrap given
	 * as needed and setting all balances and ranks
	 * 
	 * @param w
	 * @param dir
	 */
	public void doubleRotate(Wrap w, Code dir) {
		Node b = this.get(dir.not());
		Node c = b.get(dir);
		// set children to rotate
		this.set(dir.not(), c.get(dir));
		b.set(dir, c.get(dir.not()));
		c.set(dir.not(), b);
		c.set(dir, this);
		// set new balances
		if (c.balance == Code.SAME) {
			b.balance = Code.SAME;
			this.balance = Code.SAME;
		} else if (c.balance == dir.not()) {
			b.balance = Code.SAME;
			this.balance = dir;
			c.balance = Code.SAME;
		} else { // c.balance == dir
			this.balance = Code.SAME;
			b.balance = dir.not();
			c.balance = Code.SAME;
		}
		// correct wrap values
		w.n = c;
		if (w.change == Change.INS)
			w.change = Change.NONE;
		// check rank
		if (dir == Code.RIGHT) {
			this.rank -= c.rank + b.rank + 2;
			c.rank += b.rank + 1;
		} else {

			b.rank -= c.rank + 1;
			c.rank += this.rank + 1;
		}
		// update rotations
		w.rotations += 2;
	}

	/**
	 * Gets the child at the given direction
	 * 
	 * @param dir
	 * @return the node at the given direction
	 */
	public Node get(Code dir) {
		if (dir == Code.RIGHT)
			return this.right;
		if (dir == Code.LEFT)
			return this.left;
		throw new NoSuchElementException();
	}

	/**
	 * Sets the given node as the child at the given direction
	 * 
	 * @param dir
	 * @param node
	 */
	public void set(Code dir, Node node) {
		if (dir == Code.RIGHT) {
			this.right = node;
		} else if (dir == Code.LEFT) {
			this.left = node;
		} else
			throw new NoSuchElementException();
	}

	public Wrap concatenate(Node other) {
		return concatenate(other, null);
	}

	/**
	 * provides the information for concatenateHelper to concatenate, then calls
	 * concatenateHelper
	 * 
	 * @param other
	 * @return
	 */
	public Wrap concatenate(Node other, Character ch) {
		Wrap w;
		Node o = other;
		Character c = ch;
		if (other instanceof Null_Node) {
			w = new Wrap(this);
			w.change = Change.NONE;
			return w;
		}
		int diffHeight = this.height() - other.height();
		if (diffHeight < 0) {
			if (ch == null) {
				w = this.delete(this.size() - 1);
				if (w.change == Change.DEL)
					diffHeight--;
				c = w.delVal;
				o = w.n;
			}
			w = other.concatenateHelper(-diffHeight, Code.LEFT, c, o, o.size());
		} else {
			if (ch == null) {
				w = other.delete(0);
				if (w.change == Change.DEL)
					diffHeight++;
				c = w.delVal;
				o = w.n;
			}
			w = this.concatenateHelper(diffHeight, Code.RIGHT, c, o, o.size());
		}
		return w;
	}

	/**
	 * finds the node p needed in concatenation
	 * 
	 * @param height
	 *            of the smaller tree
	 * @param di,
	 *            whether to go left or right
	 * @param q,
	 *            root of subtree getting pasted in
	 * 
	 * @return node p
	 */
	public Wrap concatenateHelper(int height, Code dir, char ch, Node v, int insertedSize) {
		Wrap w;
		Node leftSubtree = (dir == Code.RIGHT) ? this : v;
		Node rightSubtree = (dir == Code.RIGHT) ? v : this;
		// base case
		if (height == 1 && this.balance == dir.not()) {
			w = this.paste(leftSubtree, ch, rightSubtree, dir.not());
			w.inserted = v.size();
			return w;
		} else if (height == 0) {
			w = this.paste(leftSubtree, ch, rightSubtree, Code.SAME);
			return w;
		}
		if (this.balance == Code.SAME || this.balance == dir) {
			w = this.get(dir).concatenateHelper(height - 1, dir, ch, v, insertedSize);

		} else {
			w = this.get(dir).concatenateHelper(height - 2, dir, ch, v, insertedSize);
		}
		if (dir == Code.LEFT)
			this.rank += insertedSize;
		this.check(w, dir);
		w.change = Change.INS;
		return w;
	}

	/**
	 * gets called on node p
	 * 
	 * @param height
	 *            of smaller tree pasted into larger one
	 * @param dir,
	 *            direction left or right
	 * @param pasted
	 *            q, root of subtree getting pasted in
	 * @return
	 */
	public Wrap paste(Node l, char rootChar, Node r, Code c) {
		Wrap w = new Wrap(rootChar);
		w.n.left = l;
		w.n.right = r;
		w.n.balance = c;
		w.n.rank = l.size();
		return w;
	}

	public int getRank() {
		return this.rank;
	}

	public Code getBalance() {
		return this.balance;
	}

	public char getElement() {
		return this.element;
	}

	public boolean hasLeft() {
		return !(this.left instanceof Null_Node);
	}

	public boolean hasRight() {
		return !(this.right instanceof Null_Node);
	}

	public boolean hasParent(Node root) {
		if (this == root)
			return false;
		return true;
	}

	public int slowHeight() {
		return Math.max(1 + this.left.slowHeight(), 1 + this.right.slowHeight());
	}

	public int slowSize() {
		return this.right.slowSize() + this.left.slowSize() + 1;
	}
}