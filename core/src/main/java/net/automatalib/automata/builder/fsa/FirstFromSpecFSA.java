package net.automatalib.automata.builder.fsa;

import net.automatalib.automata.builder.FirstFromSpec;
import net.automatalib.automata.fsa.MutableFSA;

public interface FirstFromSpecFSA<S, I,A extends MutableFSA<S,I>> extends
		FirstFromSpec<S, I, S, Boolean, Void,A> {
	@Override
	OnSpecFSA<S,I,A> on(I input);
}
