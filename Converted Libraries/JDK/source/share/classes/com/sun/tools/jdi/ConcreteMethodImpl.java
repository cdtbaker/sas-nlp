package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.ref.SoftReference;
/** 
 * Represents methods with method bodies.
 * That is, non-native non-abstract methods.
 * Private to MethodImpl.
 */
public class ConcreteMethodImpl extends MethodImpl {
static private class SoftLocationXRefs {
    final String stratumID;
    final Map<Integer,List<Location>> lineMapper;
    final List<Location> lineLocations;
    final int lowestLine;
    final int highestLine;
    SoftLocationXRefs(    String stratumID,    Map<Integer,List<Location>> lineMapper,    List<Location> lineLocations,    int lowestLine,    int highestLine){
      this.stratumID=stratumID;
      this.lineMapper=Collections.unmodifiableMap(lineMapper);
      this.lineLocations=Collections.unmodifiableList(lineLocations);
      this.lowestLine=lowestLine;
      this.highestLine=highestLine;
    }
  }
  private Location location=null;
  private SoftReference<SoftLocationXRefs> softBaseLocationXRefsRef;
  private SoftReference<SoftLocationXRefs> softOtherLocationXRefsRef;
  private SoftReference<List<LocalVariable>> variablesRef=null;
  private boolean absentVariableInformation=false;
  private long firstIndex=-1;
  private long lastIndex=-1;
  private SoftReference<byte[]> bytecodesRef=null;
  private int argSlotCount=-1;
  ConcreteMethodImpl(  VirtualMachine vm,  ReferenceTypeImpl declaringType,  long ref,  String name,  String signature,  String genericSignature,  int modifiers){
    super(vm,declaringType,ref,name,signature,genericSignature,modifiers);
  }
  public Location location(){
    if (location == null) {
      getBaseLocations();
    }
    return location;
  }
  List<Location> sourceNameFilter(  List<Location> list,  SDE.Stratum stratum,  String sourceName) throws AbsentInformationException {
    if (sourceName == null) {
      return list;
    }
 else {
      List<Location> locs=new ArrayList<Location>();
      for (      Location loc : list) {
        if (((LocationImpl)loc).sourceName(stratum).equals(sourceName)) {
          locs.add(loc);
        }
      }
      return locs;
    }
  }
  List<Location> allLineLocations(  SDE.Stratum stratum,  String sourceName) throws AbsentInformationException {
    List<Location> lineLocations=getLocations(stratum).lineLocations;
    if (lineLocations.size() == 0) {
      throw new AbsentInformationException();
    }
    return Collections.unmodifiableList(sourceNameFilter(lineLocations,stratum,sourceName));
  }
  List<Location> locationsOfLine(  SDE.Stratum stratum,  String sourceName,  int lineNumber) throws AbsentInformationException {
    SoftLocationXRefs info=getLocations(stratum);
    if (info.lineLocations.size() == 0) {
      throw new AbsentInformationException();
    }
    List<Location> list=info.lineMapper.get(new Integer(lineNumber));
    if (list == null) {
      list=new ArrayList<Location>(0);
    }
    return Collections.unmodifiableList(sourceNameFilter(list,stratum,sourceName));
  }
  public Location locationOfCodeIndex(  long codeIndex){
    if (firstIndex == -1) {
      getBaseLocations();
    }
    if (codeIndex < firstIndex || codeIndex > lastIndex) {
      return null;
    }
    return new LocationImpl(virtualMachine(),this,codeIndex);
  }
  LineInfo codeIndexToLineInfo(  SDE.Stratum stratum,  long codeIndex){
    if (firstIndex == -1) {
      getBaseLocations();
    }
    if (codeIndex < firstIndex || codeIndex > lastIndex) {
      throw new InternalError("Location with invalid code index");
    }
    List<Location> lineLocations=getLocations(stratum).lineLocations;
    if (lineLocations.size() == 0) {
      return super.codeIndexToLineInfo(stratum,codeIndex);
    }
    Iterator iter=lineLocations.iterator();
    LocationImpl bestMatch=(LocationImpl)iter.next();
    while (iter.hasNext()) {
      LocationImpl current=(LocationImpl)iter.next();
      if (current.codeIndex() > codeIndex) {
        break;
      }
      bestMatch=current;
    }
    return bestMatch.getLineInfo(stratum);
  }
  public List<LocalVariable> variables() throws AbsentInformationException {
    return getVariables();
  }
  public List<LocalVariable> variablesByName(  String name) throws AbsentInformationException {
    List<LocalVariable> variables=getVariables();
    List<LocalVariable> retList=new ArrayList<LocalVariable>(2);
    Iterator iter=variables.iterator();
    while (iter.hasNext()) {
      LocalVariable variable=(LocalVariable)iter.next();
      if (variable.name().equals(name)) {
        retList.add(variable);
      }
    }
    return retList;
  }
  public List<LocalVariable> arguments() throws AbsentInformationException {
    List<LocalVariable> variables=getVariables();
    List<LocalVariable> retList=new ArrayList<LocalVariable>(variables.size());
    Iterator iter=variables.iterator();
    while (iter.hasNext()) {
      LocalVariable variable=(LocalVariable)iter.next();
      if (variable.isArgument()) {
        retList.add(variable);
      }
    }
    return retList;
  }
  public byte[] bytecodes(){
    byte[] bytecodes=(bytecodesRef == null) ? null : bytecodesRef.get();
    if (bytecodes == null) {
      try {
        bytecodes=JDWP.Method.Bytecodes.process(vm,declaringType,ref).bytes;
      }
 catch (      JDWPException exc) {
        throw exc.toJDIException();
      }
      bytecodesRef=new SoftReference<byte[]>(bytecodes);
    }
    return bytecodes.clone();
  }
  int argSlotCount() throws AbsentInformationException {
    if (argSlotCount == -1) {
      getVariables();
    }
    return argSlotCount;
  }
  private SoftLocationXRefs getLocations(  SDE.Stratum stratum){
    if (stratum.isJava()) {
      return getBaseLocations();
    }
    String stratumID=stratum.id();
    SoftLocationXRefs info=(softOtherLocationXRefsRef == null) ? null : softOtherLocationXRefsRef.get();
    if (info != null && info.stratumID.equals(stratumID)) {
      return info;
    }
    List<Location> lineLocations=new ArrayList<Location>();
    Map<Integer,List<Location>> lineMapper=new HashMap<Integer,List<Location>>();
    int lowestLine=-1;
    int highestLine=-1;
    SDE.LineStratum lastLineStratum=null;
    SDE.Stratum baseStratum=declaringType.stratum(SDE.BASE_STRATUM_NAME);
    Iterator it=getBaseLocations().lineLocations.iterator();
    while (it.hasNext()) {
      LocationImpl loc=(LocationImpl)it.next();
      int baseLineNumber=loc.lineNumber(baseStratum);
      SDE.LineStratum lineStratum=stratum.lineStratum(declaringType,baseLineNumber);
      if (lineStratum == null) {
        continue;
      }
      int lineNumber=lineStratum.lineNumber();
      if ((lineNumber != -1) && (!lineStratum.equals(lastLineStratum))) {
        lastLineStratum=lineStratum;
        if (lineNumber > highestLine) {
          highestLine=lineNumber;
        }
        if ((lineNumber < lowestLine) || (lowestLine == -1)) {
          lowestLine=lineNumber;
        }
        loc.addStratumLineInfo(new StratumLineInfo(stratumID,lineNumber,lineStratum.sourceName(),lineStratum.sourcePath()));
        lineLocations.add(loc);
        Integer key=new Integer(lineNumber);
        List<Location> mappedLocs=lineMapper.get(key);
        if (mappedLocs == null) {
          mappedLocs=new ArrayList<Location>(1);
          lineMapper.put(key,mappedLocs);
        }
        mappedLocs.add(loc);
      }
    }
    info=new SoftLocationXRefs(stratumID,lineMapper,lineLocations,lowestLine,highestLine);
    softOtherLocationXRefsRef=new SoftReference<SoftLocationXRefs>(info);
    return info;
  }
  private SoftLocationXRefs getBaseLocations(){
    SoftLocationXRefs info=(softBaseLocationXRefsRef == null) ? null : softBaseLocationXRefsRef.get();
    if (info != null) {
      return info;
    }
    JDWP.Method.LineTable lntab=null;
    try {
      lntab=JDWP.Method.LineTable.process(vm,declaringType,ref);
    }
 catch (    JDWPException exc) {
      throw exc.toJDIException();
    }
    int count=lntab.lines.length;
    List<Location> lineLocations=new ArrayList<Location>(count);
    Map<Integer,List<Location>> lineMapper=new HashMap<Integer,List<Location>>();
    int lowestLine=-1;
    int highestLine=-1;
    for (int i=0; i < count; i++) {
      long bci=lntab.lines[i].lineCodeIndex;
      int lineNumber=lntab.lines[i].lineNumber;
      if ((i + 1 == count) || (bci != lntab.lines[i + 1].lineCodeIndex)) {
        if (lineNumber > highestLine) {
          highestLine=lineNumber;
        }
        if ((lineNumber < lowestLine) || (lowestLine == -1)) {
          lowestLine=lineNumber;
        }
        LocationImpl loc=new LocationImpl(virtualMachine(),this,bci);
        loc.addBaseLineInfo(new BaseLineInfo(lineNumber,declaringType));
        lineLocations.add(loc);
        Integer key=new Integer(lineNumber);
        List<Location> mappedLocs=lineMapper.get(key);
        if (mappedLocs == null) {
          mappedLocs=new ArrayList<Location>(1);
          lineMapper.put(key,mappedLocs);
        }
        mappedLocs.add(loc);
      }
    }
    if (location == null) {
      firstIndex=lntab.start;
      lastIndex=lntab.end;
      if (count > 0) {
        location=lineLocations.get(0);
      }
 else {
        location=new LocationImpl(virtualMachine(),this,firstIndex);
      }
    }
    info=new SoftLocationXRefs(SDE.BASE_STRATUM_NAME,lineMapper,lineLocations,lowestLine,highestLine);
    softBaseLocationXRefsRef=new SoftReference<SoftLocationXRefs>(info);
    return info;
  }
  private List<LocalVariable> getVariables1_4() throws AbsentInformationException {
    JDWP.Method.VariableTable vartab=null;
    try {
      vartab=JDWP.Method.VariableTable.process(vm,declaringType,ref);
    }
 catch (    JDWPException exc) {
      if (exc.errorCode() == JDWP.Error.ABSENT_INFORMATION) {
        absentVariableInformation=true;
        throw new AbsentInformationException();
      }
 else {
        throw exc.toJDIException();
      }
    }
    argSlotCount=vartab.argCnt;
    int count=vartab.slots.length;
    List<LocalVariable> variables=new ArrayList<LocalVariable>(count);
    for (int i=0; i < count; i++) {
      JDWP.Method.VariableTable.SlotInfo si=vartab.slots[i];
      if (!si.name.startsWith("this$") && !si.name.equals("this")) {
        Location scopeStart=new LocationImpl(virtualMachine(),this,si.codeIndex);
        Location scopeEnd=new LocationImpl(virtualMachine(),this,si.codeIndex + si.length - 1);
        LocalVariable variable=new LocalVariableImpl(virtualMachine(),this,si.slot,scopeStart,scopeEnd,si.name,si.signature,null);
        variables.add(variable);
      }
    }
    return variables;
  }
  private List<LocalVariable> getVariables1() throws AbsentInformationException {
    if (!vm.canGet1_5LanguageFeatures()) {
      return getVariables1_4();
    }
    JDWP.Method.VariableTableWithGeneric vartab=null;
    try {
      vartab=JDWP.Method.VariableTableWithGeneric.process(vm,declaringType,ref);
    }
 catch (    JDWPException exc) {
      if (exc.errorCode() == JDWP.Error.ABSENT_INFORMATION) {
        absentVariableInformation=true;
        throw new AbsentInformationException();
      }
 else {
        throw exc.toJDIException();
      }
    }
    argSlotCount=vartab.argCnt;
    int count=vartab.slots.length;
    List<LocalVariable> variables=new ArrayList<LocalVariable>(count);
    for (int i=0; i < count; i++) {
      JDWP.Method.VariableTableWithGeneric.SlotInfo si=vartab.slots[i];
      if (!si.name.startsWith("this$") && !si.name.equals("this")) {
        Location scopeStart=new LocationImpl(virtualMachine(),this,si.codeIndex);
        Location scopeEnd=new LocationImpl(virtualMachine(),this,si.codeIndex + si.length - 1);
        LocalVariable variable=new LocalVariableImpl(virtualMachine(),this,si.slot,scopeStart,scopeEnd,si.name,si.signature,si.genericSignature);
        variables.add(variable);
      }
    }
    return variables;
  }
  private List<LocalVariable> getVariables() throws AbsentInformationException {
    if (absentVariableInformation) {
      throw new AbsentInformationException();
    }
    List<LocalVariable> variables=(variablesRef == null) ? null : variablesRef.get();
    if (variables != null) {
      return variables;
    }
    variables=getVariables1();
    variables=Collections.unmodifiableList(variables);
    variablesRef=new SoftReference<List<LocalVariable>>(variables);
    return variables;
  }
}
