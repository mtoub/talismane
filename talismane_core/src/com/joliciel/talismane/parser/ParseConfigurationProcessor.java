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
package com.joliciel.talismane.parser;

import java.io.Writer;


/**
* Any class that can process parse configurations generated by the parser.
* Note that implementations of this interface should provide a default no-argument constructor,
* and should fill in all dependencies via the setParameters method.
* @author Assaf Urieli
*
*/
public interface ParseConfigurationProcessor {
	/**
	 * Called when the next parse configuration is available for processing, outputting to the writer provided.
	 */
	public void onNextParseConfiguration(ParseConfiguration parseConfiguration, Writer writer);

	/**
	 * Called when parsing is complete.
	 */
	public void onCompleteParse();
}
