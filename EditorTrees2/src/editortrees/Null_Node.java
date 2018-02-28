package editortrees;

/**
 * Overrides methods in the node class where reaching the null node needs to
 * make something special happen
 */
public class Null_Node extends Node {

	public Null_Node(char ch) {
		super(ch);
	}

	@Override
	public Wrap add(char ch) {
		return new Wrap(ch);
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append("");
	}

	@Override
	public void toDebugString(StringBuilder sb) {
		return;
	}

	@Override
	public int slowHeight() {
		return -1;
	}

	@Override
	public int slowSize() {
		return 0;
	}

	@Override
	public Wrap delete(int pos) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int height() {
		return -1;
	}

	@Override
	public Wrap addAt(char ch, int pos) {
		if (pos != 0)
			throw new IndexOutOfBoundsException();
		return new Wrap(ch);
	}

	@Override
	public char get(int pos) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void get(StringBuilder sb, int start, int end) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Wrap concatenateHelper(int height, Code dir, char ch, Node v, int size) {
		Wrap w = new Wrap(ch);
		return w;
	}
}
