///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2016 Joliciel Informatique
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

import java.io.Serializable;

/**
 * A marker for token annotations.
 * 
 * @author Assaf Urieli
 *
 */
public class TokenBoundary implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String analysisText;

	public TokenBoundary(String analysisText) {
		this.analysisText = analysisText;
	}

	/**
	 * The token's text for analysis purposes (the original text may have been
	 * replaced by something else for analysis purposes).
	 * 
	 * @return
	 */
	public String getAnalysisText() {
		return analysisText;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
