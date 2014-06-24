package detectMultipleSecurityManagerSets;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;


public class DetectMultipleSecurityManagerSets extends OpcodeStackDetector {

	private BugReporter bugReporter;
	int smSetsSeen;
	public DetectMultipleSecurityManagerSets(BugReporter br)
	{
		bugReporter = br;
		smSetsSeen = 0;
	}
	@Override
	public void sawOpcode(int opcodeSeen) {
		boolean check = true;
		String sigOperand = "";
		try
		{
		  sigOperand = getSigConstantOperand().toString();
		}
		catch(IllegalStateException e)
		{
		  check = false;
		}
		if (check == true && opcodeSeen == INVOKESTATIC && getClassConstantOperand().equals("java/lang/System")
                && getNameConstantOperand().equals("setSecurityManager") && sigOperand.equals("(Ljava/lang/SecurityManager;)V"))
			//System.out.println("ConstantOperande: "+getSigConstantOperand().toString());
			smSetsSeen = smSetsSeen + 1;
		    if (smSetsSeen > 1)
		    {
		    System.out.println("Creating a bug report");
		      bugReporter.reportBug(new BugInstance(this, "MULTIPLE_SECURITY_MANAGER_SET_BUG",HIGH_PRIORITY)
		      .addClassAndMethod(this).addString("ConstantOperand: "+sigOperand).addSourceLine(this));
		    }	
	}
	
	
}
