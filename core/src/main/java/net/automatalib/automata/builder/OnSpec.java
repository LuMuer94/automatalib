package net.automatalib.automata.builder;

import net.automatalib.automata.MutableAutomaton;

public interface OnSpec<S, I, T, SP, TP, A extends MutableAutomaton<S,I,T,SP,TP>> {
	OnSpec<S,I,T,SP,TP,A> withProp(TP property);
	FollowingFromSpec<S,I,T,SP,TP,A> to(Object stateId);
}
