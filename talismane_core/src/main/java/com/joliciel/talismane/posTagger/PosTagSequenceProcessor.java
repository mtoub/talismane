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
package com.joliciel.talismane.posTagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.joliciel.talismane.Talismane;
import com.joliciel.talismane.Talismane.BuiltInTemplate;
import com.joliciel.talismane.TalismaneException;
import com.joliciel.talismane.TalismaneSession;
import com.joliciel.talismane.output.FreemarkerTemplateWriter;
import com.joliciel.talismane.utils.ConfigUtils;
import com.typesafe.config.Config;

/**
 * Any class that can process pos-tag sequences generated by the pos-tagger or
 * by manual annotation.
 * 
 * @author Assaf Urieli
 *
 */
public interface PosTagSequenceProcessor extends Closeable {
	public enum ProcessorType {
		output,
		posTagFeatureTester
	}

	/**
	 * Process the next pos-tag sequence.
	 * 
	 * @throws TalismaneException
	 */
	public void onNextPosTagSequence(PosTagSequence posTagSequence) throws TalismaneException;

	/**
	 * Called when analysis is complete.
	 */
	public void onCompleteAnalysis();

	/**
	 * 
	 * @param writer
	 *            if provided, the main processor will write to this writer, if
	 *            null, the outDir will be used instead
	 * @param outDir
	 * @param session
	 * @return
	 * @throws IOException
	 */
	public static List<PosTagSequenceProcessor> getProcessors(Writer writer, File outDir, TalismaneSession session) throws IOException {
		List<PosTagSequenceProcessor> processors = new ArrayList<>();

		Config config = session.getConfig();
		Config posTaggerConfig = config.getConfig("talismane.core.pos-tagger");

		PosTagSequenceProcessor processor = null;
		List<ProcessorType> processorTypes = posTaggerConfig.getStringList("output.processors").stream().map(f -> ProcessorType.valueOf(f))
				.collect(Collectors.toList());

		if (outDir != null)
			outDir.mkdirs();

		for (ProcessorType type : processorTypes) {
			switch (type) {
			case output: {
				Reader templateReader = null;
				String configPath = "talismane.core.pos-tagger.output.template";
				if (config.hasPath(configPath)) {
					templateReader = new BufferedReader(new InputStreamReader(ConfigUtils.getFileFromConfig(config, configPath)));
				} else {
					String templateName = null;
					BuiltInTemplate builtInTemplate = BuiltInTemplate.valueOf(posTaggerConfig.getString("output.built-in-template"));
					switch (builtInTemplate) {
					case standard:
						templateName = "posTagger_template.ftl";
						break;
					case with_location:
						templateName = "posTagger_template_with_location.ftl";
						break;
					case with_prob:
						templateName = "posTagger_template_with_prob.ftl";
						break;
					case with_comments:
						templateName = "posTagger_template_with_comments.ftl";
						break;
					default:
						throw new RuntimeException("Unknown builtInTemplate for pos-tagger: " + builtInTemplate.name());
					}

					String path = "output/" + templateName;
					InputStream inputStream = Talismane.class.getResourceAsStream(path);
					if (inputStream == null)
						throw new IOException("Resource not found in classpath: " + path);
					templateReader = new BufferedReader(new InputStreamReader(inputStream));
				}

				if (writer == null) {
					File file = new File(outDir, session.getBaseName() + "_pos.txt");
					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), session.getOutputCharset()));
				}

				processor = new FreemarkerTemplateWriter(templateReader, writer);
				processors.add(processor);
				break;
			}
			case posTagFeatureTester: {
				File file = new File(outDir, session.getBaseName() + "_posTagFeatureTest.txt");
				Writer featureWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), session.getOutputCharset()));
				processor = new PosTagFeatureTester(session, featureWriter);
				processors.add(processor);
				break;
			}
			}
		}
		return processors;
	}
}
