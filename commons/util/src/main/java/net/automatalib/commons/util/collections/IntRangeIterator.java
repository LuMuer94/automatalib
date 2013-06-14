package net.automatalib.commons.util.collections;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IntRangeIterator implements ListIterator<Integer> {
	
	private final int low;
	private final int step;
	private final int size;
	private int curr;

	public IntRangeIterator(int low, int size, int step) {
		this(low, size, step, 0);
	}
	
	public IntRangeIterator(int low, int size, int step, int startIdx) {
		this.low = low;
		this.size = size ;
		this.step = step;
		this.curr = low + step * startIdx;
	}

	@Override
	public boolean hasNext() {
		return curr < size;
	}
	
	public int intNext() {
		if(!hasNext())
			throw new NoSuchElementException();
		return intValue(curr++);
	}

	@Override
	public Integer next() {
		return Integer.valueOf(intNext());
	}

	@Override
	public boolean hasPrevious() {
		return curr > 0;
	}

	@Override
	public Integer previous() {
		return Integer.valueOf(intPrevious());
	}
	
	public int intPrevious() {
		if(!hasPrevious())
			throw new NoSuchElementException();
		return intValue(--curr);
	}

	@Override
	public int nextIndex() {
		if(!hasNext())
			throw new NoSuchElementException();
		return curr;
	}

	@Override
	public int previousIndex() {
		if(!hasPrevious())
			throw new NoSuchElementException();
		return curr-1;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(Integer e) {
		throw new UnsupportedOperationException();
	}
	
	public final int intValue(int idx) {
		return low + step * idx;
	}
	
	public final Integer value(int idx) {
		return Integer.valueOf(intValue(idx));
	}
}