package net.automatalib.util.ts.traversal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.util.automata.Automata;
import net.automatalib.util.automata.random.RandomAutomata;
import net.automatalib.util.ts.traversal.TSTraversal.BFSEdge;
import net.automatalib.util.ts.traversal.TSTraversal.AbstractEvent;
import net.automatalib.util.ts.traversal.TSTraversal.EdgeType;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

import com.google.common.collect.Iterables;

public class TraversalTest {


	public static <S,I,T>
	void printTraversal(Iterator<? extends AbstractEvent<S,I,T,?,?>> edgesIt) {
		while(edgesIt.hasNext()) {
			AbstractEvent<S,I,T,?,?> edge = edgesIt.next();
			if(edge.getEdgeType() == EdgeType.INIT) {
				System.out.println("Initial state [" + edge.getTarget() + "]");
			}
			else {
				System.out.println("Edge from [" + edge.getSource() + "] with input " + edge.getInput() + " to [" + edge.getTarget() + "], type " + edge.getEdgeType());
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Alphabet<Integer> inputs = Alphabets.integers(0, 2);
		DFA<?,Integer> dfa = RandomAutomata.getInstance().randomDFA(5, inputs);
		
		System.out.println("BFS:");
		printTraversal(TSTraversal.bfEdgesIterator(dfa, inputs));
		System.out.println("DFS:");
		printTraversal(TSTraversal.dfsEdgesIterator(dfa, inputs));		
	}

}
