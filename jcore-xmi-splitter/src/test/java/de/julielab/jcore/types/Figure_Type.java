
/* First created by JCasGen Wed Aug 08 13:36:49 CEST 2018 */
package de.julielab.jcore.types;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/** Annotation for all elements in the CAS that belong to figures, e.g. figure title, figure caption etc.
 * Updated by JCasGen Wed Aug 08 13:36:49 CEST 2018
 * @generated */
public class Figure_Type extends TextObject_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Figure.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.julielab.jcore.types.Figure");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Figure_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    