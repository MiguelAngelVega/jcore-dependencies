

/* First created by JCasGen Wed Aug 08 13:36:49 CEST 2018 */
package de.julielab.jcore.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** AuthorInfo Type annotates the text segments containing the information about an author and his/her affiliation information.
 * Updated by JCasGen Wed Aug 08 13:36:49 CEST 2018
 * XML source: /Volumes/OUTERSPACE/Coding/git/jcore-dependencies/jcore-xmi-splitter/src/test/resources/test-types/all-test-types.xml
 * @generated */
public class AuthorInfo extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AuthorInfo.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AuthorInfo() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AuthorInfo(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AuthorInfo(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public AuthorInfo(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: foreName

  /** getter for foreName - gets An authorâs forename,C
   * @generated
   * @return value of the feature 
   */
  public String getForeName() {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_foreName == null)
      jcasType.jcas.throwFeatMissing("foreName", "de.julielab.jcore.types.AuthorInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_foreName);}
    
  /** setter for foreName - sets An authorâs forename,C 
   * @generated
   * @param v value to set into the feature 
   */
  public void setForeName(String v) {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_foreName == null)
      jcasType.jcas.throwFeatMissing("foreName", "de.julielab.jcore.types.AuthorInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_foreName, v);}    
   
    
  //*--------------*
  //* Feature: affiliation

  /** getter for affiliation - gets Affiliation of the author.
   * @generated
   * @return value of the feature 
   */
  public String getAffiliation() {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_affiliation == null)
      jcasType.jcas.throwFeatMissing("affiliation", "de.julielab.jcore.types.AuthorInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_affiliation);}
    
  /** setter for affiliation - sets Affiliation of the author. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAffiliation(String v) {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_affiliation == null)
      jcasType.jcas.throwFeatMissing("affiliation", "de.julielab.jcore.types.AuthorInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_affiliation, v);}    
   
    
  //*--------------*
  //* Feature: contact

  /** getter for contact - gets Contact information (emails, phones, etc.).
   * @generated
   * @return value of the feature 
   */
  public String getContact() {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_contact == null)
      jcasType.jcas.throwFeatMissing("contact", "de.julielab.jcore.types.AuthorInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_contact);}
    
  /** setter for contact - sets Contact information (emails, phones, etc.). 
   * @generated
   * @param v value to set into the feature 
   */
  public void setContact(String v) {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_contact == null)
      jcasType.jcas.throwFeatMissing("contact", "de.julielab.jcore.types.AuthorInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_contact, v);}    
   
    
  //*--------------*
  //* Feature: lastName

  /** getter for lastName - gets The last name of the author.
   * @generated
   * @return value of the feature 
   */
  public String getLastName() {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_lastName == null)
      jcasType.jcas.throwFeatMissing("lastName", "de.julielab.jcore.types.AuthorInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_lastName);}
    
  /** setter for lastName - sets The last name of the author. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLastName(String v) {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_lastName == null)
      jcasType.jcas.throwFeatMissing("lastName", "de.julielab.jcore.types.AuthorInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_lastName, v);}    
   
    
  //*--------------*
  //* Feature: initials

  /** getter for initials - gets Initials
   * @generated
   * @return value of the feature 
   */
  public String getInitials() {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_initials == null)
      jcasType.jcas.throwFeatMissing("initials", "de.julielab.jcore.types.AuthorInfo");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_initials);}
    
  /** setter for initials - sets Initials 
   * @generated
   * @param v value to set into the feature 
   */
  public void setInitials(String v) {
    if (AuthorInfo_Type.featOkTst && ((AuthorInfo_Type)jcasType).casFeat_initials == null)
      jcasType.jcas.throwFeatMissing("initials", "de.julielab.jcore.types.AuthorInfo");
    jcasType.ll_cas.ll_setStringValue(addr, ((AuthorInfo_Type)jcasType).casFeatCode_initials, v);}    
  }

    