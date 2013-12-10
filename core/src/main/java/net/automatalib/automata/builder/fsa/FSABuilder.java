package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.fsa.MutableFSA;

public class FSABuilder<S, I, A extends MutableFSA<S, I>> implements
		RootSpecFSA<S, I, A> {
	
	protected final FSABuilderImpl<S, I, A> impl;

	public FSABuilder(A automaton) {
		this.impl = new FSABuilderImpl<>(automaton);
	}

	@Override
	public A automaton() {
		return impl.automaton();
	}

	@Override
	public FirstFromSpecFSA<S, I, A> from(Object stateId) {
		return impl.from(stateId);
	}

	@Override
	public RootSpecFSA<S, I, A> withInitial(Object stateId) {
		return impl.withInitial(stateId);
	}

	@Override
	public RootSpecFSA<S, I, A> withInitial(Object... stateIds) {
		return impl.withInitial(stateIds);
	}

	@Override
	public RootSpecFSA<S, I, A> withStateProp(Object stateId, Boolean stateProp) {
		return impl.withStateProp(stateId, stateProp);
	}

	@Override
	public RootSpecFSA<S, I, A> withAccepting(Object stateId) {
		return impl.withAccepting(stateId);
	}

	@Override
	public RootSpecFSA<S, I, A> withAccepting(Object... stateIds) {
		return impl.withAccepting(stateIds);
	}

}
