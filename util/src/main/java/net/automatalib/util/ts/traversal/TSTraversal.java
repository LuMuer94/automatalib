/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * AutomataLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * AutomataLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with AutomataLib; if not, see
 * http://www.gnu.de/documents/lgpl.en.html.
 */
package net.automatalib.util.ts.traversal;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import javax.swing.event.DocumentEvent.EventType;

import net.automatalib.commons.util.Holder;
import net.automatalib.commons.util.collections.TransformingIterator;
import net.automatalib.commons.util.mappings.MutableMapping;
import net.automatalib.ts.TransitionSystem;
import net.automatalib.util.traversal.Color;
import net.automatalib.util.traversal.SearchEventType;
import net.automatalib.util.traversal.Record;
import net.automatalib.util.traversal.TraversalEventType;
import net.automatalib.util.traversal.TraversalOrder;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;


/**
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public abstract class TSTraversal {
	
	public static final class RawEvent<S,I,T,D> {
		
		public static <S,I,T,D> RawEvent<S,I,T,D> init(S target) {
			return new RawEvent<S, I, T, D>(TraversalEventType.INIT, null, null, null, target, null);
		}
		
		public static <S,I,T,D> RawEvent<S,I,T,D> start(Record<S,D> sourceRec) {
			return new RawEvent<S,I,T,D>(TraversalEventType.START, sourceRec, null, null, null, null);
		}
		
		public static <S,I,T,D> RawEvent<S,I,T,D> finish(S target, D targetData) {
			return new RawEvent<S,I,T,D>(TraversalEventType.FINISH, null, null, null, target, targetData);
		}
		
		public static <S,I,T,D> RawEvent<S,I,T,D> backtrack(S target, D targetData, I input, T transition, Record<S,D> sourceRec) {
			return new RawEvent<S,I,T,D>(TraversalEventType.FINISH, sourceRec, input, transition, target, targetData);
		}
		
		public static <S,I,T,D> RawEvent<S,I,T,D> edge(Record<S,D> sourceRec, I input, T transition, S target) {
			return new RawEvent<S,I,T,D>(TraversalEventType.EDGE, sourceRec, input, transition, target, null);
		}
		
		protected final Record<S,D> sourceRec;
		protected final TraversalEventType type;
		protected final I input;
		protected final T transition;
		protected final S target;
		protected final D targetData;
		
		private RawEvent(TraversalEventType type, Record<S,D> sourceRec, I input, T transition, S target, D targetData) {
			this.type = type;
			this.sourceRec = sourceRec;
			this.input = input;
			this.transition = transition;
			this.target = target;
			this.targetData = targetData;
		}

		public TraversalEventType getType() {
			return type;
		}
		
		public Record<S,D> getSourceRec() {
			return sourceRec;
		}
		
		public S getSource() {
			return sourceRec.node;
		}
		
		public D getSourceData() {
			return sourceRec.data;
		}
		
		public void setSourceData(D data) {
			sourceRec.data = data;
		}
		
		public I getInput() {
			return input;
		}
		
		public T getTransition() {
			return transition;
		}
		
		public S getTarget() {
			return target;
		}
		
		public D getTargetData() {
			return targetData;
		}
		
		
		public final boolean isInit() {
			return (type == TraversalEventType.INIT);
		}
		
		public final boolean isFinish() {
			return (type == TraversalEventType.FINISH);
		}
		
		public boolean isEdge() {
			return (type == TraversalEventType.EDGE);
		}
		
		public boolean isStart() {
			return (type == TraversalEventType.START);
		}
	}
	
	
	public static abstract class RawTraversalIterator<S, I, T, D> extends AbstractIterator<TSTraversal.RawEvent<S,I,T,D>> {
		
		protected final TransitionSystem<S, I, T> ts;
		protected final Iterable<? extends I> inputs;
		protected Iterator<? extends S> initIt;
		
		public RawTraversalIterator(TransitionSystem<S, I, T> ts, Iterable<? extends I> inputs, Iterator<? extends S> initIt) {
			this.ts = ts;
			this.inputs = inputs;
			this.initIt = initIt;
		}
		
		public abstract void scheduleExploration(S state, D data);
		public abstract void skipInput();
		public abstract void skipState();
	}
	
	public static final class BreadthFirstIterator<S,I,T,D> extends RawTraversalIterator<S,I,T,D> {
		
		private final Queue<Record<S,D>> bfQueue = new ArrayDeque<>();
		private Record<S,D> current;
		private Iterator<? extends I> inputsIt;
		private I currentInput;
		private Iterator<? extends T> transIt;
		
		
		public BreadthFirstIterator(TransitionSystem<S, I, T> ts,
				Iterable<? extends I> inputs,
				Iterator<? extends S> initIt) {
			super(ts, inputs, initIt);
		}
		

		@Override
		public void scheduleExploration(S state, D data) {
			bfQueue.offer(new Record<>(state, data));
		}

		@Override
		public void skipInput() {
			transIt = null;
		}

		@Override
		public void skipState() {
			transIt = null;
			inputsIt = null;
		}

		@Override
		protected RawEvent<S, I, T, D> computeNext() {
			if(initIt != null) {
				if(initIt.hasNext()) {
					S initState = initIt.next();
					RawEvent<S, I, T, D> event = RawEvent.init(initState);
					return event;
				}
				initIt = null;
			}
			
			if(current == null) {
				if((current = bfQueue.poll()) == null)
					return endOfData();
				RawEvent<S,I,T,D> event = RawEvent.start(current);
				inputsIt = inputs.iterator();
				transIt = null;
				return event;
			}
			
			while(transIt == null || !transIt.hasNext()) {
				if(!inputsIt.hasNext()) {
					RawEvent<S,I,T,D> event = RawEvent.finish(current.node, current.data);
					current = null;
					return event;
				}
				currentInput = inputsIt.next();
				transIt = ts.getTransitions(current.node, currentInput).iterator();
			}
			
			T trans = transIt.next();
			S succ = ts.getSuccessor(trans);
			
			RawEvent<S,I,T,D> event = RawEvent.edge(current, currentInput, trans, succ);
			
			return event;
		}	
	}
	
	public static final class DepthFirstIterator<S,I,T,D> extends RawTraversalIterator<S,I,T,D> {
		
		private static final class DFRecord<S,I,T,D> extends Record<S,D> {
			public final Iterator<? extends I> inputsIt;
			public I currentInput;
			public Iterator<? extends T> transitionsIt = null;
			public T currentTrans;
			
			public DFRecord(S state, D data, Iterator<? extends I> inputsIt) {
				super(state, data);
				this.inputsIt = inputsIt;
			}
		}
		
		private final Deque<DFRecord<S,I,T,D>> dfStack
			= new ArrayDeque<>();

		
		public DepthFirstIterator(TransitionSystem<S, I, T> ts, Iterable<? extends I> inputs, Iterator<? extends S> initIt) {
			super(ts, inputs, initIt);
		}
		
		@Override
		protected RawEvent<S,I,T,D> computeNext() {
			DFRecord<S,I,T,D> currDfRec;
			
			if((currDfRec = dfStack.peek()) == null) {
				if(!initIt.hasNext()) {
					return endOfData();
				}
				S initState = initIt.next();
				RawEvent<S,I,T,D> event = RawEvent.init(initState);
				return event;
			}
			
			S currState = currDfRec.node;
			Iterator<? extends T> transIt = currDfRec.transitionsIt;
			I currentInput = currDfRec.currentInput;
			while(transIt == null || !transIt.hasNext()) {
				if(!currDfRec.inputsIt.hasNext()) {
					dfStack.pop();
					DFRecord<S,I,T,D> pred = dfStack.peek();
					RawEvent<S,I,T,D> event;
					if(pred == null) {
						event = RawEvent.finish(currState, currDfRec.data);
					}
					else {
						event = RawEvent.backtrack(currState, currDfRec.data, pred.currentInput, pred.currentTrans, pred);
					}
					return event;
				}
				currDfRec.currentInput = currentInput = currDfRec.inputsIt.next();
				currDfRec.transitionsIt = transIt = ts.getTransitions(currState, currentInput).iterator();
			}
			
			T currTrans;
			currDfRec.currentTrans = currTrans = transIt.next();
			S succ = ts.getSuccessor(currTrans);
			
			RawEvent<S, I, T, D> event = RawEvent.edge(currDfRec, currentInput, currTrans, succ);
			
			return event;
		}

		@Override
		public void scheduleExploration(S state, D data) {
			DFRecord<S, I, T, D> rec = new DFRecord<>(state, data, inputs.iterator());
			dfStack.push(rec);
		}

		@Override
		public void skipInput() {
			DFRecord<S,I,T,D> curr = dfStack.peek();
			if(curr == null)
				throw new IllegalStateException();
			curr.transitionsIt = null;
		}

		@Override
		public void skipState() {
			if(dfStack.isEmpty())
				throw new IllegalStateException();
			dfStack.pop();
		}	
	}
	

	private static final class EventTypeFilter implements Predicate<AbstractEvent<?,?,?,?,?>> {
		private final EventType type;
		public EventTypeFilter(EventType type) {
			this.type = type;
		}
		@Override
		public boolean apply(AbstractEvent<?,?,?,?,?> edge) {
			return (edge.getEventType() == type);
		}
	}
	
	private static final class EventTypeSetFilter implements Predicate<AbstractEvent<?,?,?,?,?>> {
		private final Set<? super EventType> types;
		public EventTypeSetFilter(Set<? super EventType> types) {
			this.types = types;
		}
		@Override
		public boolean apply(AbstractEvent<?,?,?,?,?> event) {
			return types.contains(event.getEventType());
		}
	}
	
	public static Predicate<AbstractEvent<?,?,?,?,?>> eventTypeFilter(EventType firstEv, EventType ...restEv) {
		if(restEv.length == 0)
			return new EventTypeFilter(firstEv);
		EnumSet<EventType> set = EnumSet.of(firstEv, restEv);
		return new EventTypeSetFilter(set);
	}
	
	public static final Predicate<AbstractEvent<?,?,?,?,?>> INIT_FILTER = eventTypeFilter(EventType.INIT);
	public static final Predicate<AbstractEvent<?,?,?,?,?>> FINISH_FILTER = eventTypeFilter(EventType.FINISH);
	public static final Predicate<AbstractEvent<?,?,?,?,?>> EDGE_FILTER = eventTypeFilter(EventType.EDGE);

	
	/*
	 * Search (breadth-first search, depth-first search)
	 */
	

	public static abstract class SearchRec<S,D> extends Record<S,D> {
		protected Color color;
		public SearchRec(S state) {
			this(state, null, Color.WHITE);
		}
		public SearchRec(S state, D data) {
			this(state, data, Color.WHITE);
		}
		public SearchRec(S state, D data, Color color) {
			super(state, data);
			this.color = color;
		}
		
		public Color getColor() {
			return color;
		}
	}

	
	public static class SearchEvent<S,I,T,D,R extends SearchRec<S,D>> {
		protected final SearchEventType type;
		protected final R sourceRec;
		protected final I input;
		protected final T transition;
		protected final R targetRec;
		
		protected SearchEvent(SearchEventType type, R sourceRec, I input, T transition, R targetRec) {
			this.type = type;
			this.sourceRec = sourceRec;
			this.input = input;
			this.transition = transition;
			this.targetRec = targetRec;
		}
		
		public SearchEventType getType() {
			return type;
		}
		
		public S getSource() {
			return sourceRec.node;
		}
		
		public D getSourceData() {
			return sourceRec.data;
		}
		
		public void setSourceData(D data) {
			sourceRec.data = data;
		}
		
		public Color getSourceColor() {
			return sourceRec.color;
		}
		
		public R getSourceRec() {
			return sourceRec;
		}
		
		public S getTarget() {
			return targetRec.node;
		}
		
		public D getTargetData() {
			return targetRec.data;
		}
		
		public void setTargetData(D data) {
			targetRec.data = data;
		}
		
		public Color getTargetColor() {
			return targetRec.color;
		}
		
		public R getTargetRec() {
			return targetRec;
		}
	}
	
	

	public static abstract class SearchIterator<E extends SearchEvent<?, ?, ?, ?, ?>>
			extends UnmodifiableIterator<E> {

		public abstract void skipInput();
		public abstract void skipState();

	}
	
	public static class BFSRec<S,D> extends SearchRec<S,D> {
		public BFSRec(S state, D data) {
			this(state, data, Color.WHITE);
		}
		public BFSRec(S state, D data, Color color) {
			super(state, data);
			this.color = color;
		}
		public BFSRec(S state) {
			this(state, null, Color.WHITE);
		}
	}
	
	public static class BFSEvent<S,I,T,D> extends SearchEvent<S,I,T,D,BFSRec<S,D>> {
		public static <S,I,T,D> BFSEvent<S,I,T,D> init(BFSRec<S,D> initRec) {
			return new BFSEvent<>(SearchEventType.INIT, null, null, null, initRec);
		}
		
		public static <S,I,T,D> BFSEvent<S,I,T,D> start(BFSRec<S,D> startRec) {
			return new BFSEvent<>(SearchEventType.START, startRec, null, null, null);
		}
		
		public static <S,I,T,D> BFSEvent<S,I,T,D> finish(BFSRec<S,D> finishRec) {
			return new BFSEvent<>(SearchEventType.FINISH, null, null, null, finishRec);
		}
		
		public static <S,I,T,D> BFSEvent<S,I,T,D> treeEdge(BFSRec<S,D> sourceRec, I input, T trans, BFSRec<S,D> targetRec) {
			return new BFSEvent<>(SearchEventType.TREE_EDGE, sourceRec, input, trans, targetRec);
		}
		
		public static <S,I,T,D> BFSEvent<S,I,T,D> backEdge(BFSRec<S,D> sourceRec, I input, T trans, BFSRec<S,D> targetRec) {
			return new BFSEvent<>(SearchEventType.BACK_EDGE, sourceRec, input, trans, targetRec);
		}
		
		public static <S,I,T,D> BFSEvent<S,I,T,D> crossEdge(BFSRec<S,D> sourceRec, I input, T trans, BFSRec<S,D> targetRec) {
			return new BFSEvent<>(SearchEventType.CROSS_EDGE, sourceRec, input, trans, targetRec);
		}
		
		
		private BFSEvent(SearchEventType type, BFSRec<S, D> sourceRec, I input, T transition,
				BFSRec<S, D> targetRec) {
			super(type, sourceRec, input, transition, targetRec);
		}
	}
	
	

	public static final class BFSIterator<S, I, T, D> extends
			SearchIterator<BFSEvent<S, I, T, D>> {
		private final RawTraversalIterator<S, I, T, BFSRec<S, D>> traversalIt;
		private final MutableMapping<S, BFSRec<S, D>> records;

		public BFSIterator(TransitionSystem<S, I, T> ts,
				Iterable<? extends I> inputs, Iterator<? extends S> initIt) {
			this.traversalIt = new BreadthFirstIterator<>(ts, inputs, initIt);
			this.records = ts.createStaticStateMapping();
		}

		@Override
		public boolean hasNext() {
			return traversalIt.hasNext();
		}

		private final BFSRec<S, D> createRec(S state) {
			BFSRec<S, D> rec = new BFSRec<>(state);
			records.put(state, rec);
			return rec;
		}

		@Override
		public BFSEvent<S, I, T, D> next() {
			RawEvent<S, I, T, BFSRec<S, D>> rawEv = traversalIt.next();
			BFSEvent<S,I,T,D> event;
			switch (rawEv.getType()) {
			case INIT: {
				S initState = rawEv.getTarget();
				BFSRec<S, D> rec = createRec(initState);
				traversalIt.scheduleExploration(initState, rec);
				event = BFSEvent.init(rec);
				return event;
			}
			case START: {
				BFSRec<S, D> rec = rawEv.getSourceData();
				rec.color = Color.GRAY;
				event = BFSEvent.start(rec);
				return event;
			}
			case FINISH: {
				BFSRec<S, D> rec = rawEv.getTargetData();
				rec.color = Color.BLACK;
				event = BFSEvent.finish(rec);
				return event;
			}
			default: { // case EDGE:
				BFSRec<S, D> sourceRec = rawEv.getSourceData();
				I input = rawEv.getInput();
				T trans = rawEv.getTransition();
				S target = rawEv.getTarget();
				BFSRec<S, D> targetRec = records.get(target);
				if (targetRec == null) {
					targetRec = createRec(target);
					records.put(target, targetRec);
					traversalIt.scheduleExploration(target, targetRec);
					event = BFSEvent.treeEdge(sourceRec, input, trans, targetRec);
					return event;
				}
				Color col = targetRec.color;
				switch(col) {
				case WHITE:
					targetRec.color = Color.GRAY;
					// fall-through!
				case GRAY:
					event = BFSEvent.crossEdge(sourceRec, input, trans, targetRec);
					return event;
				default: // case BLACK:
					event = BFSEvent.backEdge(sourceRec, input, trans, targetRec);
					return event;
				}
			}
			}
		}

		@Override
		public void skipInput() {
			traversalIt.skipInput();
		}

		@Override
		public void skipState() {
			traversalIt.skipState();
		}
	}
	
	public static final class DFSRec<S,D> extends SearchRec<S,D> {
		public final int dfsNum;
		public DFSRec(S state, int dfsNum) {
			this(state, null, dfsNum);
		}
		public DFSRec(S state, D data, int dfsNum) {
			this(state, data, Color.WHITE, dfsNum);
		}
		public DFSRec(S state, D data, Color color, int dfsNum) {
			super(state, data, color);
			this.dfsNum = dfsNum;
		}
	}
	
	public static final class DFSEvent<S,I,T,D> extends SearchEvent<S,I,T,D,DFSRec<S,D>> {
		public static <S,I,T,D> DFSEvent<S,I,T,D> init(DFSRec<S,D> initRec) {
			return new DFSEvent<>(SearchEventType.INIT, null, null, null, initRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> start(DFSRec<S,D> startRec) {
			return new DFSEvent<>(SearchEventType.START, startRec, null, null, null);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> finish(DFSRec<S,D> finishRec) {
			return new DFSEvent<>(SearchEventType.FINISH, null, null, null, finishRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> backtrack(DFSRec<S,D> finishRec, I input, T trans, DFSRec<S,D> sourceRec) {
			return new DFSEvent<>(SearchEventType.FINISH, sourceRec, input, trans, finishRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> treeEdge(DFSRec<S,D> sourceRec, I input, T trans, DFSRec<S,D> targetRec) {
			return new DFSEvent<>(SearchEventType.TREE_EDGE, sourceRec, input, trans, targetRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> backEdge(DFSRec<S,D> sourceRec, I input, T trans, DFSRec<S,D> targetRec) {
			return new DFSEvent<>(SearchEventType.BACK_EDGE, sourceRec, input, trans, targetRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> crossEdge(DFSRec<S,D> sourceRec, I input, T trans, DFSRec<S,D> targetRec) {
			return new DFSEvent<>(SearchEventType.CROSS_EDGE, sourceRec, input, trans, targetRec);
		}
		
		public static <S,I,T,D> DFSEvent<S,I,T,D> forwardEdge(DFSRec<S,D> sourceRec, I input, T trans, DFSRec<S,D> targetRec) {
			return new DFSEvent<>(SearchEventType.FORWARD_EDGE, sourceRec, input, trans, targetRec);
		}
		
		private DFSEvent(SearchEventType type, DFSRec<S, D> sourceRec, I input, T transition,
				DFSRec<S, D> targetRec) {
			super(type, sourceRec, input, transition, targetRec);
		}
		
		public int getSourceDFSNum() {
			return sourceRec.dfsNum;
		}
		
		public int getTargetDFSNum() {
			return targetRec.dfsNum;
		}
		
	}

	public static final class DFSIterator<S, I, T, D> extends
			SearchIterator<DFSEvent<S, I, T, D>> {
		private final RawTraversalIterator<S, I, T, DFSRec<S, D>> traversalIt;
		private final MutableMapping<S, DFSRec<S, D>> records;
		private int dfsNum = 0;

		public DFSIterator(TransitionSystem<S, I, T> ts,
				Iterable<? extends I> inputs, Iterator<? extends S> initIt) {
			this.traversalIt = new DepthFirstIterator<>(ts, inputs, initIt);
			this.records = ts.createStaticStateMapping();
		}

		@Override
		public boolean hasNext() {
			return traversalIt.hasNext();
		}

		private final DFSRec<S, D> createRec(S state) {
			DFSRec<S, D> rec = new DFSRec<>(state, dfsNum++);
			records.put(state, rec);
			return rec;
		}

		@Override
		public DFSEvent<S, I, T, D> next() {
			RawEvent<S, I, T, DFSRec<S, D>> rawEv = traversalIt.next();
			DFSEvent<S,I,T,D> event;
			switch (rawEv.getType()) {
			case INIT: {
				S initState = rawEv.getTarget();
				DFSRec<S, D> rec = records.get(initState);
				if (rec == null) {
					rec = createRec(initState);
					records.put(initState, rec);
					traversalIt.scheduleExploration(initState, rec);
				}
				event = DFSEvent.init(rec);
				return event;
			}
			case START: {
				DFSRec<S,D> rec = rawEv.getSourceData();
				rec.color = Color.GRAY;
				event = DFSEvent.start(rec);
				return event;
			}
			case FINISH: {
				DFSRec<S, D> rec = rawEv.getTargetData();
				DFSRec<S,D> sourceRec = rawEv.getSourceData();
				rec.color = Color.BLACK;
				if(sourceRec == null) {
					event = DFSEvent.finish(rec);
				}
				else {
					I input = rawEv.getInput();
					T trans = rawEv.getTransition();
					event = DFSEvent.backtrack(rec, input, trans, sourceRec);
				}
				return event;
			}
			default: { // case EDGE:
				DFSRec<S, D> sourceRec = rawEv.getSourceData();
				I input = rawEv.getInput();
				T trans = rawEv.getTransition();
				S target = rawEv.getTarget();
				DFSRec<S, D> targetRec = records.get(target);
				if (targetRec == null) {
					targetRec = createRec(target);
					records.put(target, targetRec);
					traversalIt.scheduleExploration(target, targetRec);
					
					event = DFSEvent.treeEdge(sourceRec, input, trans, targetRec);
					return event;
				}
				
				Color col = targetRec.color;
				
				switch(col) {
				case GRAY:
					event = DFSEvent.backEdge(sourceRec, input, trans, targetRec);
					return event;
				default: // case BLACK:
					if(sourceRec.dfsNum > targetRec.dfsNum) {
						event = DFSEvent.crossEdge(sourceRec, input, trans, targetRec);
					}
					else {
						event = DFSEvent.forwardEdge(sourceRec, input, trans, targetRec);
					}
					return event;
				}
			}
			}
		}

		@Override
		public void skipInput() {
			traversalIt.skipInput();
		}

		@Override
		public void skipState() {
			traversalIt.skipState();
		}
	}
	
	
	

	public static final int NO_LIMIT = -1;
	
	
	public static <S,I,T,D>
	BreadthFirstIterator<S, I, T, D> breadthFirstIterator(
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		return new BreadthFirstIterator<>(ts, inputs, ts.getInitialStates().iterator());
	}
	
	public static <S,I,T,D>
	DepthFirstIterator<S,I,T,D> depthFirstIterator(
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		return new DepthFirstIterator<>(ts, inputs, ts.getInitialStates().iterator());
	}
	
	
	public static <S,I,T,D>
	BFSIterator<S,I,T,D> bfsIterator(
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		return new BFSIterator<>(ts, inputs, ts.getInitialStates().iterator());
	}
	
	public static <S,I,T,D>
	Iterable<BFSEvent<S,I,T,D>> bfs(
			final TransitionSystem<S, I, T> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<BFSEvent<S,I,T,D>>() {
			@Override
			public Iterator<BFSEvent<S, I, T, D>> iterator() {
				return bfsIterator(ts, inputs);
			}
		};
	}
	
	
	public static <S,I,T>
	Iterator<S> bfsStatesIterator(TransitionSystem<S, I, T> ts, Iterable<? extends I> inputs) {
		Iterator<BFSEvent<S,I,T,Void>> unfiltered = bfsIterator(ts, inputs);
		Iterator<BFSEvent<S,I,T,Void>> filtered = Iterators.filter(unfiltered, FIRST_VISIT_FILTER);
		return new TransformingIterator<BFSEvent<S,I,T,Void>,S>(filtered) {
			@Override
			protected S transform(BFSEvent<S, I, T, Void> event) {
				return event.targetRec.node;
			}
		};
	}
	
	
	public static <S,I,T>
	Iterable<S> bfsStates(final TransitionSystem<S, I, T> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<S>() {
			@Override
			public Iterator<S> iterator() {
				return bfsStatesIterator(ts, inputs);
			}
		};
	}
	
	
	public static <S,I,T,D>
	DFSIterator<S,I,T,D> dfsIterator(
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		return new DFSIterator<>(ts, inputs, ts.getInitialStates().iterator());
	}
	
	public static <S,I,T,D>
	Iterable<DFSEvent<S,I,T,D>> dfs(
			final TransitionSystem<S, I, T> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<DFSEvent<S,I,T,D>>() {
			@Override
			public Iterator<DFSEvent<S, I, T, D>> iterator() {
				return dfsIterator(ts, inputs);
			}
		};
	}
	
	public static <S,I>
	Iterator<S> dfsStatesIterator(TransitionSystem<S, I, ?> ts,
			Iterable<? extends I> inputs) {
		DFSIterator<S,?,?,?> unfiltered = dfsIterator(ts, inputs);
		Iterator<DFSEvent<S,?,?,?>> filtered = Iterators.filter(unfiltered, FIRST_VISIT_FILTER);
		return new TransformingIterator<DFSEvent<S,?,?,?>,S>(filtered) {
			@Override
			protected S transform(DFSEvent<S, ?, ?, ?> event) {
				return event.sourceRec.node;
			}
		};
	}
	
	public static <S,I>
	Iterable<S> dfsStates(
			final TransitionSystem<S, I, ?> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<S>() {
			@Override
			public Iterator<S> iterator() {
				return dfsStatesIterator(ts, inputs);
			}			
		};
	}

	
	public static <S,I,T,D>
	RawTraversalIterator<S,I,T,D> traverseIterator(
			TraversalOrder order,
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		switch(order) {
		case BREADTH_FIRST:
			return breadthFirstIterator(ts, inputs);
		default: // case DEPTH_FIRST:
			return depthFirstIterator(ts, inputs);
		}
	}
	
	public static <S,I,T,D>
	SearchIterator<? extends SearchEvent<S,I,T,D,?>> searchIterator(
			TraversalOrder order,
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		switch(order) {
		case BREADTH_FIRST:
			return bfsIterator(ts, inputs);
		default: // case DEPTH_FIRST:
			return dfsIterator(ts, inputs);
		}
	}
	
	public static <S,I,T,D>
	Iterable<? extends SearchEvent<S,I,T,D,?>> search(
			TraversalOrder order,
			TransitionSystem<S, I, T> ts,
			Iterable<? extends I> inputs) {
		switch(order) {
		case BREADTH_FIRST:
			return bfs(ts, inputs);
		default: // case DEPTH_FIRST:
			return dfs(ts, inputs);
		}
	}
	
	
	public static <S,I,T,D>
	boolean depthFirst(TransitionSystem<S, I, T> ts,
			int limit,
			Iterable<? extends I> inputs,
			TSTraversalVisitor<S, I, T, D> vis) {
		
		DepthFirstIterator<S, I, T, D> it = depthFirstIterator(ts, inputs);
		
		Holder<D> dataHolder = new Holder<>();
		while(it.hasNext()) {
			RawEvent<S, I, T, D> ev = it.next();
		}
		
		Deque<DFRecord<S,I,T,D>> dfsStack = new ArrayDeque<DFRecord<S,I,T,D>>();
		
		Holder<D> dataHolder = new Holder<>();
		
		// setting the following to false means that the traversal had to be aborted
		// due to reaching the limit
		boolean complete = true;
		int stateCount = 0;
		
		for(S initS : ts.getInitialStates()) {
			dataHolder.value = null;
			TSTraversalAction act = vis.processInitial(initS, dataHolder);
			switch(act) {
			case ABORT_INPUT:
			case ABORT_STATE:
			case IGNORE:
				continue;
			case ABORT_TRAVERSAL:
				return complete;
			case EXPLORE:
				if(stateCount != limit) {
					dfsStack.push(new DFRecord<S, I, T, D>(initS, inputs, dataHolder.value));
					stateCount++;
				}
				else
					complete = false;
				break;
			}
		}
		
		while(!dfsStack.isEmpty()) {
			DFRecord<S,I,T,D> current = dfsStack.peek();
			
			S source = current.state;
			D data = current.data;
			
			if(current.start(ts)) {
				if(!vis.startExploration(source, data)) {
					dfsStack.pop();
					continue;
				}
			}
			
			if(!current.hasNextTransition()) {
				dfsStack.pop();
				continue;
			}
		
			I input = current.input();
			T trans = current.transition();
			
			S succ = ts.getSuccessor(trans);
			dataHolder.value = null;
			TSTraversalAction act = vis.processTransition(source, data, input, trans, succ, dataHolder);
			
			switch(act) {
			case ABORT_INPUT:
				current.advanceInput(ts);
				break;
			case ABORT_STATE:
				dfsStack.pop();
				break;
			case ABORT_TRAVERSAL:
				return complete;
			case IGNORE:
				current.advance(ts);
				break;
			case EXPLORE:
				if(stateCount != limit) {
					dfsStack.push(new DFRecord<S,I,T,D>(succ, inputs, dataHolder.value));
					stateCount++;
				}
				else
					complete = false;
				break;
			}
		}
		
		return complete;
	}
	
	public static <S,I,T,D>
	boolean depthFirst(TransitionSystem<S, I, T> ts,
			Collection<? extends I> inputs,
			TSTraversalVisitor<S, I, T, D> vis) {
		return depthFirst(ts, NO_LIMIT, inputs, vis);
	}
	
	
	/**
	 * Traverses the given transition system in a breadth-first fashion.
	 * The traversal is steered by the specified visitor.
	 * 
	 * @param ts the transition system.
	 * @param inputs the input alphabet.
	 * @param vis the visitor.
	 */
	public static <S,I,T,D>
	boolean breadthFirst(TransitionSystem<S, I, T> ts,
			int limit,
			Collection<? extends I> inputs,
			TSTraversalVisitor<? super S, ? super I, ? super T, D> vis) {
		Deque<BFSRecord<S,D>> bfsQueue = new ArrayDeque<BFSRecord<S,D>>();

		// setting the following to false means that the traversal had to be aborted
		// due to reaching the limit
		boolean complete = true;
		int stateCount = 0;
		
		Holder<D> dataHolder = new Holder<>();
		
		for(S initS : ts.getInitialStates()) {
			dataHolder.value = null;
			TSTraversalAction act = vis.processInitial(initS, dataHolder);
			switch(act) {
			case ABORT_TRAVERSAL:
				return complete;
			case EXPLORE:
				if(stateCount != limit) {
					bfsQueue.offer(new BFSRecord<S,D>(initS, dataHolder.value));
					stateCount++;
				}
				else
					complete = false;
				break;
			case ABORT_INPUT:
			case ABORT_STATE:
			case IGNORE:
			}
		}
		
		while(!bfsQueue.isEmpty()) {
			BFSRecord<S,D> current = bfsQueue.poll();
			
			S state = current.state;
			D data = current.data;
			
			if(!vis.startExploration(state, data))
				continue;
			
inputs_loop:
			for(I input : inputs) {
				Collection<T> transitions = ts.getTransitions(state, input);
				
				if(transitions == null)
					continue;
				
				for(T trans : transitions) {
					S succ = ts.getSuccessor(trans);
					
					dataHolder.value = null;
					TSTraversalAction act = vis.processTransition(state, data, input, trans, succ, dataHolder);
					
					switch(act) {
					case ABORT_INPUT:
						continue inputs_loop;
					case ABORT_STATE:
						break inputs_loop;
					case ABORT_TRAVERSAL:
						return complete;
					case EXPLORE:
						if(stateCount != limit) {
							bfsQueue.offer(new BFSRecord<S,D>(succ, dataHolder.value));
							stateCount++;
						}
						else
							complete = false;
						break;
					case IGNORE:
					}
				}
			}
		}
		
		return complete;
	}
	
	public static <S,I,T,D>
	boolean breadthFirst(TransitionSystem<S, I, T> ts,
			Collection<? extends I> inputs,
			TSTraversalVisitor<? super S, ? super I, ? super T, D> vis) {
		return breadthFirst(ts, NO_LIMIT, inputs, vis);
	}

	
	public static <S,I,T,D>
	boolean traverse(TraversalOrder order, TransitionSystem<S,I,T> ts, int limit, Collection<? extends I> inputs, TSTraversalVisitor<S, I, T, D> vis) {
		switch(order) {
		case BREADTH_FIRST:
			return breadthFirst(ts, limit, inputs, vis);
		case DEPTH_FIRST:
			return depthFirst(ts, limit, inputs, vis);
		default:
			throw new IllegalArgumentException("Unknown traversal order: " + order);
		}
	}
	
	public static <S,I,T,D>
	boolean traverse(TraversalOrder order, TransitionSystem<S,I,T> ts, Collection<? extends I> inputs, TSTraversalVisitor<S, I, T, D> vis) {
		return traverse(order, ts, NO_LIMIT, inputs, vis);
	}
	
}
