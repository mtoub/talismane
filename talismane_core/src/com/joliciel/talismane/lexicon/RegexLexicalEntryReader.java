package com.joliciel.talismane.lexicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joliciel.talismane.TalismaneException;

/**
 * <p>A lexical entry reader where the lexical attributes are identified via a set of regex.</p>
 * <p>The set of regex is defined in a Scanner, where each line is a regex through a single lexical attribute
 * can be extracted. The first part of each line in the file is the attribute name which is a {@link LexicalAttribute}.</p>
 * <p>This is followed by a tab, and the regular expression. A third tab can be used to indicate the replacement value to be used,
 * when it is different from the one captured by the regular expression. Lines beginning with # are skipped.</p>
 * <p>Additional unrecognised attributes can be specified, in which case they will be retrievable via {@link LexicalEntry#getAttribute(String)}.</p>
 * <p>For example, if the input string looked like "word\tlemma\tcategory\tg=f|p=3|n=s" (for gender=female, person=3, number=singular), we might 
 * have the following input:</p>
 * <pre>
 * Word	$(.+)\t.+\t.+\t.+^
 * Lemma	$.+\t(.+)\t.+\t.+^
 * Category	$.+\t.+\t(.+)\t.+^
 * Gender	\bg=([fm])\b
 * Person	\bp=([123])\b
 * Number	\bn=([sp])\b
 * </pre>
 * <p>If your morphology part like this "3fs", we would need the following input:</p>
 * <pre>
 * Word	$(.+)\t.+\t.+\t.+^
 * Lemma	$.+\t(.+)\t.+\t.+^
 * Category	$.+\t.+\t(.+)\t.+^
 * Gender	\b[123]([mf])[ps]\b
 * Person	\b([123])[mf][ps]\b
 * Number	\b[123][mf]([ps])\b
 * </pre>
 * <p>By default, it is assumed the actual value is indicated by the first capturing group, and that only one value can be added per lexical attribute.</p>
 * <p>Various comma-separated modifiers can be added in parentheses after the attribute name. These include:</p>
 * <ul>
 * <li><b>group</b>: an integer indicating the capturing group (in case it's not group number 1)</li>
 * <li><b>stop</b>: true/false. If <tt>true</tt> (default), only the first regex to match will assign a value to the attribute. If false, another regex
 * for the same attribute can add additional values for this attribute.</li>
 * </ul>
 * <p>For example:</p>
 * <pre>
 * Gender(group=2,stop=false)	\b([123])([mf])[ps]\b
 * </pre>
 * <p>Indicates that the gender is found in the 2nd capturing group, and that further genders can be found if matched by other regex patterns
 * further downstream in the file.</p>
 * <p>By default, it is assumed there will be exactly one lexical entry per line.
 * If a lexical entry spans more than one line (e.g. an XML file where each entry spans multiple lines), there needs to be a special entry in the file called Entry, giving
 * the start of a new entry, as follows:</p>
 * <pre>
 * Entry	(&lt;entry&gt;)
 * </pre>
 * @author Assaf Urieli
 *
 */
public class RegexLexicalEntryReader implements LexicalEntryReader {
	private Map<LexicalAttribute,List<LexicalAttributePattern>> attributePatternMap = new HashMap<LexicalAttribute, List<LexicalAttributePattern>>();
	private Map<String,List<LexicalAttributePattern>> otherAttributeMap = new HashMap<String, List<LexicalAttributePattern>>();
	private List<LexicalAttributePattern> entryStartMap = new ArrayList<LexicalAttributePattern>();
	
	private LexicalEntryFactory lexicalEntryFactory;
	
	public RegexLexicalEntryReader(Scanner regexScanner) {
		this(regexScanner, null);
	}
	
	public RegexLexicalEntryReader(Scanner regexScanner, LexicalEntryFactory lexicalEntryFactory) {
		this.lexicalEntryFactory = lexicalEntryFactory;
		while (regexScanner.hasNextLine()) {
			String line = regexScanner.nextLine();
			if (line.length()>0 && !line.startsWith("#")) {
				String[] parts = line.split("\t");
				String attributeString = parts[0];
				Map<String,String> modifiers = new HashMap<String, String>();
				if (attributeString.indexOf('(')>=0) {
					String modifierString = attributeString.substring(attributeString.indexOf('(')+1, attributeString.lastIndexOf(')'));
					attributeString = attributeString.substring(0, attributeString.indexOf('('));
					String[] modifierParts = modifierString.split(",");
					for (String modifierPart : modifierParts) {
						String modifier = modifierPart.substring(0, modifierPart.indexOf('='));
						String value = modifierPart.substring(modifierPart.indexOf('=')+1);
						modifiers.put(modifier, value);
					}
				}
				
				boolean entryAttribute =(attributeString.equals("Entry"));

				LexicalAttribute attribute = null;
				String otherAttribute = null;
				
				if (!entryAttribute) {
					try {
						attribute = LexicalAttribute.valueOf(attributeString);
					} catch (IllegalArgumentException e) {
						otherAttribute = attributeString;
					}
				}
				
				Pattern pattern = Pattern.compile(parts[1], Pattern.UNICODE_CHARACTER_CLASS);
				
				int group = 1;
				if (modifiers.containsKey("group")) {
					group = Integer.parseInt(modifiers.get("group"));
				}
				
				boolean stop = true;
				if (modifiers.containsKey("stop")) {
					stop = modifiers.get("stop").equals("true");
				}
				
				String replacement = null;
				if (parts.length>2)
					replacement = parts[2];
				
				List<LexicalAttributePattern> patterns = null;
				if (entryAttribute) {
					patterns = entryStartMap;
				} else if (attribute!=null) {
					patterns = attributePatternMap.get(attribute);
					if (patterns==null) {
						patterns = new ArrayList<RegexLexicalEntryReader.LexicalAttributePattern>();
						attributePatternMap.put(attribute, patterns);
					}
				} else {
					patterns = otherAttributeMap.get(otherAttribute);
					if (patterns==null) {
						patterns = new ArrayList<RegexLexicalEntryReader.LexicalAttributePattern>();
						otherAttributeMap.put(otherAttribute, patterns);
					}
				}
				LexicalAttributePattern myPattern = new LexicalAttributePattern(pattern, group);
				myPattern.setStop(stop);
				myPattern.setReplacement(replacement);
				patterns.add(myPattern);
			}
		}
		regexScanner.close();
		
		if (!attributePatternMap.containsKey(LexicalAttribute.Word))
			throw new TalismaneException("A lexical entry must contain a Word attribute");
		
	}
	
	public LexicalEntry readEntry(String text) {
		if (this.lexicalEntryFactory==null)
			this.lexicalEntryFactory = new SimpleLexicalEntryFactory();
		
		WritableLexicalEntry lexicalEntry = this.lexicalEntryFactory.newLexicalEntry();

		boolean foundWord = false;
		for (LexicalAttribute attribute : this.attributePatternMap.keySet()) {
			for (LexicalAttributePattern myPattern : this.attributePatternMap.get(attribute)) {
				Matcher matcher = myPattern.getPattern().matcher(text);
				if (matcher.find()) {
					String value = matcher.group(myPattern.getGroup());
					if (myPattern.getReplacement()!=null)
						value = myPattern.getReplacement();
					
					switch (attribute) {
					case Word:
						lexicalEntry.setWord(value);
						foundWord = true;
						break;
					case Lemma:
						lexicalEntry.setLemma(value);
						break;
					case LemmaComplement:
						lexicalEntry.setLemmaComplement(value);
						break;
					case Morphology:
						lexicalEntry.setMorphology(value);
						break;
					case Category:
						lexicalEntry.setCategory(value);
						break;
					case SubCategory:
						lexicalEntry.setSubCategory(value);
						break;
					case Case:
						lexicalEntry.addCase(value);
						break;
					case Gender:
						lexicalEntry.addGender(value);
						break;
					case Number:
						lexicalEntry.addNumber(value);
						break;
					case Person:
						lexicalEntry.addPerson(value);
						break;
					case PossessorNumber:
						lexicalEntry.addPossessorNumber(value);
						break;
					case Tense:
						lexicalEntry.addTense(value);
						break;
					case Aspect:
						lexicalEntry.addAspect(value);
						break;
					case Mood:
						lexicalEntry.addMood(value);
						break;
					}
					
					if (myPattern.isStop())
						break;
				} // match found?
			} // next pattern
		} // next attribute
		
		for (String otherAttribute : this.otherAttributeMap.keySet()) {
			for (LexicalAttributePattern myPattern : this.otherAttributeMap.get(otherAttribute)) {
				Matcher matcher = myPattern.getPattern().matcher(text);
				if (matcher.find()) {
					String value = matcher.group(myPattern.getGroup());
					lexicalEntry.setAttribute(otherAttribute, value);
					
					if (myPattern.isStop())
						break;
				} // match found?
			} // next pattern
		} // next other attribute
		
		if (!foundWord)
			throw new TalismaneException("No Word found in lexical entry: " + text);
		
		return lexicalEntry;
	}

	private static final class LexicalAttributePattern {
		private Pattern pattern;
		private int group;
		private boolean stop = true;
		private String replacement = null;
		
		public LexicalAttributePattern(Pattern pattern, int group) {
			super();
			this.pattern = pattern;
			this.group = group;
		}

		public Pattern getPattern() {
			return pattern;
		}

		public int getGroup() {
			return group;
		}

		public boolean isStop() {
			return stop;
		}

		public void setStop(boolean stop) {
			this.stop = stop;
		}

		public String getReplacement() {
			return replacement;
		}

		public void setReplacement(String replacement) {
			this.replacement = replacement;
		}
	
	}

	public LexicalEntryFactory getLexicalEntryFactory() {
		return lexicalEntryFactory;
	}


	public void setLexicalEntryFactory(LexicalEntryFactory lexicalEntryFactory) {
		this.lexicalEntryFactory = lexicalEntryFactory;
	}
	
	
}