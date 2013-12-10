package net.automatalib.util.ts;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import net.automatalib.commons.util.collections.TransformingIterator;
import net.automatalib.commons.util.collections.TwoLevelIterator;
import net.automatalib.commons.util.mappings.Mapping;
import net.automatalib.commons.util.mappings.MutableMapping;
import net.automatalib.ts.DeterministicTransitionSystem;
import net.automatalib.ts.TransitionSystem;
import net.automatalib.util.ts.TS.AccessSequence;
import net.automatalib.util.ts.TS.TransRef;
import net.automatalib.words.Word;

import com.google.common.collect.AbstractIterator;

abstract class TSIterators {
	public static final class AccessSequencesIterator<S,I> implements Iterator<AccessSequence<S,I>> {
		public static final class WordsIterator<I> extends TransformingIterator<AccessSequence<?,I>, Word<I>> {
			public WordsIterator(
					Iterator<? extends AccessSequence<?, I>> iterator) {
				super(iterator);
			}
			public WordsIterator(
					DeterministicTransitionSystem<?, I, ?> ts, Iterable<? extends I> inputs) {
				super(new AccessSequencesIterator<>(ts, inputs));
			}
			@Override
			protected Word<I> transform(AccessSequence<?, I> internal) {
				return internal.access;
			}
		}
		
		private final MutableMapping<S,Word<I>> asMapping;
		private final Queue<AccessSequence<S,I>> bfsQueue = new ArrayDeque<>();
		private final DeterministicTransitionSystem<S, I, ?> ts;
		private final Iterable<? extends I> inputs;
		public AccessSequencesIterator(DeterministicTransitionSystem<S, I, ?> ts, Iterable<? extends I> inputs) {
			this.ts = ts;
			this.asMapping = ts.createStaticStateMapping();
			this.inputs = inputs;
		}
		@Override
		public boolean hasNext() {
			return !bfsQueue.isEmpty();
		}
		@Override
		public AccessSequence<S, I> next() {
			AccessSequence<S,I> nextAs = bfsQueue.poll();
			if(nextAs == null)
				throw new NoSuchElementException();
			S state = nextAs.state;
			Word<I> seq = nextAs.access.flatten();
			for(I input : inputs) {
				S succ = ts.getSuccessor(state, input);
				if(succ != null) {
					if(asMapping.get(succ) == null) {
						Word<I> succSeq = seq.append(input);
						AccessSequence<S,I> succAs = new AccessSequence<>(succ, succSeq);
						asMapping.put(succ, succSeq);
						bfsQueue.offer(succAs);
					}
				}
			}
			return nextAs;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		public Mapping<S,Word<I>> getAsMapping() {
			return asMapping;
		}
	}
	static final class AllTransitionsIterator<S, I, T> extends TwoLevelIterator<I,T,T> {
		private final TransitionSystem<S, I, T> ts;
		private final S state;
		public AllTransitionsIterator(TransitionSystem<S, I, T> ts, S state, Iterator<? extends I> inputsIt) {
			super(inputsIt);
			this.ts = ts;
			this.state = state;
		}
		@Override
		protected Iterator<T> l2Iterator(I input) {
			return ts.getTransitions(state, input).iterator();
		}
		@Override
		protected T combine(I input, T transition) {
			return transition;
		}
	}
	
	public static final class DefinedTransitionsIterator<S,I,T> extends AbstractIterator<TransRef<S,I,T>> {
		private final DeterministicTransitionSystem<S, I, T> dts;
		private final Iterator<? extends I> inputsIt;
		private final S state;
		public DefinedTransitionsIterator(
				DeterministicTransitionSystem<S, I, T> dts,
				S state,
				Iterator<? extends I> inputsIt) {
			this.dts = dts;
			this.inputsIt = inputsIt;
			this.state = state;
		}
		
		@Override
		protected TransRef<S,I,T> computeNext() {
			while(inputsIt.hasNext()) {
				I input = inputsIt.next();
				T trans = dts.getTransition(state, input);
				if(dts.getTransition(state, input) != null) {
					return new TransRef<>(state, input, trans);
				}
			}
			return endOfData();
		}	
	}
	
	public static final class AllDefinedTransitionsIterator<S,I,T>
			extends TwoLevelIterator<S,TransRef<S,I,T>,TransRef<S, I, T>> {
		private final DeterministicTransitionSystem<S, I, T> dts;
		private final Iterable<? extends I> inputs;
		public AllDefinedTransitionsIterator(
				Iterator<? extends S> stateIt,
				DeterministicTransitionSystem<S, I, T> dts,
				Iterable<? extends I> inputs) {
			super(stateIt);
			this.dts = dts;
			this.inputs = inputs;
		}
		@Override
		protected Iterator<TransRef<S,I,T>> l2Iterator(S state) {
			return TS.definedTransitionsIterator(dts, state, inputs.iterator());
		}
		@Override
		protected TransRef<S, I, T> combine(S state, TransRef<S,I,T> transRef) {
			return transRef;
		}
	}

	public static final class UndefinedTransitionsIterator<S,I,T> extends AbstractIterator<TransRef<S,I,T>> {
		private final DeterministicTransitionSystem<S, I, ?> dts;
		private final Iterator<? extends I> inputsIt;
		private final S state;
		public UndefinedTransitionsIterator(DeterministicTransitionSystem<S, I, ?> dts, S state, Iterator<? extends I> inputsIt) {
			this.dts = dts;
			this.inputsIt = inputsIt;
			this.state = state;
		}
		@Override
		protected TransRef<S,I,T> computeNext() {
			while(inputsIt.hasNext()) {
				I input = inputsIt.next();
				if(dts.getTransition(state, input) == null) {
					return new TransRef<>(state, input);
				}
			}
			return endOfData();
		}
	}
	
	
	public static final class AllUndefinedTransitionsIterator<S,I,T>
			extends TwoLevelIterator<S, TransRef<S,I,T>, TransRef<S,I,T>> {
		private final DeterministicTransitionSystem<S, I, T> dts;
		private final Iterable<? extends I> inputs;
		public AllUndefinedTransitionsIterator(
				Iterator<? extends S> stateIt,
				DeterministicTransitionSystem<S, I, T> dts,
				Iterable<? extends I> inputs) {
			super(stateIt);
			this.dts = dts;
			this.inputs = inputs;
		}
		@Override
		protected Iterator<TransRef<S,I,T>> l2Iterator(S state) {
			return TS.undefinedTransitionsIterator(dts, state, inputs.iterator());
		}
		@Override
		protected TransRef<S, I, T> combine(S l1Object, TransRef<S,I,T> l2Object) {
			return l2Object;
		}
	}


}
