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
package com.joliciel.talismane.tokeniser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.joliciel.talismane.TalismaneSession;
import com.joliciel.talismane.filters.Sentence;
import com.joliciel.talismane.machineLearning.features.Feature;
import com.joliciel.talismane.machineLearning.features.FeatureResult;
import com.joliciel.talismane.posTagger.PosTag;
import com.joliciel.talismane.tokeniser.patterns.TokenMatch;

class TokenImpl implements TokenInternal {
	@SuppressWarnings("unused")
	private static final Log LOG = LogFactory.getLog(TokenImpl.class);
	private static Pattern whiteSpacePattern = Pattern.compile("\\s+");

	private String text;
	private String originalText;
	private int index;
	private int indexWithWhiteSpace;
	private TokenSequence tokenSequence;
	private Set<PosTag> possiblePosTags;
	private Map<PosTag, Integer> frequencies;
	private Map<String,FeatureResult<?>> featureResults = new HashMap<String, FeatureResult<?>>();
	private boolean separator = false;
	private boolean whiteSpace = false;
	private List<TokenMatch> matches = null;
	private List<TaggedToken<TokeniserOutcome>> atomicDecisions = new ArrayList<TaggedToken<TokeniserOutcome>>();
	private boolean logged = false;
	
	private int startIndex = 0;
	private int endIndex = 0;
	
	private String fileName = null;
	private Integer lineNumber = null;
	private Integer columnNumber = null;
	
	TokenImpl(TokenImpl tokenToClone) {
		this.text = tokenToClone.text;
		this.originalText = tokenToClone.originalText;
		this.index = tokenToClone.index;
		this.indexWithWhiteSpace = tokenToClone.indexWithWhiteSpace;
		this.tokenSequence = tokenToClone.tokenSequence;
		this.possiblePosTags = tokenToClone.possiblePosTags;
		this.frequencies = tokenToClone.frequencies;
		this.featureResults = tokenToClone.featureResults;
		this.separator = tokenToClone.separator;
		this.whiteSpace = tokenToClone.whiteSpace;
		this.matches = tokenToClone.matches;
		this.atomicDecisions = tokenToClone.atomicDecisions;
		this.logged = tokenToClone.logged;
		this.startIndex = tokenToClone.startIndex;
		this.endIndex = tokenToClone.endIndex;
		this.fileName = tokenToClone.fileName;
		this.lineNumber = tokenToClone.lineNumber;
		this.columnNumber = tokenToClone.columnNumber;
	}
	
	TokenImpl(String text, TokenSequence tokenSequence) {
		this(text, tokenSequence, 0);
	}
	
	TokenImpl(String text, TokenSequence tokenSequence, int index) {
		this.text = text;
		this.originalText = text;
		this.tokenSequence = tokenSequence;
		this.index = index;
		if (text.length()==0)
			this.whiteSpace = false;
		else if (whiteSpacePattern.matcher(text).matches())
			this.whiteSpace = true;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getOriginalText() {
		return originalText;
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndexWithWhiteSpace() {
		return indexWithWhiteSpace;
	}

	public void setIndexWithWhiteSpace(int indexWithWhiteSpace) {
		this.indexWithWhiteSpace = indexWithWhiteSpace;
	}

	public TokenSequence getTokenSequence() {
		return tokenSequence;
	}
	public void setTokenSequence(TokenSequence tokenSequence) {
		this.tokenSequence = tokenSequence;
	}
	@Override
	public String toString() {
		return this.getText();
	}
	@Override
	public Set<PosTag> getPossiblePosTags() {
		if (possiblePosTags==null) {
			possiblePosTags = TalismaneSession.getLexicon().findPossiblePosTags(this.getText());
		}
		
		return possiblePosTags;
	}
	
	@Override
	public Map<PosTag, Integer> getFrequencies() {
		return frequencies;
	}
	@Override
	public void setFrequencies(Map<PosTag, Integer> frequencies) {
		this.frequencies = frequencies;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public <T, Y> FeatureResult<Y> getResultFromCache(Feature<T, Y> feature) {
		FeatureResult<Y> result = null;
		
		if (this.featureResults.containsKey(feature.getName())) {
			result = (FeatureResult<Y>) this.featureResults.get(feature.getName());
		}
		return result;
	}

	@Override
	public <T, Y> void putResultInCache(Feature<T, Y> feature,
			FeatureResult<Y> featureResult) {
		this.featureResults.put(feature.getName(), featureResult);	
	}

	@Override
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	@Override
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
	public boolean isSeparator() {
		return separator;
	}
	public void setSeparator(boolean separator) {
		this.separator = separator;
	}
	public boolean isWhiteSpace() {
		return whiteSpace;
	}

	public List<TokenMatch> getMatches() {
		if (matches==null)
			matches = new ArrayList<TokenMatch>();
		return matches;
	}

	@Override
	public int compareTo(Token o) {
		return this.getStartIndex() - o.getStartIndex();
	}

	@Override
	public List<TaggedToken<TokeniserOutcome>> getAtomicParts() {
		return atomicDecisions;
	}

	public void setAtomicParts(
			List<TaggedToken<TokeniserOutcome>> atomicDecisions) {
		this.atomicDecisions = atomicDecisions;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	@Override
	public Token getToken() {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((originalText == null) ? 0 : originalText.hashCode());
		result = prime * result + startIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TokenImpl other = (TokenImpl) obj;
		if (originalText == null) {
			if (other.originalText != null)
				return false;
		} else if (!originalText.equals(other.originalText))
			return false;
		if (startIndex != other.startIndex)
			return false;
		return true;
	}

	@Override
	public boolean isEmpty() {
		return this.getStartIndex()==this.getEndIndex();
	}

	@Override
	public int getOriginalIndex() {
		return this.getTokenSequence().getSentence().getOriginalIndex(this.startIndex);
	}

	@Override
	public int getLineNumber() {
		if (this.lineNumber==null) {
			this.lineNumber = this.getTokenSequence().getSentence().getLineNumber(this.getOriginalIndex());
		}
		return this.lineNumber;
	}
	@Override
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}


	@Override
	public int getColumnNumber() {
		if (this.columnNumber==null) {
			this.columnNumber = this.getTokenSequence().getSentence().getColumnNumber(this.getOriginalIndex());
		}
		return this.columnNumber;
	}
	@Override
	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}

	@Override
	public int getOriginalIndexEnd() {
		return this.getTokenSequence().getSentence().getOriginalIndex(this.endIndex);
	}

	@Override
	public int getLineNumberEnd() {
		return this.getTokenSequence().getSentence().getLineNumber(this.getOriginalIndexEnd());
	}
	
	@Override
	public int getColumnNumberEnd() {
		return this.getTokenSequence().getSentence().getColumnNumber(this.getOriginalIndexEnd());
	}
	
	@Override
	public String getFileName() {
		if (this.fileName==null) {
			this.fileName = this.getTokenSequence().getSentence().getFileName();
		}
		return this.fileName;
	}
	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getPrecedingRawOutput() {
		Sentence sentence = this.getTokenSequence().getSentence();
		Entry<Integer, String> textSegment = sentence.getPrecedingOriginalTextSegment(startIndex);
		if (textSegment==null)
			return null;
		if (this.index==0)
			return textSegment.getValue();
		Token previousToken = this.getTokenSequence().get(index-1);
		if (previousToken.getStartIndex()>=textSegment.getKey())
			return null;
		return textSegment.getValue();
	}

	@Override
	public Token cloneToken() {
		TokenImpl token = new TokenImpl(this);
		return token;
	}
	
	
}
