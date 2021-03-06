/* Copyright (C) 2014 TU Dortmund
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
package net.automatalib.util.automata.predicates;

import java.util.function.Predicate;

import net.automatalib.automata.concepts.TransitionOutput;
import net.automatalib.ts.TransitionPredicate;


final class OutputSatisfies<S,I,T,O> implements TransitionPredicate<S, I, T> {

	private final TransitionOutput<? super T, ? extends O> transOut;
	private final Predicate<? super O> outputPred;
	private final boolean negate;
	
	public OutputSatisfies(TransitionOutput<? super T, ? extends O> transOut, Predicate<? super O> outputPred) {
		this(transOut, outputPred, false);
	}
	
	public OutputSatisfies(TransitionOutput<? super T,? extends O> transOut, Predicate<? super O> outputPred, boolean negate) {
		this.transOut = transOut;
		this.outputPred = outputPred;
		this.negate = negate;
	}

	@Override
	public boolean apply(S source, I input, T transition) {
		O out = transOut.getTransitionOutput(transition);
		return negate ^ outputPred.test(out);
	}
}
