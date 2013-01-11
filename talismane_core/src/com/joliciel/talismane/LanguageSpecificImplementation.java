//Copyright (C) 2012 Assaf Urieli
package com.joliciel.talismane;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import com.joliciel.talismane.parser.TransitionSystem;
import com.joliciel.talismane.posTagger.PosTagSet;
import com.joliciel.talismane.posTagger.PosTaggerLexicon;
import com.joliciel.talismane.tokeniser.filters.TokenSequenceFilter;

/**
 * An implementation of Talismane for a specific language.
 * @author Assaf Urieli
 *
 */
public interface LanguageSpecificImplementation {
	/**
	 * The default transition system for this language.
	 * @return
	 */
	public TransitionSystem getDefaultTransitionSystem();

	/**
	 * Return a stream containing the default text maker filter descriptors.
	 * @return
	 */
	public InputStream getDefaultTextMarkerFiltersFromStream();

	/**
	 * Return a stream containing the default token filter descriptors.
	 * @return
	 */
	public InputStream getDefaultTokenFiltersFromStream();

	/**
	 * A list of filters to be applied to the atomic token sequences
	 * prior to tokenisation.
	 * @return
	 */
	public List<TokenSequenceFilter> getTokenSequenceFilters();

	/**
	 * A list of filters to be applied to token sequences generated by the tokeniser
	 * prior to pos-tagging.
	 * @return
	 */
	public List<TokenSequenceFilter> getPosTaggerPreprocessingFilters();

	/**
	 * Return the lexicon service for this language.
	 * @return
	 */
	public PosTaggerLexicon getDefaultLexiconService();
	
	/**
	 * Return the postag set for this language.
	 * @return
	 */
	public PosTagSet getDefaultPosTagSet();

	/**
	 * Return a stream containing the default pos-tag set descriptors.
	 * @return
	 */
	public InputStream getDefaultPosTagSetFromStream();
	
	/**
	 * Return a stream containing the default pos tagger rule descriptors.
	 * @return
	 */
	public InputStream getDefaultPosTaggerRulesFromStream();
	
	/**
	 * Return a stream containing the default parser rule descriptors.
	 * @return
	 */
	public InputStream getDefaultParserRulesFromStream();

	/**
	 * Return a ZipInputStream containing the default sentence model for this language.
	 * @return
	 */
	public ZipInputStream getDefaultSentenceModelStream();

	/**
	 * Return a ZipInputStream containing the default tokeniser model for this language.
	 * @return
	 */
	public ZipInputStream getDefaultTokeniserModelStream();

	/**
	 * Return a ZipInputStream containing the default pos-tagger model for this language.
	 * @return
	 */
	public ZipInputStream getDefaultPosTaggerModelStream();

	/**
	 * Return a ZipInputStream containing the default parser model for this language.
	 * @return
	 */
	public ZipInputStream getDefaultParserModelStream();

}