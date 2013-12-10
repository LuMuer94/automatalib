package net.automatalib.automata.builder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import net.automatalib.automata.MutableAutomaton;


@Generated("(withInitial(Object*) | (from(=currentSource) (on(=currentInput) (to@completeTransition)+)+)* !automaton)")
public class AutomatonBuilderBase<S, I, T, SP, TP, A extends MutableAutomaton<S, I, T, SP, TP>> {
	
	protected final A automaton;
	private final Map<Object,S> states = new HashMap<>(); 
	
	protected S currentSource;
	protected I currentInput;
	protected TP currentTransProp;
	
	protected SP defaultStateProp() {
		return null;
	}
	
	protected TP defaultTransProp() {
		return null;
	}
	
	protected S state(Object stateId) {
		S state;
		if(states.containsKey(stateId)) {
			state = states.get(stateId);
		}
		else {
			SP prop = defaultStateProp();
			state = automaton.addState(prop);
			states.put(stateId, state);
		}
		return state;
	}

	protected AutomatonBuilderBase(A automaton) {
		this.automaton = automaton;
	}

	protected A automaton() {
		return automaton;
	}
	
	protected void on() {
		currentTransProp = null;
	}
	
	protected void withInitial(Object stateId) {
		S state = state(stateId);
		automaton.setInitial(state, true);
	}
	
	protected void completeTransition(Object targetId) {
		S target = state(targetId);
		automaton.addTransition(currentSource, currentInput, target, currentTransProp);
	}

}
