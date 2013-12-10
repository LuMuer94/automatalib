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
package net.automatalib.util.ts;

import java.util.Iterator;
import java.util.Objects;

import net.automatalib.commons.util.mappings.Mapping;
import net.automatalib.ts.DeterministicTransitionSystem;
import net.automatalib.ts.TransitionSystem;
import net.automatalib.ts.UniversalTransitionSystem;
import net.automatalib.util.ts.TSIterators.AccessSequencesIterator;
import net.automatalib.util.ts.TSIterators.AllDefinedTransitionsIterator;
import net.automatalib.util.ts.TSIterators.AllTransitionsIterator;
import net.automatalib.util.ts.TSIterators.DefinedTransitionsIterator;
import net.automatalib.util.ts.TSIterators.UndefinedTransitionsIterator;
import net.automatalib.util.ts.traversal.BFSOrderIterator;
import net.automatalib.words.Word;

import com.google.common.base.Function;


public abstract class TS {
	
	public static final class TransRef<S,I,T> {
		private static final Function<? extends TransRef<?,?,?>,?> GET_STATE = new Function<TransRef<Object,?,?>,Object>() {
			@Override
			public Object apply(TransRef<Object,?,?> tr) {
				return tr.state;
			}
		};
		private static final Function<? extends TransRef<?,?,?>,?> GET_INPUT = new Function<TransRef<?,Object,?>,Object>() {
			@Override
			public Object apply(TransRef<?,Object,?> tr) {
				return tr.input;
			}
		};
		private static final Function<? extends TransRef<?,?,?>,?> GET_TRANSITION = new Function<TransRef<?,?,Object>,Object>() {
			@Override
			public Object apply(TransRef<?,?,Object> tr) {
				return tr.state;
			}
		};
		@SuppressWarnings("unchecked")
		public static <S> Function<TransRef<S,?,?>,S> stateGetter() {
			return (Function<TransRef<S,?,?>,S>)GET_STATE;
		}
		@SuppressWarnings("unchecked")
		public static <I> Function<TransRef<?,I,?>,I> inputGetter() {
			return (Function<TransRef<?,I,?>,I>)GET_INPUT;
		}
		@SuppressWarnings("unchecked")
		public static <T> Function<TransRef<?,?,T>,T> transitionGetter() {
			return (Function<TransRef<?,?,T>,T>)GET_TRANSITION;
		}
		
		public final S state;
		public final I input;
		public final T transition;
		public TransRef(S state, I input) {
			this(state, input, null);
		}
		public TransRef(S state, I input, T transition) {
			this.state = state;
			this.input = input;
			this.transition = transition;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hashCode(input);
			result = prime * result + Objects.hashCode(input);
			result = prime * result + Objects.hashCode(transition);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (obj.getClass() != TransRef.class)
				return false;
			TransRef<?,?,?> other = (TransRef<?,?,?>)obj;
			if(!Objects.equals(state, other.state))
				return false;
			if(!Objects.equals(input, other.input))
				return false;
			return Objects.equals(transition, other.transition);
		}
	}
	
	
	public static final class AccessSequence<S,I> {
		public final S state;
		public final Word<I> access;
		public AccessSequence(S state, Word<I> access) {
			this.state = state;
			this.access = access;
		}
	}
	
	
	
	
	public static <S,I,T> Iterable<T> allTransitions(final TransitionSystem<S,I,T> ts,
			final S state,
			final Iterable<? extends I> inputs) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new AllTransitionsIterator<S,I,T>(ts, state, inputs.iterator());
			}
		};
	}
	
	public static <S,I> Iterable<S> bfsOrder(final TransitionSystem<S,I,?> ts, final Iterable<? extends I> inputs) {
		return new Iterable<S>() {
			@Override
			public Iterator<S> iterator() {
				return new BFSOrderIterator<S, I>(ts, inputs);
			}
			
		};
	}
	
	public static <S,SP> Mapping<S,SP> stateProperties(final UniversalTransitionSystem<S, ?, ?, SP, ?> uts) {
		return new Mapping<S,SP>() {
			@Override
			public SP get(S elem) {
				return uts.getStateProperty(elem);
			}
		};
	}
	
	public static <T,TP> Mapping<T,TP> transitionProperties(final UniversalTransitionSystem<?, ?, T, ?, TP> uts) {
		return new Mapping<T,TP>() {
			@Override
			public TP get(T elem) {
				return uts.getTransitionProperty(elem);
			}
		};
	}
	
	public static <S,I,T> Iterator<TransRef<S,I,T>> definedTransitionsIterator(DeterministicTransitionSystem<S, I, T> dts,
			S state,
			Iterator<? extends I> inputsIt) {
		return new DefinedTransitionsIterator<S,I,T>(dts, state, inputsIt);
	}
	
	public static <S,I,T> Iterable<TransRef<S,I,T>> definedTransitions(
			final DeterministicTransitionSystem<S, I, T> dts,
			final S state,
			final Iterable<? extends I> inputs) {
		return new Iterable<TransRef<S,I,T>>() {
			@Override
			public Iterator<TransRef<S,I,T>> iterator() {
				return definedTransitionsIterator(dts, state, inputs.iterator());
			}
		};
	}
	
	public static <S,I,T> Iterator<TransRef<S,I,T>> allDefinedTransitionsIterator(
			DeterministicTransitionSystem<S, I, T> dts,
			Iterator<? extends S> stateIt,
			Iterable<? extends I> inputs) {
		return new AllDefinedTransitionsIterator<>(stateIt, dts, inputs);
	}
	
	public static <S,I,T> Iterable<TransRef<S,I,T>> allDefinedTransitions(
			final DeterministicTransitionSystem<S, I, T> dts,
			final Iterable<? extends S> states,
			final Iterable<? extends I> inputs) {
		return new Iterable<TransRef<S,I,T>>() {
			@Override
			public Iterator<TransRef<S,I,T>> iterator() {
				return allDefinedTransitionsIterator(dts, states.iterator(), inputs);
			}
		};
	}
	
	
	
	public static <S,I,T> Iterator<TransRef<S,I,T>> undefinedTransitionsIterator(DeterministicTransitionSystem<S, I, ?> dts,
			S state,
			Iterator<? extends I> inputsIt) {
		return new UndefinedTransitionsIterator<S,I,T>(dts, state, inputsIt);
	}
	
	public static <S,I,T> Iterable<TransRef<S,I,T>> undefinedTransitions(
			final DeterministicTransitionSystem<S, I, ?> dts,
			final S state,
			final Iterable<? extends I> inputs) {
		return new Iterable<TransRef<S,I,T>>() {
			@Override
			public Iterator<TransRef<S,I,T>> iterator() {
				return undefinedTransitionsIterator(dts, state, inputs.iterator());
			}
		};
	}
	
	public static <S,I,T> Iterator<TransRef<S,I,T>> allUndefinedTransitionsIterator(
			DeterministicTransitionSystem<S, I, T> dts,
			Iterator<? extends S> stateIt,
			Iterable<? extends I> inputs) {
		return new TSIterators.AllUndefinedTransitionsIterator<>(stateIt, dts, inputs);
	}
	
	public static <S,I,T> Iterable<TransRef<S,I,T>> allUndefinedTransitions(
			final DeterministicTransitionSystem<S, I, T> dts,
			final Iterable<? extends S> states,
			final Iterable<? extends I> inputs) {
		return new Iterable<TransRef<S,I,T>>() {
			@Override
			public Iterator<TransRef<S,I,T>> iterator() {
				return allUndefinedTransitionsIterator(dts, states.iterator(), inputs);
			}
		};
	}
	
	
	public static <S,I> Iterator<AccessSequence<S,I>>
	accessSequencesIterator(DeterministicTransitionSystem<S, I, ?> ts, Iterable<? extends I> inputs) {
		return new AccessSequencesIterator<>(ts, inputs);
	}
	
	public static <S,I>
	Iterable<AccessSequence<S,I>> accessSequences(
			final DeterministicTransitionSystem<S, I, ?> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<AccessSequence<S,I>>() {
			@Override
			public Iterator<AccessSequence<S, I>> iterator() {
				return accessSequencesIterator(ts, inputs); 
			}
		};
	}
	
	public static <I>
	Iterator<Word<I>> accessSequenceWordsIterator(DeterministicTransitionSystem<?, I, ?> ts, Iterable<? extends I> inputs) {
		return new AccessSequencesIterator.WordsIterator<>(
				accessSequencesIterator(ts, inputs));
	}
	
	public static <I>
	Iterable<Word<I>> accessSequenceWords(
			final DeterministicTransitionSystem<?, I, ?> ts,
			final Iterable<? extends I> inputs) {
		return new Iterable<Word<I>>() {
			@Override
			public Iterator<Word<I>> iterator() {
				return accessSequenceWordsIterator(ts, inputs);
			}		
		};
	}
	
}
