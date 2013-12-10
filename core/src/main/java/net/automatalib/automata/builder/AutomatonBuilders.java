package net.automatalib.automata.builder;

import net.automatalib.automata.MutableAutomaton;
import net.automatalib.automata.builder.fsa.FSABuilder;
import net.automatalib.automata.fsa.MutableFSA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.words.Alphabet;

public abstract class AutomatonBuilders {

	
	public static <S,I,T,SP,TP,A extends MutableAutomaton<S,I,T,SP,TP>>
	AutomatonBuilder<S,I,T,SP,TP,A> from(A automaton) {
		return new AutomatonBuilder<>(automaton);
	}
	
	public static <S,I,A extends MutableFSA<S,I>>
	FSABuilder<S,I,A> from(A automaton) {
		return new FSABuilder<>(automaton);
	}
	
	public static <I>
	FSABuilder<Integer,I,CompactDFA<I>> newDFA(Alphabet<I> alphabet) {
		return from(new CompactDFA<>(alphabet));
	}
	
	
	private AutomatonBuilders() {
		// prevent inheritance
	}

}
