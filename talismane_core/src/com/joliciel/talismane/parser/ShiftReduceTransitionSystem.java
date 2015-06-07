///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2014 Joliciel Informatique
//
//This file is part of Talismane.
//
//Talismane is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Talismane is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with Talismane.  If not, see <http://www.gnu.org/licenses/>.
//////////////////////////////////////////////////////////////////////////////
package com.joliciel.talismane.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joliciel.talismane.TalismaneException;
import com.joliciel.talismane.posTagger.PosTag;
import com.joliciel.talismane.posTagger.PosTaggedToken;

/**
 * The arc-standard Shift-Reduce transition system, as described in Nivre 2009 Dependency Parsing, Chapter 3,
 * augmented with an explicit Reduce transition to allow for corpora with unattached nodes: artificially attaching these nodes
 * to the root node could induce non-projectivity.
 * @author Assaf Urieli
 *
 */
class ShiftReduceTransitionSystem extends AbstractTransitionSystem {
	private static final long serialVersionUID = -6536246443810297657L;
	private static final Log LOG = LogFactory.getLog(ShiftReduceTransitionSystem.class);
	private transient Set<Transition> transitions = null;

	@Override
	public void predictTransitions(ParseConfiguration configuration,
			Set<DependencyArc> targetDependencies) {
		LOG.debug("predictTransitions");
		LOG.debug(configuration.getSentence().getText());
		LOG.debug(configuration);
		LOG.debug(targetDependencies);
		
		Map<PosTaggedToken,DependencyArc> ungovernedTokens = new HashMap<PosTaggedToken,DependencyArc>();
		
		for (DependencyArc arc : targetDependencies) {
			if (arc.getHead().getTag().equals(PosTag.ROOT_POS_TAG)&& (arc.getLabel()==null || arc.getLabel().length()==0)) {
				ungovernedTokens.put(arc.getDependent(), arc);
			}
		}
		
		while (!configuration.getBuffer().isEmpty()) {
			PosTaggedToken stackHead = configuration.getStack().peek();
			PosTaggedToken bufferHead = configuration.getBuffer().peekFirst();
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("S0: " + stackHead);
				LOG.trace("B0: " + bufferHead);
			}

			Transition transition = null;
			DependencyArc currentDep = null;
			for (DependencyArc arc : targetDependencies) {
				if (arc.getHead().equals(bufferHead)&&arc.getDependent().equals(stackHead)) {
					try {
						transition = this.getTransitionForCode("LeftArc[" + arc.getLabel() + "]");
					} catch (UnknownDependencyLabelException udle) {
						throw new UnknownDependencyLabelException(arc.getDependent().getIndex(), arc.getLabel());
					}
					currentDep = arc;
					break;
				}
				
				if (arc.getHead().equals(stackHead)&&arc.getDependent().equals(bufferHead)) {
					boolean dependentHasDependents = false;
					for (DependencyArc otherArc : targetDependencies) {
						if (otherArc.getHead().equals(bufferHead)) {
							dependentHasDependents = true;
							break;
						}
					}
					if (!dependentHasDependents) {
						try {
							transition = this.getTransitionForCode("RightArc[" + arc.getLabel() + "]");
						} catch (UnknownDependencyLabelException udle) {
							throw new UnknownDependencyLabelException(arc.getDependent().getIndex(), arc.getLabel());
						}

						currentDep = arc;
						break;
					}
				}

			}
			if (transition==null) {
				boolean stackHeadUngoverned = ungovernedTokens.containsKey(stackHead);
				if (stackHeadUngoverned) {
					// ungoverned punctuation only
					transition = this.getTransitionForCode("ForceReduce");
					currentDep = ungovernedTokens.get(stackHead);
				} else {
					transition =  this.getTransitionForCode("Shift");
				}
			}
			if (currentDep!=null)
				targetDependencies.remove(currentDep);
			
			transition.apply(configuration);
			
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("Transition: " + transition);
				LOG.trace("Configuration: " + configuration);
			}
		}
		if (targetDependencies.size()>0) {
			throw new RuntimeException("Wasn't able to predict: " + targetDependencies);
		}
		LOG.debug("Full prediction complete");
	}

	@Override
	public Transition getTransitionForCode(String code) {
		AbstractTransition transition = null;
		String label = null;
		if (code.indexOf('[')>=0) {
			label = code.substring(code.indexOf('[')+1, (code.indexOf(']')));
			if (this.getDependencyLabels().size()>0 && !this.getDependencyLabels().contains(label)) {
				throw new UnknownDependencyLabelException(label);
			}
		}
		if (code.startsWith("LeftArc")) {
			transition = new LeftArcTransition(label);
		} else if (code.startsWith("RightArc")) {
			transition = new RightArcTransition(label);
		} else if (code.startsWith("Shift")) {
			transition = new ShiftTransition();
		} else if (code.startsWith("ForceReduce")) {
			transition = new ForceReduceTransition();
		} else {
			throw new TalismaneException("Unknown transition name: " + code);
		}
		
		return transition;
	}
	
	@Override
	public Set<Transition> getTransitions() {
		if (transitions==null) {
			transitions = new TreeSet<Transition>();
			transitions.add(this.getTransitionForCode("Shift"));
			for (String dependencyLabel : this.getDependencyLabels()) {
				transitions.add(this.getTransitionForCode("LeftArc[" + dependencyLabel + "]"));
				transitions.add(this.getTransitionForCode("RightArc[" + dependencyLabel + "]"));
			}
		}
		return transitions;
	}

}
