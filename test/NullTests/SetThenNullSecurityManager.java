package NullTests;
import java.security.Policy;

import WeakenTests.AllPermissionsPolicy;


public class SetThenNullSecurityManager {
	public static void main(String[] args)
	{
		//first check to make sure that the securityManager wasn't set by 
		//the command line
		SecurityManager testManager = System.getSecurityManager();
		if(testManager == null)
		{
			Policy.setPolicy(new AllPermissionsPolicy());
			System.setSecurityManager(new SecurityManager());
			System.out.println("Set the security manager the first time");
			System.setSecurityManager(null);
			System.out.println("Sucessfully set the security manager to null");
		}
		
		else
		{
			System.out.println("Security manager was already set when "
					+ "running this program");
			System.exit(1);
		}
	}

}
