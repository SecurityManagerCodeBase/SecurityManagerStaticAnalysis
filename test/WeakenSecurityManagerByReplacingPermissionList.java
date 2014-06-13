import java.io.File;
import java.io.FilePermission;
import java.security.AccessControlException;
import java.security.Policy;

//This shows an example of setting the security manager to use a policy that
//that starts off restricting certain accesses but then changes to 
//allow everything. This is debatebly nulling the security manager since
//everything is allowed but can weaken it to a lesser degree with the 
//same strategy.

public class WeakenSecurityManagerByReplacingPermissionList {

	public static void main(String[] args)
	{
		DynamicallyWeakenedPolicyThroughPolicyReplacement dwp = new DynamicallyWeakenedPolicyThroughPolicyReplacement();
		Policy.setPolicy(dwp);
		System.setSecurityManager(new SecurityManager());
		SecurityManager sm = System.getSecurityManager();
		System.out.println("Set initial security manager");
		sm.checkRead("test"+File.separator+"testInput.txt");
		System.out.println("Intial Security Manager allows read of file");
		try 
		{
			sm.checkWrite("test"+File.separator+"unimportantFile.txt");
			System.out.println("Intial Security Manager allows write to file");
		} catch (AccessControlException e)
		{
			System.out.println("Intial Security Manager prevents write to file");
		}
		dwp.refresh(); 
		System.out.println("!!!!!Changing to new Policy that allows all Permissions!!!!!");
		sm.checkWrite("test"+File.separator+"unimportantFile.txt");
		System.out.println("Changed Security Manager allows read of file");
		sm.checkWrite("test"+File.separator+"unimportantFile.txt");
		System.out.println("Changed Security Manager allows write to file");
	}
	
	
}
