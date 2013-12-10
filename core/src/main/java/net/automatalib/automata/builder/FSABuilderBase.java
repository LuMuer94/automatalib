package net.automatalib.automata.builder;

import net.automatalib.automata.fsa.MutableFSA;

public class FSABuilderBase<S, I, A extends MutableFSA<S, I>>
		extends AutomatonBuilderBase<S,I,S,Boolean,Void,A> {

	public FSABuilderBase(A automaton) {
		super(automaton);
	}

	protected void withAccepting(Object stateId) {
		S state = state(stateId);
		automaton.setAccepting(state, true);
	}

}
