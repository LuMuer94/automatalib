package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.builder.AutomatonBuilderImpl;
import net.automatalib.automata.fsa.MutableFSA;

public class FSABuilderImpl<S, I, A extends MutableFSA<S, I>> extends
		AutomatonBuilderImpl<S, I, S, Boolean, Void, A>
		implements FollowingFromSpecFSA<S,I,A> {

	public FSABuilderImpl(A automaton) {
		super(automaton);
	}

	@Override
	public A automaton() {
		return automaton;
	}

	@Override
	public FirstFromSpecFSA<S, I, A> from(Object stateId) {
		super.from(stateId);
		return this;
	}

	@Override
	public RootSpecFSA<S, I, A> withInitial(Object stateId) {
		super.withInitial(stateId);
		return this;
	}

	@Override
	public RootSpecFSA<S, I, A> withInitial(Object... stateIds) {
		super.withInitial(stateIds);
		return this;
	}

	@Override
	public RootSpecFSA<S, I, A> withStateProp(Object stateId, Boolean stateProp) {
		super.withStateProp(stateId, stateProp);
		return this;
	}

	@Override
	public RootSpecFSA<S, I, A> withAccepting(Object stateId) {
		S state = state(stateId);
		automaton.setAccepting(state, true);
		return this;
	}

	@Override
	public RootSpecFSA<S, I, A> withAccepting(Object... stateIds) {
		for(Object stateId : stateIds) {
			S state = state(stateId);
			automaton.setAccepting(state, true);
		}
		return this;
	}

	@Override
	protected Boolean defaultStateProp() {
		return false;
	}

	@Override
	public OnSpecFSA<S, I, A> on(I input) {
		super.on(input);
		return this;
	}

	@Override
	public FollowingFromSpecFSA<S, I, A> to(Object stateId) {
		super.to(stateId);
		return this;
	}

	
	
}
