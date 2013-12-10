package net.automatalib.automata.builder;

import net.automatalib.automata.MutableDeterministic;

class DetAutomatonBuilderBase<S,I,T,SP,TP,A extends MutableDeterministic<S, I, T, SP, TP>>
		extends AutomatonBuilderBase<S, I, T, SP, TP, A> {

	public DetAutomatonBuilderBase(A automaton) {
		super(automaton);
	}

}
