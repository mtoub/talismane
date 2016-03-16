///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2015 Joliciel Informatique
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
package com.joliciel.talismane;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import com.joliciel.talismane.lexicon.Diacriticizer;
import com.joliciel.talismane.lexicon.LexicalEntryReader;
import com.joliciel.talismane.lexicon.PosTaggerLexicon;
import com.joliciel.talismane.machineLearning.ClassificationModel;
import com.joliciel.talismane.machineLearning.MachineLearningModel;
import com.joliciel.talismane.parser.TransitionSystem;
import com.joliciel.talismane.posTagger.PosTagSet;
import com.joliciel.talismane.posTagger.filters.PosTagSequenceFilter;
import com.joliciel.talismane.tokeniser.filters.TokenSequenceFilter;

/**
 * An implementation of Talismane for a specific language.
 * @author Assaf Urieli
 *
 */
public interface LanguageImplementation {
	/**
	 * The locale which this language implementation represents.
	 */
	public Locale getLocale();
	
	/**
	 * The default transition system for this language.
	 */
	public TransitionSystem getDefaultTransitionSystem();
	
	/**
	 * The default linguistic rules for this language.
	 */
	public LinguisticRules getDefaultLinguisticRules();

	/**
	 * Return a scanner containing the default text marker filter descriptors.
	 */
	public Scanner getDefaultTextMarkerFiltersScanner();

	/**
	 * Return a scanner containing the default token filter descriptors.
	 */
	public Scanner getDefaultTokenFiltersScanner();

	/**
	 * Returns a scanner containing descriptors for filters to be applied to token sequences generated by the tokeniser
	 * prior to pos-tagging.
	 */
	public Scanner getDefaultTokenSequenceFiltersScanner();

	/**
	 * A list of filters to be applied to pos-tag sequences generated by the pos-tagger
	 * after pos-tagging.
	 */
	public List<PosTagSequenceFilter> getDefaultPosTagSequenceFilters();
	
	/**
	 * Return the postag set for this language.
	 */
	public PosTagSet getDefaultPosTagSet();

	/**
	 * Return a scanner containing the default pos tagger rule descriptors.
	 */
	public Scanner getDefaultPosTaggerRulesScanner();
	
	/**
	 * Return a scanner containing the default parser rule descriptors.
	 */
	public Scanner getDefaultParserRulesScanner();

	/**
	 * The default sentence model for this language.
	 */
	public ClassificationModel getDefaultSentenceModel();

	/**
	 * The default tokeniser model for this language.
	 */
	public ClassificationModel getDefaultTokeniserModel();

	/**
	 * The default pos-tagger model for this language.
	 */
	public ClassificationModel getDefaultPosTaggerModel();

	/**
	 * The default parser model for this language.
	 */
	public MachineLearningModel getDefaultParserModel();
	
	/**
	 * Returns the default lexicons for this language,
	 * already in the correct order.
	 */
	public List<PosTaggerLexicon> getDefaultLexicons();
	
	/**
	 * Returns a list of TokenSequenceFilters available for this language.
	 */
	public List<Class<? extends TokenSequenceFilter>> getAvailableTokenSequenceFilters();
	
	/**
	 * A reader for extracting lexical information from a previously analysed corpus.
	 */
	public LexicalEntryReader getDefaultCorpusLexicalEntryReader();
	
	/**
	 * A diacriticizer.
	 */
	public Diacriticizer getDiacriticizer();
	
	/**
	 * A map of preferences in uppercase-to-lowercase mappings, in cases where ambiguities exist.
	 */
	public Map<String,String> getLowercasePreferences();
}