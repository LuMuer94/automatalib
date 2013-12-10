package net.automatalib.automata.builder;

import net.automatalib.automata.fsa.MutableDFA;

public class DFABuilderBase<S, I, A extends MutableDFA<S, I>> extends
		AutomatonBuilderBase<S, I, S, Boolean, Void, A> {

	@Override
	protected Boolean defaultStateProp() {
		return Boolean.FALSE;
	}
	
	public DFABuilderBase(A automaton) {
		super(automaton);
	}

	
}
