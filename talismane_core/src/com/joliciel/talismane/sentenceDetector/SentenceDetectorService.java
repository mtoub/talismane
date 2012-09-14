///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2012 Assaf Urieli
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
package com.joliciel.talismane.sentenceDetector;

import java.util.List;
import java.util.Set;

import com.joliciel.talismane.filters.TextFilter;
import com.joliciel.talismane.sentenceDetector.features.SentenceDetectorFeature;
import com.joliciel.talismane.utils.CorpusEventStream;
import com.joliciel.talismane.utils.DecisionMaker;

public interface SentenceDetectorService {
	
	public SentenceDetector getSentenceDetector(DecisionMaker decisionMaker,
			Set<SentenceDetectorFeature<?>> features);
	
	public SentenceDetectorEvaluator getEvaluator(SentenceDetector sentenceDetector);
	
	public PossibleSentenceBoundary getPossibleSentenceBoundary(String text, int index);
	
	public CorpusEventStream getSentenceDetectorEventStream(SentenceDetectorAnnotatedCorpusReader corpusReader,
			Set<SentenceDetectorFeature<?>> features, List<TextFilter> filters);

}
