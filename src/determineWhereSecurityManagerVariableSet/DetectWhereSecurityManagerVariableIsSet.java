package determineWhereSecurityManagerVariableSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.structurals.ControlFlowGraph;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;
import edu.umd.cs.findbugs.ba.LocationAndEdgeType;
import edu.umd.cs.findbugs.ba.SignatureParser;
import edu.umd.cs.findbugs.ba.XFactory;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.ba.heap.StoreDataflow;
import edu.umd.cs.findbugs.ba.npe.IsNullValue;
import edu.umd.cs.findbugs.ba.npe.IsNullValueDataflow;
import edu.umd.cs.findbugs.ba.npe.IsNullValueFrame;
import edu.umd.cs.findbugs.ba.obl.ObligationAcquiredOrReleasedInLoopException;
import edu.umd.cs.findbugs.ba.obl.ObligationDataflow;
import edu.umd.cs.findbugs.ba.type.NullType;
import edu.umd.cs.findbugs.ba.type.TypeDataflow;
import edu.umd.cs.findbugs.ba.type.TypeFrame;
import edu.umd.cs.findbugs.ba.vna.ValueNumber;
import edu.umd.cs.findbugs.ba.vna.ValueNumberDataflow;
import edu.umd.cs.findbugs.ba.vna.ValueNumberFrame;
import edu.umd.cs.findbugs.bcel.BCELUtil;
import edu.umd.cs.findbugs.bcel.CFGDetector;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class DetectWhereSecurityManagerVariableIsSet extends CFGDetector {

  BugReporter bugReporter;
  static BitSet testSet = new BitSet();
  static {
    testSet.set(Constants.INVOKESTATIC);
  }

  public DetectWhereSecurityManagerVariableIsSet(BugReporter bugReporter) {
    this.bugReporter = bugReporter;
    // IAnalysisCache analysisCache = Global.getAnalysisCache();
  }

  @Override
  public void visitClass(ClassDescriptor classDescriptor)
      throws CheckedAnalysisException {
    IAnalysisCache analysisCache = Global.getAnalysisCache();

    JavaClass jclass = analysisCache.getClassAnalysis(JavaClass.class,
	classDescriptor);
    classContext = analysisCache.getClassAnalysis(ClassContext.class,
	classDescriptor);
    System.out.println("Current Class:" + classDescriptor.getClassName());
    for (Method m : classContext.getMethodsInCallOrder()) {

      method = m;
      MethodDescriptor methodDescriptor = BCELUtil.getMethodDescriptor(jclass,
	  method);

      // Try to get MethodGen. If we can't get one,
      // then this method should be skipped.
      MethodGen methodGen = analysisCache.getMethodAnalysis(MethodGen.class,
	  methodDescriptor);

      if (!prescreen(m, methodGen, classContext)) {
	continue;
      }
      InstructionList il = methodGen.getInstructionList();
      Iterator<InstructionHandle> insHandleIter = (Iterator<InstructionHandle>) il
	  .iterator();
      CFG cfg = analysisCache.getMethodAnalysis(CFG.class, methodDescriptor);
      while (insHandleIter.hasNext()) {
	InstructionHandle insHandle = insHandleIter.next();
	Instruction ins = insHandle.getInstruction();
	if (ins instanceof InvokeInstruction) {
	  InvokeInstruction invoke = (InvokeInstruction) ins;
	  if (invoke.getMethodName(methodGen.getConstantPool()).equals(
	      "setSecurityManager")) {
	    ValueNumberDataflow vnaDataflow = classContext
		.getValueNumberDataflow(method);
	    IsNullValueDataflow isNullDataflow = classContext
		.getIsNullValueDataflow(method);
	    TypeDataflow typeDataflow = classContext.getTypeDataflow(method);
	    Iterator<Location> locIter = cfg
		.getLocationsContainingInstructionWithOffset(
		    insHandle.getPosition()).iterator();
	    while (locIter.hasNext()) {
	      Location currentLoc = locIter.next();
	      IsNullValueFrame nullFrame = isNullDataflow
		  .getFactAtLocation(currentLoc);
	      if (!nullFrame.isValid()) {
		System.err.println("nullFrame is not valid");
		System.err.println("currentLoc: "
		    + currentLoc.toCompactString());
	      }
	      IsNullValue operandNullness = nullFrame.getTopValue();
	      TypeFrame frame = typeDataflow.getFactAtLocation(currentLoc);
	      Type operandType = frame.getTopValue();
	      ValueNumberFrame vnf = vnaDataflow.getAnalysis()
		  .getFactAtLocation(currentLoc);
	      System.out
		  .print("Original value number frame: " + vnf.toString());
	      System.out.println("Original instuction: "
		  + currentLoc.getHandle().toString());
	      SignatureParser sigParser = new SignatureParser(
		  invoke.getSignature(classContext.getConstantPoolGen()));
	      ValueNumber vn = vnf.getOperand(invoke,
		  classContext.getConstantPoolGen(), 0);
	      String bugString;
	      if (operandType.equals(NullType.instance())
		  || operandNullness.isDefinitelyNull()) {
		bugString = "Set to null: " + frame.getStackValue(0);
	      } else if (operandNullness.isDefinitelyNotNull()) {
		bugString = "Not null: " + frame.getStackValue(0);
	      } else {
		bugString = "Could be null or other value: "
		    + frame.getStackValue(0);
	      }
	      BugInstance bugInstance = new BugInstance(this,
		  "DETECT_SECURITY_MANAGER_SET_LOCATION_BUG", HIGH_PRIORITY)
		  .addClassAndMethod(methodDescriptor).addString(bugString)
		  .addSourceLine(methodDescriptor, currentLoc);
	      // Location lastSeenLocation = currentLoc;

	      // boolean haventFoundFirstSet = true;
	      // boolean notAtBeginningOfControlFlow =
	      // !lastSeenLocation.equals(checkingLocation);
	      // //getPreviousLocation returns the same location when the
	      // location is the first location
	      ArrayList<Location> locationsVariableLastSeenList = new ArrayList<Location>();
	      dfsForVariable(cfg, currentLoc, locationsVariableLastSeenList,
		  vnaDataflow, vn, false, false,
		  vn.hasFlag(ValueNumber.PHI_NODE));
	      System.out.println("number of locations found at the end: "
		  + locationsVariableLastSeenList.size());
	      for (Location lastSeenLocation : locationsVariableLastSeenList) {
		bugInstance.addString(lastSeenLocation.getHandle()
		    .getInstruction().getName());
		bugInstance.addString(lastSeenLocation.getHandle()
		    .getInstruction().toString());
		bugInstance.addString("\nVariable is set at: ").addSourceLine(
		    methodDescriptor, lastSeenLocation);
	      }
	      /*
	       * while(haventFoundFirstSet && notAtBeginningOfControlFlow) {
	       * 
	       * ValueNumberFrame cfgVnf = vnaDataflow.getAnalysis()
	       * .getFactAtLocation(checkingLocation);
	       * 
	       * if (!cfgVnf.contains(vn)) { //contains already checks isValid
	       * so don't need to do it again haventFoundFirstSet = false; }
	       * lastSeenLocation = checkingLocation; checkingLocation =
	       * cfg.getPreviousLocation(lastSeenLocation);
	       * notAtBeginningOfControlFlow =
	       * !lastSeenLocation.equals(checkingLocation);
	       * //getPreviousLocation returns the same location when the
	       * location is the first location }
	       * System.out.println("Class Name: "+jclass.getClassName());
	       * bugInstance.addString(lastSeenLocation.getHandle()
	       * .getInstruction().getName());
	       * bugInstance.addString(lastSeenLocation.getHandle()
	       * .getInstruction().toString());
	       * 
	       * bugInstance.addString("Variable is set at: ").addSourceLine(
	       * methodDescriptor, lastSeenLocation);
	       * //bugInstance.addString("Before (position-wise): "
	       * +String.valueOf(lastSeenLocation.compareTo(currentLoc)));
	       * //bugInstance
	       * .addString("Method: "+cfg.getMethodName()+" "+cfg.getMethodSig
	       * ()); //
	       * bugInstance.addString("vertice count: "+cfg.getNumVertices());
	       * 
	       * //boolean containsEventualSetValue = false; boolean
	       * foundEventualSetValue = false;
	       * while(notAtBeginningOfControlFlow) { ValueNumberFrame cfgVnf =
	       * vnaDataflow.getAnalysis() .getFactAtLocation(checkingLocation);
	       * System
	       * .out.println("Checking instruction: "+checkingLocation.getHandle
	       * ().getInstruction().getName()); if (!foundEventualSetValue &&
	       * cfgVnf.contains(vn)) { //contains already checks isValid so
	       * don't need to do it again foundEventualSetValue = true; } else
	       * if (foundEventualSetValue && !cfgVnf.contains(vn)){
	       * bugInstance.addString(lastSeenLocation.getHandle()
	       * .getInstruction().getName());
	       * bugInstance.addString(lastSeenLocation.getHandle()
	       * .getInstruction().toString());
	       * bugInstance.addString("\nVariable is set at: ").addSourceLine(
	       * methodDescriptor, lastSeenLocation); }
	       * System.out.println("before:"
	       * +String.valueOf(lastSeenLocation.equals(checkingLocation)));
	       * lastSeenLocation = checkingLocation; checkingLocation =
	       * cfg.getPreviousLocation(lastSeenLocation);
	       * notAtBeginningOfControlFlow =
	       * !lastSeenLocation.equals(checkingLocation);
	       * //getPreviousLocation returns the same location when the
	       * location is the first location
	       * System.out.println("after:"+String
	       * .valueOf(lastSeenLocation.equals(checkingLocation))); }
	       */
	      bugReporter.reportBug(bugInstance);
	    }
	  }
	}

	visitMethodCFG(methodDescriptor, cfg);

      }
    }
  }

  private void dfsForVariable(CFG cfg, Location currentLoc,
      ArrayList<Location> locList, ValueNumberDataflow vnaDataflow,
      ValueNumber vn, boolean lost, boolean lostThenFound,
      boolean currentlyNewValueNumberInDifferentBlock) {
    Collection<LocationAndEdgeType> checkingLocations = cfg
	.getPreviousLocations(currentLoc);
    // **************
    Iterator<LocationAndEdgeType> sizeCheckingLocIter = checkingLocations
	.iterator();
    int count = 0;
    while (sizeCheckingLocIter.hasNext()) {
      sizeCheckingLocIter.next();
      count++;
    }
    if (count > 1) {
      System.out.println("Number of locations found: " + String.valueOf(count));
    }
    // *************
    Iterator<LocationAndEdgeType> checkingLocIter = checkingLocations
	.iterator();
    int branchCount = 0;
    while (checkingLocIter.hasNext()) {
      if (branchCount > 0) {
	System.out.println("new branch");
      }
      branchCount++;
      LocationAndEdgeType tempCheckingLoc = checkingLocIter.next();
      ValueNumberFrame cfgVnf = vnaDataflow.getAnalysis().getFactAtLocation(
	  tempCheckingLoc.getLocation());
      ValueNumberFrame afterCfgVnf = vnaDataflow.getAnalysis().getFactAfterLocation(
	  tempCheckingLoc.getLocation());
      System.out.print("Value number frame: " + cfgVnf.toString() + "   ");
      System.out.print("Instruction : "
	  + tempCheckingLoc.getLocation().getHandle().toString() + "    ");
      System.out.print("Value number fram after: "+afterCfgVnf.toString()+"||");
      if (tempCheckingLoc.getEdgeType() == null
	  || tempCheckingLoc.getEdgeType().intValue() == 0) {
	if (cfgVnf.contains(vn)) {
	  // previous statement with no branching and contains the value
	  boolean newLostThenFound = lostThenFound;
	  if (lost) {
	    newLostThenFound = true;
	  }
	  dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
	      vnaDataflow, vn, lost, newLostThenFound,
	      currentlyNewValueNumberInDifferentBlock);
	} else {
	  // previous statement with no branching and does not contain the value
	  if (currentlyNewValueNumberInDifferentBlock) {
	    ValueNumber newVn = getEquivalentValueNumberForNewBlock(vn,
		currentLoc, tempCheckingLoc.getLocation(), vnaDataflow);
	    if (newVn == null)
	    {
	      newVn = getEquivalentValueNumberForNewBlock(vn,
			currentLoc, tempCheckingLoc.getLocation(), vnaDataflow);
	    }
	    dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
		vnaDataflow, newVn, lost, lostThenFound,
		newVn.hasFlag(ValueNumber.PHI_NODE));
	  } else {
	    if (!lost || lostThenFound) {
	      locList.add(tempCheckingLoc.getLocation());
	    } else {
	      dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
		  vnaDataflow, vn, lost, lostThenFound,
		  currentlyNewValueNumberInDifferentBlock);
	    }
	  }
	}
      } else {
	if (cfgVnf.contains(vn)) {
	  // branching and contains the value
	  // not sure what the correct way to handle
	  // this option is.
	  // I don't think it will happen but handling
	  // it to be sure
	  boolean newLostThenFound = lostThenFound;
	  if (lost) {
	    newLostThenFound = true;
	  }
	  dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
	      vnaDataflow, vn, lost, newLostThenFound,
	      currentlyNewValueNumberInDifferentBlock);
	} else {
	  // branching and does not contain the value
	  if (currentlyNewValueNumberInDifferentBlock) {
	    ValueNumber newVn = getEquivalentValueNumberForNewBlock(vn,
		currentLoc, tempCheckingLoc.getLocation(), vnaDataflow);
	    dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
		vnaDataflow, newVn, lost, lostThenFound,
		newVn.hasFlag(ValueNumber.PHI_NODE));
	  } else {
	    dfsForVariable(cfg, tempCheckingLoc.getLocation(), locList,
		vnaDataflow, vn, lost, lostThenFound, false);
	  }
	}
      }
    }
  }

  private ValueNumber getEquivalentValueNumberForNewBlock(
      ValueNumber oldValueNumber, Location oldLocation, Location newLocation,
      ValueNumberDataflow vnaDataflow) {
    ValueNumberFrame oldVnf = vnaDataflow.getAnalysis().getFactAtLocation(
	oldLocation);
    ValueNumberFrame newVnf = vnaDataflow.getAnalysis().getFactAfterLocation(
	newLocation);
    for (int i = 0; i < oldVnf.getNumSlots(); i++) {
      if (oldVnf.getValue(i).equals(oldValueNumber)) {
	return newVnf.getValue(i);
      }
    }
    return null;
  }

  private boolean prescreen(Method m, MethodGen methodGen,
      ClassContext classContext) {
    if (m.getCode() == null) {
      return false;
    }
    if (methodGen == null) {
      return false;
    }
    BitSet byteCodeSet = classContext.getBytecodeSet(m);

    if (byteCodeSet == null || !byteCodeSet.intersects(testSet)) {
      return false;
    }
    return true;
  }

  @Override
  protected void visitMethodCFG(MethodDescriptor methodDescriptor, CFG cfg)
      throws CheckedAnalysisException {
    /*
     * XMethod xmethod = XFactory.createXMethod(methodDescriptor);
     * IAnalysisCache analysisCache = Global.getAnalysisCache();
     * IsNullValueDataflow dataflow; try { dataflow =
     * analysisCache.getMethodAnalysis(IsNullValueDataflow.class,
     * methodDescriptor); } catch (ObligationAcquiredOrReleasedInLoopException
     * e) { // It is not possible to analyze this method. /*if (DEBUG) {
     * System.out.println("FindUnsatisifedObligation: " + methodDescriptor +
     * ": " + e.getMessage()); } //return;
     * System.err.println("Caught Excpetion creating dataflow!!!!");
     * e.printStackTrace(); System.exit(1); } IsNullValueFrame endFieldSet =
     * dataflow.getResultFact(cfg.getExit()); //endFieldSet.contains(/*Not sure
     * what field to put here at the moment); //XField testField = new XField();
     * //this line should be deleted later
     * 
     * Iterator<Location> i = cfg.locationIterator(); while (i.hasNext()) {
     * Location location = i.next(); org.apache.bcel.generic.InstructionHandle
     * handle = location.getHandle(); Instruction ins = handle.getInstruction();
     * if (ins instanceof InvokeInstruction) { InvokeInstruction invoke =
     * (InvokeInstruction) ins; IAnalysisCache analysisCache =
     * Global.getAnalysisCache(); ClassContext classContext =
     * analysisCache.getClassAnalysis(ClassContext.class, classDescriptor);
     * methodDescriptor.getSignature(); if (invoke.getSignature(cpg))
     * 
     * getClassConstantOperand().equals("java/lang/System") &&
     * getNameConstantOperand().equals("setSecurityManager") &&
     * sigOperand.equals("(Ljava/lang/SecurityManager;)V") } /*if
     * (ins.getOpcode() == 184) //This is the INVOKESTATIC call {
     * System.out.println(ins.getName()); }
     * 
     * }
     */

  }

}
