package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.builder.RootSpec;
import net.automatalib.automata.fsa.MutableFSA;


public interface RootSpecFSA<S, I, A extends MutableFSA<S,I>> extends RootSpec<S,I,S,Boolean,Void,A> {
	@Override
	FirstFromSpecFSA<S,I,A> from(Object stateId);
	@Override
	RootSpecFSA<S,I,A> withInitial(Object stateId);
	@Override
	RootSpecFSA<S,I,A> withInitial(Object ...stateIds);
	@Override
	RootSpecFSA<S,I,A> withStateProp(Object stateId, Boolean stateProp);
	
	public RootSpecFSA<S,I,A> withAccepting(Object stateId);
	public RootSpecFSA<S,I,A> withAccepting(Object ...stateIds);
}
