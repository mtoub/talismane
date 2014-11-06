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
package com.joliciel.talismane.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


import com.joliciel.talismane.utils.CSVFormatter;
import com.joliciel.talismane.utils.LogUtils;

public class FScoreCalculatorOneVsRest<E extends Comparable<E>> {
	private static final Log LOG = LogFactory.getLog(FScoreCalculator.class);
	private static final CSVFormatter CSV = new CSVFormatter();
	
	private Map<E,FScoreCalculator<Boolean>> fScoreCalculators = new TreeMap<E, FScoreCalculator<Boolean>>();
	
	private int eventCount = 0;
	private Map<E,Integer> outcomeCounts = new TreeMap<E, Integer>();
	
	public void nextEvent() {
		eventCount++;
	}
	
	public void increment(E outcome, boolean expected, boolean guessed) {
		FScoreCalculator<Boolean> fScoreCalculator = fScoreCalculators.get(outcome);
		if (fScoreCalculator==null) {
			fScoreCalculator = new FScoreCalculator<Boolean>(outcome.toString());
			fScoreCalculators.put(outcome, fScoreCalculator);
		}
		fScoreCalculator.increment(expected, guessed);
		if (expected) {
			if (!outcomeCounts.containsKey(outcome))
				outcomeCounts.put(outcome, 0);
			int count = outcomeCounts.get(outcome);
			outcomeCounts.put(outcome, count+1);
		}
	}
	
	public void writeScoresToCSV(Writer fscoreFileWriter) {
		try {
			fscoreFileWriter.write(CSV.format("outcome"));
			fscoreFileWriter.write(CSV.format("count")
					+ CSV.format("true+")
					+ CSV.format("false+")
					+ CSV.format("false-")
					+ CSV.format("precision")
					+ CSV.format("recall")
					+ CSV.format("f-score")
					+ CSV.format("accuracy")
					);
			fscoreFileWriter.write("\n");
			
			DescriptiveStatistics precisionStats = new DescriptiveStatistics();
			DescriptiveStatistics recallStats = new DescriptiveStatistics();
			DescriptiveStatistics fScoreStats = new DescriptiveStatistics();
			
			DescriptiveStatistics precisionWeightedStats = new DescriptiveStatistics();
			DescriptiveStatistics recallWeightedStats = new DescriptiveStatistics();
			DescriptiveStatistics fScoreWeightedStats = new DescriptiveStatistics();
			
			for (E outcome : fScoreCalculators.keySet()) {
				fscoreFileWriter.write(CSV.format(outcome.toString()));
				FScoreCalculator<Boolean> fScoreCalculator = fScoreCalculators.get(outcome);
				if (!outcomeCounts.containsKey(outcome))
					outcomeCounts.put(outcome, 0);
				
				int count = outcomeCounts.get(outcome);
				fscoreFileWriter.write(CSV.format(count));			
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getTruePositiveCount(true)));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getFalsePositiveCount(true)));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getFalseNegativeCount(true)));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getPrecision(true)*100));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getRecall(true)*100));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getFScore(true)*100));
				fscoreFileWriter.write(CSV.format(fScoreCalculator.getAccuracy()*100));
				fscoreFileWriter.write("\n");
				fscoreFileWriter.flush();
				
				if (count>0) {
					precisionStats.addValue(fScoreCalculator.getPrecision(true)*100);
					recallStats.addValue(fScoreCalculator.getRecall(true)*100);
					fScoreStats.addValue(fScoreCalculator.getFScore(true)*100);
				}
				for (int i=0; i<count; i++) {
					precisionWeightedStats.addValue(fScoreCalculator.getPrecision(true)*100);
					recallWeightedStats.addValue(fScoreCalculator.getRecall(true)*100);
					fScoreWeightedStats.addValue(fScoreCalculator.getFScore(true)*100);
				}
			}
			
			fscoreFileWriter.write(CSV.format("TOTAL"));
			fscoreFileWriter.write(CSV.format(eventCount));
			fscoreFileWriter.write("\n");
			
			fscoreFileWriter.write(CSV.format("WEIGHTED AVERAGE"));
			fscoreFileWriter.write(CSV.format("")
					+ CSV.format("")
					+ CSV.format("")
					+ CSV.format("")
					+ CSV.format(precisionWeightedStats.getMean())
					+ CSV.format(recallWeightedStats.getMean())
					+ CSV.format(fScoreWeightedStats.getMean())
					);
			fscoreFileWriter.write("\n");

			fscoreFileWriter.write(CSV.format("AVERAGE"));
			fscoreFileWriter.write(CSV.format("")
					+ CSV.format("")
					+ CSV.format("")
					+ CSV.format("")
					+ CSV.format(precisionStats.getMean())
					+ CSV.format(recallStats.getMean())
					+ CSV.format(fScoreStats.getMean())
					);
			fscoreFileWriter.write("\n");
			
			fscoreFileWriter.flush();
		} catch (IOException ioe) {
			LogUtils.logError(LOG, ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public void writeScoresToCSVFile(File fscoreFile) {
		try {
			fscoreFile.delete();
			fscoreFile.createNewFile();
			Writer fscoreFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fscoreFile, false),"UTF8"));
			try {
				this.writeScoresToCSV(fscoreFileWriter);
			} finally {
				fscoreFileWriter.flush();
				fscoreFileWriter.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}