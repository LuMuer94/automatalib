package net.automatalib.util.traversal;

public abstract class SearchEvent<N, D> {
	
	private final SearchEventType type;

	public abstract Record<N,D> getSourceRec();
	public abstract Record<N,D> getTargetRec();
	
	public SearchEvent(SearchEventType type) {
		this.type = type;
	}
	
	public final SearchEventType getType() {
		return type;
	}
	
	public N getSource() {
		return getSourceRec().node;
	}
	
	public D getSourceData() {
		return getSourceRec().data;
	}
	
	public void setSourceData(D data) {
		getSourceRec().data = data;
	}
	
	public N getTarget() {
		return getTargetRec().node;
	}
	
	public D getTargetData() {
		return getTargetRec().data;
	}
	
	public void setTargetData(D data) {
		getTargetRec().data = data;
	}
}
