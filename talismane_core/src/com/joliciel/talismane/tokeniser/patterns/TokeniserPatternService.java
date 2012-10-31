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
package com.joliciel.talismane.tokeniser.patterns;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.joliciel.talismane.machineLearning.DecisionMaker;
import com.joliciel.talismane.tokeniser.Tokeniser;
import com.joliciel.talismane.tokeniser.TokeniserOutcome;
import com.joliciel.talismane.tokeniser.features.TokeniserContextFeature;

public interface TokeniserPatternService {
	public static final String PATTERN_DESCRIPTOR_KEY = "pattern";
	
	public TokeniserPatternManager getPatternManager(List<String> patternDescriptors);

	public Tokeniser getPatternTokeniser(TokeniserPatternManager patternManager,
			Set<TokeniserContextFeature<?>> tokeniserContextFeatures,
			DecisionMaker<TokeniserOutcome> decisionMaker, int beamWidth);

	public TokenPattern getTokeniserPattern(String regexp, Pattern separatorPattern);

}
