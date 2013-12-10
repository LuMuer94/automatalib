package net.automatalib.automata.builder;

import net.automatalib.automata.MutableAutomaton;

public interface FollowingFromSpec<S, I, T, SP, TP, A extends MutableAutomaton<S,I,T,SP,TP>>
	extends FirstFromSpec<S,I,T,SP,TP,A>, OnSpec<S,I,T,SP,TP,A>, RootSpec<S,I,T,SP,TP,A> {
}
