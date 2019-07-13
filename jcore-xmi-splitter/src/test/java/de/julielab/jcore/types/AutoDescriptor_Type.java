
/* First created by JCasGen Sat Jul 13 15:41:00 CEST 2019 */
package de.julielab.jcore.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The descriptor type for automatically (i.e. algorithmically) acquired meta information. It can be refined and extended.
 * Updated by JCasGen Sat Jul 13 15:41:00 CEST 2019
 * @generated */
public class AutoDescriptor_Type extends Descriptor_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AutoDescriptor.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.julielab.jcore.types.AutoDescriptor");
 
  /** @generated */
  final Feature casFeat_documentClasses;
  /** @generated */
  final int     casFeatCode_documentClasses;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentClasses(int addr) {
        if (featOkTst && casFeat_documentClasses == null)
      jcas.throwFeatMissing("documentClasses", "de.julielab.jcore.types.AutoDescriptor");
    return ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentClasses(int addr, int v) {
        if (featOkTst && casFeat_documentClasses == null)
      jcas.throwFeatMissing("documentClasses", "de.julielab.jcore.types.AutoDescriptor");
    ll_cas.ll_setRefValue(addr, casFeatCode_documentClasses, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getDocumentClasses(int addr, int i) {
        if (featOkTst && casFeat_documentClasses == null)
      jcas.throwFeatMissing("documentClasses", "de.julielab.jcore.types.AutoDescriptor");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setDocumentClasses(int addr, int i, int v) {
        if (featOkTst && casFeat_documentClasses == null)
      jcas.throwFeatMissing("documentClasses", "de.julielab.jcore.types.AutoDescriptor");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentClasses), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_documentTopics;
  /** @generated */
  final int     casFeatCode_documentTopics;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDocumentTopics(int addr) {
        if (featOkTst && casFeat_documentTopics == null)
      jcas.throwFeatMissing("documentTopics", "de.julielab.jcore.types.AutoDescriptor");
    return ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentTopics(int addr, int v) {
        if (featOkTst && casFeat_documentTopics == null)
      jcas.throwFeatMissing("documentTopics", "de.julielab.jcore.types.AutoDescriptor");
    ll_cas.ll_setRefValue(addr, casFeatCode_documentTopics, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getDocumentTopics(int addr, int i) {
        if (featOkTst && casFeat_documentTopics == null)
      jcas.throwFeatMissing("documentTopics", "de.julielab.jcore.types.AutoDescriptor");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setDocumentTopics(int addr, int i, int v) {
        if (featOkTst && casFeat_documentTopics == null)
      jcas.throwFeatMissing("documentTopics", "de.julielab.jcore.types.AutoDescriptor");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_documentTopics), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AutoDescriptor_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_documentClasses = jcas.getRequiredFeatureDE(casType, "documentClasses", "uima.cas.FSArray", featOkTst);
    casFeatCode_documentClasses  = (null == casFeat_documentClasses) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentClasses).getCode();

 
    casFeat_documentTopics = jcas.getRequiredFeatureDE(casType, "documentTopics", "uima.cas.FSArray", featOkTst);
    casFeatCode_documentTopics  = (null == casFeat_documentTopics) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentTopics).getCode();

  }
}



    