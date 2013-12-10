package net.automatalib.automata.builder;

import net.automatalib.automata.MutableAutomaton;

public interface FirstFromSpec<S, I, T, SP, TP, A extends MutableAutomaton<S,I,T,SP,TP>> {
	public OnSpec<S,I,T,SP,TP,A> on(I input);
}
