package net.automatalib.automata.builder;

import java.util.HashMap;
import java.util.Map;

import net.automatalib.automata.MutableAutomaton;

public class AutomatonBuilderImpl<S, I, T, SP, TP, A extends MutableAutomaton<S,I,T,SP,TP>>
		implements FollowingFromSpec<S,I,T,SP,TP,A> {
	
	protected final A automaton;
	private final Map<Object,S> states = new HashMap<>();
	
	private S currentSource;
	private I currentInput;
	private TP currentTransProp;

	public AutomatonBuilderImpl(A automaton) {
		this.automaton = automaton;
	}
	
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

	@Override
	public OnSpec<S, I, T, SP, TP, A> on(I input) {
		this.currentInput = input;
		return this;
	}

	@Override
	public OnSpec<S, I, T, SP, TP, A> withProp(TP property) {
		this.currentTransProp = property;
		return this;
	}

	@Override
	public FollowingFromSpec<S, I, T, SP, TP, A> to(Object stateId) {
		S target = state(stateId);
		automaton.addTransition(currentSource, currentInput, target, currentTransProp);
		return this;
	}

	@Override
	public FirstFromSpec<S, I, T, SP, TP, A> from(Object stateId) {
		this.currentSource = state(stateId);
		return this;
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withInitial(Object... stateIds) {
		for(Object stateId : stateIds) {
			S state = state(stateId);
			automaton.setInitial(state, true);
		}
		return this;
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withInitial(Object stateId) {
		S state = state(stateId);
		automaton.setInitial(state, true);
		return this;
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withStateProp(Object stateId, SP stateProp) {
		S state = state(stateId);
		automaton.setStateProperty(state, stateProp);
		return this;
	}

	@Override
	public A automaton() {
		return automaton;
	}


}
