package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.builder.OnSpec;
import net.automatalib.automata.fsa.MutableFSA;

public interface OnSpecFSA<S, I, A extends MutableFSA<S,I>> extends OnSpec<S, I, S, Boolean, Void, A> {
	@Override
	FollowingFromSpecFSA<S,I,A> to(Object stateId);
}
