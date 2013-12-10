package net.automatalib.automata.builder;

import javax.annotation.Generated;

import net.automatalib.automata.MutableAutomaton;

public class AutomatonBuilder<S,I,T,SP,TP,A extends MutableAutomaton<S,I,T,SP,TP>> implements RootSpec<S,I,T,SP,TP,A> {
	
	protected final AutomatonBuilderImpl<S,I,T,SP,TP,A> impl;
	
	protected AutomatonBuilder(A automaton) {
		this.impl = new AutomatonBuilderImpl<>(automaton);
	}
	
	@Override
	public FirstFromSpec<S, I, T, SP, TP, A> from(Object stateId) {
		return impl.from(stateId);
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withInitial(Object stateId) {
		return impl.withInitial(stateId);
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withInitial(Object... stateIds) {
		return impl.withInitial(stateIds);
	}

	@Override
	public RootSpec<S, I, T, SP, TP, A> withStateProp(Object stateId,
			SP stateProp) {
		return impl.withStateProp(stateId, stateProp);
	}

	@Override
	public A automaton() {
		return impl.automaton();
	}
	
	
}
