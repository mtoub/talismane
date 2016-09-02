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
package com.joliciel.talismane.tokeniser;

import java.io.Reader;

import com.joliciel.talismane.TalismaneService;
import com.joliciel.talismane.filters.Sentence;
import com.joliciel.talismane.tokeniser.features.TokenFeatureService;
import com.joliciel.talismane.tokeniser.filters.TokenFilterService;
import com.joliciel.talismane.tokeniser.patterns.TokeniserPatternManager;
import com.joliciel.talismane.tokeniser.patterns.TokeniserPatternService;

class TokeniserServiceImpl implements TokeniserServiceInternal {
	private TokenFeatureService tokenFeatureService;
	private TokeniserPatternService tokeniserPatternService;
	private TokenFilterService tokenFilterService;
	private TalismaneService talismaneService;

	@Override
	public Tokeniser getSimpleTokeniser() {
		SimpleTokeniser tokeniser = new SimpleTokeniser(talismaneService.getTalismaneSession());
		tokeniser.setTokeniserService(this);
		return tokeniser;
	}

	@Override
	public TokeniserEvaluator getTokeniserEvaluator(Tokeniser tokeniser) {
		TokeniserEvaluatorImpl evaluator = new TokeniserEvaluatorImpl();
		evaluator.setTokeniser(tokeniser);
		return evaluator;
	}

	@Override
	public <T extends TokenTag> TaggedTokenSequence<T> getTaggedTokenSequence(int initialCapacity) {
		TaggedTokenSequence<T> sequence = new TaggedTokenSequenceImpl<T>(initialCapacity);
		return sequence;
	}

	@Override
	public <T extends TokenTag> TaggedTokenSequence<T> getTaggedTokenSequence(TaggedTokenSequence<T> history) {
		TaggedTokenSequence<T> sequence = new TaggedTokenSequenceImpl<T>(history);
		return sequence;
	}

	@Override
	public TokenisedAtomicTokenSequence getTokenisedAtomicTokenSequence(Sentence sentence, int initialCapacity) {
		TokenisedAtomicTokenSequenceImpl sequence = new TokenisedAtomicTokenSequenceImpl(sentence, initialCapacity, talismaneService.getTalismaneSession());
		sequence.setTokeniserServiceInternal(this);
		return sequence;
	}

	@Override
	public TokenisedAtomicTokenSequence getTokenisedAtomicTokenSequence(TokenisedAtomicTokenSequence history) {
		TokenisedAtomicTokenSequence sequence = history.cloneSequence();
		return sequence;
	}

	public TokenFeatureService getTokenFeatureService() {
		return tokenFeatureService;
	}

	public void setTokenFeatureService(TokenFeatureService tokenFeatureService) {
		this.tokenFeatureService = tokenFeatureService;
	}

	public TokeniserPatternService getTokeniserPatternService() {
		return tokeniserPatternService;
	}

	public void setTokeniserPatternService(TokeniserPatternService tokeniserPatternService) {
		this.tokeniserPatternService = tokeniserPatternService;
	}

	@Override
	public TokenRegexBasedCorpusReader getRegexBasedCorpusReader(Reader reader) {
		TokenRegexBasedCorpusReaderImpl corpusReader = new TokenRegexBasedCorpusReaderImpl(reader, talismaneService.getTalismaneSession());
		corpusReader.setTokeniserService(this);
		corpusReader.setTokenFilterService(this.getTokenFilterService());
		return corpusReader;
	}

	public TokenFilterService getTokenFilterService() {
		return tokenFilterService;
	}

	public void setTokenFilterService(TokenFilterService tokenFilterService) {
		this.tokenFilterService = tokenFilterService;
	}

	@Override
	public TokenComparator getTokenComparator(TokeniserAnnotatedCorpusReader referenceCorpusReader, TokeniserAnnotatedCorpusReader evaluationCorpusReader,
			TokeniserPatternManager tokeniserPatternManager) {
		TokenComparatorImpl tokenComparator = new TokenComparatorImpl(referenceCorpusReader, evaluationCorpusReader, tokeniserPatternManager,
				talismaneService.getTalismaneSession());
		tokenComparator.setTokeniserServiceInternal(this);
		return tokenComparator;
	}

	public TalismaneService getTalismaneService() {
		return talismaneService;
	}

	public void setTalismaneService(TalismaneService talismaneService) {
		this.talismaneService = talismaneService;
	}
}
