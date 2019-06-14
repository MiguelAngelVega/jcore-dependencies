/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */




/** 
		Count the number of times the provided regular expression matches
		the token text, and add a feature with the provided name having
		value equal to the count.

		@author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

package edu.umass.cs.mallet.base.pipe.tsf;

import edu.umass.cs.mallet.base.pipe.Pipe;
import edu.umass.cs.mallet.base.types.Instance;
import edu.umass.cs.mallet.base.types.Token;
import edu.umass.cs.mallet.base.types.TokenSequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CountMatches extends Pipe
{
	public static final int INTEGER_COUNT = 0;
	public static final int BINARY_COUNT = 1;
	public static final int NORMALIZED_COUNT = 2;
	public static final int OVER_MAX = 3;
	
	Pattern regex;
	String feature;
	boolean normalizeByCharLength = false;
	boolean countIsBinary = false;
	
	public CountMatches (String featureName, Pattern regex, int countType)
	{
		this.feature = featureName;
		this.regex = regex;
		if (countType == BINARY_COUNT)
			countIsBinary = true;
		else if (countType == NORMALIZED_COUNT)
			normalizeByCharLength = true;
		else if (countType >= OVER_MAX)
			throw new IllegalArgumentException ("Bad countType.");
	}

	public CountMatches (String featureName, Pattern regex)
	{
		this (featureName, regex, INTEGER_COUNT);
	}
	
	public Instance pipe (Instance carrier)
	{
		TokenSequence ts = (TokenSequence) carrier.getData();
		int count;
		for (int i = 0; i < ts.size(); i++) {
			count = 0;
			Token t = ts.getToken(i);
			Matcher matcher = regex.matcher (t.getText());
			while (matcher.find ()) {
				count++;
				if (countIsBinary) break;
			}
			if (count > 0)
				t.setFeatureValue (feature, (normalizeByCharLength
																		 ? ((double)count)/t.getText().length()
																		 : (double)count));
		}
		return carrier;
	}


}
