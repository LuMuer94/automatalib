package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.builder.FollowingFromSpec;
import net.automatalib.automata.fsa.MutableFSA;

public interface FollowingFromSpecFSA<S, I, A extends MutableFSA<S,I>> extends
	FollowingFromSpec<S,I,S,Boolean,Void,A>,
	FirstFromSpecFSA<S,I,A>,
	OnSpecFSA<S,I,A>,
	RootSpecFSA<S,I,A> {

}
