/* Copyright (C) 2013-2014 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.automatalib.automata.transout;

import java.util.Collection;

import net.automatalib.automata.UniversalDeterministicAutomaton;
import net.automatalib.automata.dot.DOTHelperMealy;
import net.automatalib.automata.graphs.TransitionEdge;
import net.automatalib.automata.graphs.UniversalAutomatonGraphView;
import net.automatalib.graphs.UniversalGraph;
import net.automatalib.graphs.dot.GraphDOTHelper;
import net.automatalib.ts.transout.MealyTransitionSystem;

/**
 *
 * @author fh
 */
public interface MealyMachine<S,I,T,O> extends UniversalDeterministicAutomaton<S, I, T, Void, O>,
		TransitionOutputAutomaton<S,I,T,O>, MealyTransitionSystem<S, I, T, O> {
	
	public static class MealyGraphView<S, I, T, O, A extends MealyMachine<S, I, T, O>>
			extends UniversalAutomatonGraphView<S, I, T, Void, O, A> {

		public MealyGraphView(A automaton, Collection<? extends I> inputs) {
			super(automaton, inputs);
		}

		@Override
		public GraphDOTHelper<S, ? super TransitionEdge<I, T>> getGraphDOTHelper() {
			return new DOTHelperMealy<>(automaton);
		}
	}
	
	@Override
	default public UniversalGraph<S,TransitionEdge<I,T>,Void,TransitionEdge.Property<I,O>>
	transitionGraphView(Collection<? extends I> inputs) {
		return new MealyGraphView<S,I,T,O,MealyMachine<S,I,T,O>>(this, inputs);
	}
}
