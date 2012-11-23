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
package com.joliciel.talismane.machineLearning.features;

/**
 * Returns operand1 * operand2.
 * @author Assaf Urieli
 *
 * @param <T>
 */
public class MultiplyIntegerOperator<T> extends AbstractCachableFeature<T,Integer> implements
		IntegerFeature<T> {
	private IntegerFeature<T> operand1;
	private IntegerFeature<T> operand2;
	
	public MultiplyIntegerOperator(IntegerFeature<T> operand1, IntegerFeature<T> operand2) {
		super();
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.setName("(" + operand1.getName() + "*" + operand2.getName() + ")");
	}

	@Override
	protected FeatureResult<Integer> checkInternal(T context) {
		FeatureResult<Integer> featureResult = null;
		
		FeatureResult<Integer> operand1Result = operand1.check(context);
		FeatureResult<Integer> operand2Result = operand2.check(context);
		
		if (operand1Result!=null && operand2Result!=null) {
			int result = operand1Result.getOutcome() * operand2Result.getOutcome();
			featureResult = this.generateResult(result);
		}
		
		return featureResult;
		
	}

	public IntegerFeature<T> getOperand1() {
		return operand1;
	}

	public IntegerFeature<T> getOperand2() {
		return operand2;
	}


}