import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;


public class DetectTwoSecurityManagerSets extends OpcodeStackDetector {

	int smSetsSeen;
	public DetectTwoSecurityManagerSets()
	{
		smSetsSeen = 0;
	}
	@Override
	public void sawOpcode(int opcodeSeen) {
		if (opcodeSeen == INVOKESPECIAL && getClassConstantOperand().equals("java/lang/System")
                && getNameConstantOperand().equals("setSecurityManager"))
			System.out.println("ConstantOperande: "+getSigConstantOperand().toString());
		
	}
	
	
}
