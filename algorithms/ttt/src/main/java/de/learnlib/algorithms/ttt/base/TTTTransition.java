/* Copyright (C) 2014 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.learnlib.algorithms.ttt.base;

import de.learnlib.api.AccessSequenceProvider;
import de.learnlib.datastructure.list.IntrusiveListElem;
import de.learnlib.datastructure.list.IntrusiveListElemImpl;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;

import java.io.Serializable;

/**
 * A transition in a {@link TTTHypothesis}.
 * 
 * @author Malte Isberner
 *
 * @param <I> input symbol type
 */
public class TTTTransition<I,D> extends IntrusiveListElemImpl<TTTTransition<I,D>> implements
		AccessSequenceProvider<I>,
		Serializable {
	
	private final TTTState<I,D> source;
	private final I input;

	// TREE TRANSITION
	private TTTState<I,D> treeTarget;
	
	// NON-TREE TRANSITION
	BaseDTNode<I,D> nonTreeTarget;
	
	protected IntrusiveListElem<TTTTransition<I, D>> prevIncoming;
	

	public TTTTransition(TTTState<I,D> source, I input) {
		this.source = source;
		this.input = input;
	}
	
	
	public boolean isTree() {
		return (treeTarget != null);
	}
	
	public TTTState<I,D> getTreeTarget() {
		assert isTree();
		
		return treeTarget;
	}
	
	public BaseDTNode<I,D> getNonTreeTarget() {
		assert !isTree();
		
		return nonTreeTarget;
	}
	
	public BaseDTNode<I,D> getDTTarget() {
		if(treeTarget != null) {
			return treeTarget.dtLeaf;
		}
		return nonTreeTarget;
	}
	
	
	public TTTState<I,D> getTarget() {
		if(treeTarget != null) {
			return treeTarget;
		}
		
		assert nonTreeTarget.isLeaf() : "transition target is not a leaf, but is a " + (nonTreeTarget.isTemp() ? "temp" : "non-temp") + " node with discr" + nonTreeTarget.getDiscriminator();
		assert nonTreeTarget.getData() != null;
		return nonTreeTarget.getData();
	}
	
	public TTTState<I,D> getSource() {
		return source;
	}
	
	public I getInput() {
		return input;
	}
	
	protected Object getProperty() {
		return null;
	}
	
	@Override
	public Word<I> getAccessSequence() {
		WordBuilder<I> wb = new WordBuilder<>(); // FIXME capacity hint
		
		TTTTransition<I,D> curr = this;
		
		while(curr != null) {
			wb.add(curr.input);
			curr = curr.source.getParentTransition();
		}
		
		return wb.reverse().toWord();
	}
	
	void makeTree(TTTState<I,D> treeTarget) {
		removeFromList();
		this.treeTarget = treeTarget;
		this.nonTreeTarget = null;
	}
	
	void setNonTreeTarget(BaseDTNode<I,D> nonTreeTarget) {
		this.nonTreeTarget = nonTreeTarget;
		nonTreeTarget.getIncoming().insertIncoming(this);
	}
	
	
	void removeFromList() {
		if(prevIncoming != null) {
			prevIncoming.setNextElement(next);
		}
		if(next != null) {
			next.prevIncoming = prevIncoming;
		}
	}
}
