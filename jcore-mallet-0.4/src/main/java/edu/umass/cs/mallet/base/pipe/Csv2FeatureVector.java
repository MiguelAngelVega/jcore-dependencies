/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package edu.umass.cs.mallet.base.pipe;

import edu.umass.cs.mallet.base.types.Alphabet;
import edu.umass.cs.mallet.base.types.FeatureVector;
import edu.umass.cs.mallet.base.types.Instance;
import edu.umass.cs.mallet.base.types.Labeling;
import edu.umass.cs.mallet.base.util.MalletLogger;

import java.util.logging.Logger;


/**
 * Converts a string of the form
 * <tt>feature_1:val_1 feature_2:val_2 ... feature_k:val_k</tt>
 * into a (sparse) FeatureVector.
 *
 * @author Gary Huang
 */
public class Csv2FeatureVector extends Pipe 
{

    private static Logger logger = MalletLogger.getLogger(Csv2FeatureVector.class.getName());

    public Csv2FeatureVector(int capacity) 
    {
        this.dataDict = new Alphabet(capacity);
    }
    
    public Csv2FeatureVector() 
    {
        this(1000);
    }
    
    /**
     * Convert the data in the given <tt>Instance</tt> from a <tt>CharSequence</tt> 
     * of sparse feature-value pairs to a <tt>FeatureVector</tt>
     *
     * @throws IllegalStateException If <CODE>Instance.getTarget()</CODE> is
     * not a Labeling
     */
    public Instance pipe(Instance carrier) 
        throws IllegalStateException
    {
        CharSequence c = (CharSequence) carrier.getData();
        String[] pairs = c.toString().trim().split("\\s+");
        String[] keys = new String[pairs.length];
        double[] values = new double[pairs.length];

        for (int i = 0; i < pairs.length; i++) {
	  int delimIndex = pairs[i].lastIndexOf(":");
	  if (delimIndex <= 0 || delimIndex == (pairs[i].length()-1))
	      throw new IllegalStateException("token is not a valid feature name-feature value pair: "
				        + pairs[i] + "\nfaulting instance name:" + carrier.getName());
	  
	  keys[i] = pairs[i].substring(0, delimIndex);
	  values[i] = Double.parseDouble(pairs[i].substring(delimIndex+1));

	  dataDict.lookupIndex(keys[i], true); // add the feature name
        }
        // Sort indices beforehand to prevent the bubble sort used in
        // constructor of SparseVector from taking too much time
        int[] keyIndices = FeatureVector.getObjectIndices(keys, dataDict, true);
        java.util.Arrays.sort(keyIndices);
        FeatureVector fv = new FeatureVector(dataDict, keyIndices, values);
        // Check if we've set the target alphabet member
        if (targetDict == null) {
	  if (carrier.getTarget() instanceof Labeling)
	      targetDict = ((Labeling)carrier.getTarget()).getLabelAlphabet();
	  else
	      throw new IllegalStateException ("Instance target is not a " +
				         "Labeling; it is a " + 
				         carrier.getTarget().getClass().getName());
	  	  
        }
        
        carrier.setData( fv );
        return carrier;
    }
    
}
