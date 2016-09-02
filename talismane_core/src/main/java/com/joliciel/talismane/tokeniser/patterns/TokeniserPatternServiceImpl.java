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
package com.joliciel.talismane.tokeniser.patterns;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.joliciel.talismane.TalismaneService;
import com.joliciel.talismane.machineLearning.ClassificationEventStream;
import com.joliciel.talismane.machineLearning.ClassificationModel;
import com.joliciel.talismane.machineLearning.DecisionMaker;
import com.joliciel.talismane.machineLearning.ExternalResource;
import com.joliciel.talismane.machineLearning.MachineLearningService;
import com.joliciel.talismane.machineLearning.features.FeatureService;
import com.joliciel.talismane.tokeniser.Token;
import com.joliciel.talismane.tokeniser.Tokeniser;
import com.joliciel.talismane.tokeniser.TokeniserAnnotatedCorpusReader;
import com.joliciel.talismane.tokeniser.TokeniserService;
import com.joliciel.talismane.tokeniser.features.TokenFeatureService;
import com.joliciel.talismane.tokeniser.features.TokenPatternMatchFeature;
import com.joliciel.talismane.tokeniser.features.TokeniserContextFeature;
import com.joliciel.talismane.tokeniser.filters.TokenFilterService;

class TokeniserPatternServiceImpl implements TokeniserPatternServiceInternal {
	private TokeniserService tokeniserService;
	private TokenFeatureService tokenFeatureService;
	private FeatureService featureService;
	private TokenFilterService tokenFilterService;
	private MachineLearningService machineLearningService;
	private TalismaneService talismaneService;

	@Override
	public TokeniserPatternManager getPatternManager(List<String> patternDescriptors) {
		TokeniserPatternManagerImpl patternManager = new TokeniserPatternManagerImpl(patternDescriptors);
		patternManager.setTokeniserPatternService(this);
		patternManager.setTokeniserService(this.getTokeniserService());
		return patternManager;
	}

	@Override
	public TokenPattern getTokeniserPattern(String regexp, Pattern separatorPattern) {
		TokenPatternImpl pattern = new TokenPatternImpl(regexp, separatorPattern);
		pattern.setTokeniserPatternServiceInternal(this);
		return pattern;
	}

	@Override
	public Tokeniser getIntervalPatternTokeniser(TokeniserPatternManager patternManager, Set<TokeniserContextFeature<?>> tokeniserContextFeatures,
			DecisionMaker decisionMaker, int beamWidth) {
		IntervalPatternTokeniser tokeniser = new IntervalPatternTokeniser(patternManager, tokeniserContextFeatures, beamWidth,
				talismaneService.getTalismaneSession());
		tokeniser.setTokeniserPatternService(this);
		tokeniser.setTokeniserService(this.getTokeniserService());
		tokeniser.setTokenFeatureService(this.getTokenFeatureService());
		tokeniser.setDecisionMaker(decisionMaker);
		tokeniser.setFeatureService(this.getFeatureService());
		tokeniser.setMachineLearningService(this.getMachineLearningService());
		return tokeniser;
	}

	@Override
	public Tokeniser getCompoundPatternTokeniser(TokeniserPatternManager patternManager, Set<TokenPatternMatchFeature<?>> features, DecisionMaker decisionMaker,
			int beamWidth) {
		CompoundPatternTokeniser tokeniser = new CompoundPatternTokeniser(patternManager, features, beamWidth, talismaneService.getTalismaneSession());
		tokeniser.setTokeniserPatternService(this);
		tokeniser.setTokeniserService(this.getTokeniserService());
		tokeniser.setTokenFeatureService(this.getTokenFeatureService());
		tokeniser.setDecisionMaker(decisionMaker);
		tokeniser.setFeatureService(this.getFeatureService());
		tokeniser.setMachineLearningService(this.getMachineLearningService());
		return tokeniser;

	}

	@Override
	public Tokeniser getPatternTokeniser(ClassificationModel tokeniserModel, int beamWidth) {
		Collection<ExternalResource<?>> externalResources = tokeniserModel.getExternalResources();
		if (externalResources != null) {
			for (ExternalResource<?> externalResource : externalResources) {
				this.getTokenFeatureService().getExternalResourceFinder().addExternalResource(externalResource);
			}
		}

		TokeniserPatternManager patternManager = this.getPatternManager(tokeniserModel.getDescriptors().get(TokeniserPatternService.PATTERN_DESCRIPTOR_KEY));

		PatternTokeniserType patternTokeniserType = PatternTokeniserType
				.valueOf((String) tokeniserModel.getModelAttributes().get(PatternTokeniserType.class.getSimpleName()));

		Tokeniser tokeniser = null;
		if (patternTokeniserType == PatternTokeniserType.Interval) {
			Set<TokeniserContextFeature<?>> tokeniserContextFeatures = this.getTokenFeatureService()
					.getTokeniserContextFeatureSet(tokeniserModel.getFeatureDescriptors(), patternManager.getParsedTestPatterns());

			tokeniser = this.getIntervalPatternTokeniser(patternManager, tokeniserContextFeatures, tokeniserModel.getDecisionMaker(), beamWidth);
		} else {
			Set<TokenPatternMatchFeature<?>> features = this.getTokenFeatureService().getTokenPatternMatchFeatureSet(tokeniserModel.getFeatureDescriptors());

			tokeniser = this.getCompoundPatternTokeniser(patternManager, features, tokeniserModel.getDecisionMaker(), beamWidth);
		}
		return tokeniser;
	}

	@Override
	public ClassificationEventStream getIntervalPatternEventStream(TokeniserAnnotatedCorpusReader corpusReader,
			Set<TokeniserContextFeature<?>> tokeniserContextFeatures, TokeniserPatternManager patternManager) {
		IntervalPatternEventStream eventStream = new IntervalPatternEventStream(corpusReader, tokeniserContextFeatures, talismaneService.getTalismaneSession());

		eventStream.setTokenFeatureService(this.getTokenFeatureService());
		eventStream.setTokeniserPatternService(this);
		eventStream.setMachineLearningService(this.getMachineLearningService());
		eventStream.setTokeniserService(this.getTokeniserService());
		eventStream.setTokeniserPatternManager(patternManager);
		eventStream.setTokenFilterService(this.getTokenFilterService());
		eventStream.setFeatureService(this.getFeatureService());
		return eventStream;
	}

	@Override
	public ClassificationEventStream getCompoundPatternEventStream(TokeniserAnnotatedCorpusReader corpusReader, Set<TokenPatternMatchFeature<?>> features,
			TokeniserPatternManager patternManager) {
		CompoundPatternEventStream eventStream = new CompoundPatternEventStream(corpusReader, features, talismaneService.getTalismaneSession());

		eventStream.setTokenFeatureService(this.getTokenFeatureService());
		eventStream.setTokeniserPatternService(this);
		eventStream.setMachineLearningService(this.getMachineLearningService());
		eventStream.setTokeniserService(this.getTokeniserService());
		eventStream.setTokeniserPatternManager(patternManager);
		eventStream.setTokenFilterService(this.getTokenFilterService());
		eventStream.setFeatureService(this.getFeatureService());
		return eventStream;

	}

	public TokeniserService getTokeniserService() {
		return tokeniserService;
	}

	public void setTokeniserService(TokeniserService tokeniserService) {
		this.tokeniserService = tokeniserService;
	}

	public TokenFeatureService getTokenFeatureService() {
		return tokenFeatureService;
	}

	public void setTokenFeatureService(TokenFeatureService tokenFeatureService) {
		this.tokenFeatureService = tokenFeatureService;
	}

	public FeatureService getFeatureService() {
		return featureService;
	}

	public void setFeatureService(FeatureService featureService) {
		this.featureService = featureService;
	}

	public TokenFilterService getTokenFilterService() {
		return tokenFilterService;
	}

	public void setTokenFilterService(TokenFilterService tokenFilterService) {
		this.tokenFilterService = tokenFilterService;
	}

	public MachineLearningService getMachineLearningService() {
		return machineLearningService;
	}

	public void setMachineLearningService(MachineLearningService machineLearningService) {
		this.machineLearningService = machineLearningService;
	}

	@Override
	public TokenPatternMatch getTokenPatternMatch(TokenPatternMatchSequence sequence, Token token, TokenPattern pattern, int index) {
		TokenPatternMatchImpl match = new TokenPatternMatchImpl(sequence, token, pattern, index);
		return match;
	}

	@Override
	public TokenPatternMatchSequence getTokenPatternMatchSequence(TokenPattern tokenPattern, List<Token> tokenSequence) {
		TokenPatternMatchSequenceImpl sequence = new TokenPatternMatchSequenceImpl(tokenPattern, tokenSequence);
		return sequence;
	}

	public TalismaneService getTalismaneService() {
		return talismaneService;
	}

	public void setTalismaneService(TalismaneService talismaneService) {
		this.talismaneService = talismaneService;
	}

}
