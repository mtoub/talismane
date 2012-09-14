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
package com.joliciel.talismane.tokeniser.features;


import com.joliciel.talismane.tokeniser.Token;
import com.joliciel.talismane.tokeniser.TokenWrapper;
import com.joliciel.talismane.utils.features.FeatureResult;
import com.joliciel.talismane.utils.features.StringFeature;

/**
 * Returns the first word in a compound token. If not a compound token, returns null.
 * @author Assaf Urieli
 *
 */
public class FirstWordInCompoundFeature extends AbstractTokenFeature<String> implements StringFeature<TokenWrapper> {
	public FirstWordInCompoundFeature() {
	}
	
	@Override
	public FeatureResult<String> checkInternal(TokenWrapper tokenWrapper) {
		Token token = tokenWrapper.getToken();
		FeatureResult<String> result = null;
		String string = token.getText().trim();
		
		if (string.indexOf(' ')>=0) {
			String firstWord = string.substring(0, string.indexOf(' '));
			result = this.generateResult(firstWord);
		}
		return result;
	}
}
