package net.automatalib.util.traversal;

public class Record<N,D> {
	public final N node;
	public D data;
	
	public Record(N node) {
		this(node, null);
	}
	
	public Record(N node, D data) {
		this.node = node;
		this.data = data;
	}
}