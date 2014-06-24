package determineWhereSecurityManagerVariableSet;

import java.util.Iterator;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

import com.sun.org.apache.bcel.internal.generic.ClassGen;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;
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
import edu.umd.cs.findbugs.bcel.BCELUtil;
import edu.umd.cs.findbugs.bcel.CFGDetector;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.Global;
import edu.umd.cs.findbugs.classfile.IAnalysisCache;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class DetectWhereSecurityManagerVariableIsSet extends CFGDetector {
	
	BugReporter bugReporter;
	
	 public DetectWhereSecurityManagerVariableIsSet(BugReporter bugReporter) {
	        this.bugReporter = bugReporter;
	        //IAnalysisCache analysisCache = Global.getAnalysisCache();
	    }
	 
	 @Override
	 public void visitClass(ClassDescriptor classDescriptor) throws CheckedAnalysisException {
		 IAnalysisCache analysisCache = Global.getAnalysisCache();
		 
		 JavaClass jclass = analysisCache.getClassAnalysis(JavaClass.class, classDescriptor);
	        classContext = analysisCache.getClassAnalysis(ClassContext.class, classDescriptor);
	        for (Method m : classContext.getMethodsInCallOrder()) {
	            if (m.getCode() == null) {
	                continue;
	            }
	            method = m;
	            MethodDescriptor methodDescriptor = BCELUtil.getMethodDescriptor(jclass, method);

	            // Try to get MethodGen. If we can't get one,
	            // then this method should be skipped.
	            MethodGen methodGen = analysisCache.getMethodAnalysis(MethodGen.class, methodDescriptor);
	            if (methodGen == null) {
	                continue;
	            }
                InstructionList il = methodGen.getInstructionList();
                Iterator<InstructionHandle> insHandleIter = (Iterator<InstructionHandle>)il.iterator();
                CFG cfg = analysisCache.getMethodAnalysis(CFG.class, methodDescriptor);
                while(insHandleIter.hasNext())
                {
                	InstructionHandle insHandle = insHandleIter.next();
                	Instruction ins = insHandle.getInstruction();
                	if (ins instanceof InvokeInstruction)
                	{
                		//InstructionFactory
                		InvokeInstruction invoke = (InvokeInstruction) ins;
                		if (invoke.getMethodName(methodGen.getConstantPool()).equals("setSecurityManager")) 
                		{
                			//	&& invoke.getSignature(methodGen.getConstantPool()).equals("(Ljava/lang/SecurityManager;)V"))
                	    //System.out.println("Method name: "+invoke.getMethodName(methodGen.getConstantPool()));
                		  System.out.println("Signature: "+invoke.getSignature(methodGen.getConstantPool()));

                		  //ValueNumberDataflow vnaDataflow = classContext.getValueNumberDataflow(invoke.getMethodName(methodGen.getConstantPool()));
                		  //ValueNumber vn = vnaDataflow.getAnalysis().getEntryValueForParameter(0);
                		  //System.out.println("Value Number: "+String.valueOf(vn.getNumber()));
                		  /*int paramLocalOffset = method.isStatic() ? 0 : 1;
                		  SignatureParser parser = new SignatureParser(method.getSignature());
                		  Iterator<String> paramIterator = parser.parameterSignatureIterator();
                		  */
                		  org.apache.bcel.generic.Type[] types = invoke.getArgumentTypes(methodGen.getConstantPool());
                		  System.out.println("types:");
                		  for(int i = 0; i < types.length; i++)
                		  {
                			  System.out.println("Type "+String.valueOf(i)+": "+types[i].toString());
                		  }
                		  
                		  System.out.println("Consume stack result: "+invoke.consumeStack(methodGen.getConstantPool()));
                		  //OpcodeStack opStack = new OpcodeStack();
                		  IsNullValueDataflow isNullDataflow = classContext.getIsNullValueDataflow(method);
                		  Iterator<Location> locIter = cfg.getLocationsContainingInstructionWithOffset(insHandle.getPosition()).iterator();
                		  TypeDataflow typeDataflow = classContext.getTypeDataflow(method);
                		  while(locIter.hasNext())
                		  {
                			Location currentLoc = locIter.next();
                		    IsNullValueFrame nullFrame = isNullDataflow.getFactAtLocation(currentLoc);
                		    if(!nullFrame.isValid())
                		    {
                		    	System.err.println("nullFrame is not valid");
                		    	System.err.println("currentLoc: "+currentLoc.toCompactString());
                		    }
                		    IsNullValue operandNullness = nullFrame.getTopValue();
                		    TypeFrame frame = typeDataflow.getFactAtLocation(currentLoc);
                		    Type operandType = frame.getTopValue();
                		    if (operandType.equals(NullType.instance()) || operandNullness.isDefinitelyNull())
                		    {
                		      bugReporter.reportBug(new BugInstance(this, "DETECT_SECURITY_MANAGER_SET_LOCATION_BUG",HIGH_PRIORITY)
            		          .addClassAndMethod(methodDescriptor).addString("\nSet to null: "+frame.getStackValue(0)+"\n").addSourceLine(methodDescriptor, currentLoc));
                		    }
                		    else
                		    {
                		    	bugReporter.reportBug(new BugInstance(this, "DETECT_SECURITY_MANAGER_SET_LOCATION_BUG",HIGH_PRIORITY)
              		          .addClassAndMethod(methodDescriptor).addString("\nCould not be set to null: "+frame.getStackValue(0)+"\n").addSourceLine(methodDescriptor, currentLoc));
                		    }
                		  }
                		  
                		  //Location loc = new Location(insHandle, cfg.getEntry());
                		    //.addSourceLine(invoke));
                		//getClassConstantOperand().equals("java/lang/System")
                       // && getNameConstantOperand().equals("setSecurityManager") 
                       // && sigOperand.equals("(Ljava/lang/SecurityManager;)V")
                		//SourceLineAnnotation
                		}
                }
                
	            
	            visitMethodCFG(methodDescriptor, cfg);
	        
                }
           }
	 }

	@Override
	protected void visitMethodCFG(MethodDescriptor methodDescriptor, CFG cfg)
			throws CheckedAnalysisException {
		/*XMethod xmethod = XFactory.createXMethod(methodDescriptor);
        IAnalysisCache analysisCache = Global.getAnalysisCache();
        IsNullValueDataflow dataflow;
        try {
            dataflow = analysisCache.getMethodAnalysis(IsNullValueDataflow.class, methodDescriptor);
        } catch (ObligationAcquiredOrReleasedInLoopException e) {
            // It is not possible to analyze this method.
            /*if (DEBUG) {
                System.out.println("FindUnsatisifedObligation: " + methodDescriptor + ": " + e.getMessage());
            }
            //return;
        	System.err.println("Caught Excpetion creating dataflow!!!!");
        	e.printStackTrace();
        	System.exit(1);
        }
        IsNullValueFrame endFieldSet = dataflow.getResultFact(cfg.getExit());
        //endFieldSet.contains(/*Not sure what field to put here at the moment);
       //XField testField = new XField(); //this line should be deleted later
        
        Iterator<Location> i = cfg.locationIterator();
        while (i.hasNext())
        {
        	Location location = i.next();
        	org.apache.bcel.generic.InstructionHandle handle = location.getHandle();
        	Instruction ins = handle.getInstruction();
        	if (ins instanceof InvokeInstruction)
        	{
        		InvokeInstruction invoke = (InvokeInstruction) ins;
        		IAnalysisCache analysisCache = Global.getAnalysisCache();
                ClassContext classContext = analysisCache.getClassAnalysis(ClassContext.class, classDescriptor);
        		methodDescriptor.getSignature();
                if (invoke.getSignature(cpg))
        	
        		getClassConstantOperand().equals("java/lang/System")
                && getNameConstantOperand().equals("setSecurityManager") 
                && sigOperand.equals("(Ljava/lang/SecurityManager;)V")
        	}
        	/*if (ins.getOpcode() == 184) //This is the INVOKESTATIC call
        	{
        	  System.out.println(ins.getName());	
        	}
        	
        }*/
		
	}
	

}
