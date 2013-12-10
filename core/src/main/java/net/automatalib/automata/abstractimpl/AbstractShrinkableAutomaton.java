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
package net.automatalib.automata.abstractimpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.automatalib.automata.MutableAutomaton;
import net.automatalib.automata.ShrinkableAutomaton;

public abstract class AbstractShrinkableAutomaton<S, I, T, SP, TP> extends
		AbstractMutableAutomaton<S, I, T, SP, TP> implements
		ShrinkableAutomaton<S, I, T, SP, TP> {
	
	public static <S,I,T,SP,TP> void removeState(ShrinkableAutomaton<S,I,T,SP,TP> $this,
			S state) {
		$this.removeState(state, null);
	}
	

	public static <S,I,T,SP,TP> void unlinkState(MutableAutomaton<S,I,T,SP,TP> automaton,
			S state, S replacement, Collection<I> inputs) {
		
		for(S curr : automaton) {
			if(state.equals(curr))
				continue;
			
			for(I input : inputs) {
				Collection<? extends T> transitions = automaton.getTransitions(curr, input);
				if(transitions == null || transitions.isEmpty())
					continue;
				
				boolean modified = false;
				List<T> modTransitions = new LinkedList<T>(transitions); // TODO
					
				ListIterator<T> it = modTransitions.listIterator();
				while(it.hasNext()) {
					T trans = it.next();
					if(automaton.getSuccessor(trans) == state) {
						if(replacement == null)
							it.remove();
						else {
							T transRep = automaton.copyTransition(trans, replacement);
							it.set(transRep);
						}
						modified = true;
					}
				}
					
				if(modified)
					automaton.setTransitions(curr, input, modTransitions);
			}
		}
		
		if(automaton.getInitialStates().contains(state)) {
			automaton.setInitial(state, false);
			if(replacement != null)
				automaton.setInitial(replacement, true);
		}
	}
	
	

	@Override
	public void removeState(S state) {
		removeState(this, state);
	}
	
	

}
