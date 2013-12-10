package net.automatalib.automata.builder;

import net.automatalib.automata.MutableAutomaton;


public interface RootSpec<S, I, T, SP, TP, A extends MutableAutomaton<S,I,T,SP,TP>> {
	public FirstFromSpec<S,I,T,SP,TP,A> from(Object stateId);
	public RootSpec<S,I,T,SP,TP,A> withInitial(Object stateId);
	public RootSpec<S,I,T,SP,TP,A> withInitial(Object ...stateIds);
	public RootSpec<S,I,T,SP,TP,A> withStateProp(Object stateId, SP stateProp);
	public A automaton();
}
