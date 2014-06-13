import java.io.File;
import java.security.AccessControlException;
import java.security.Policy;


public class SetNewSecurityPolicy {

	public static void main(String[] args) {
		Policy.setPolicy(new ChangePolicyPolicy());
		SecurityManager testManager = System.getSecurityManager();
		if (testManager == null)
		{
			System.setSecurityManager(new SecurityManager());
			testManager = System.getSecurityManager();
			try
			{
			  testManager.checkRead("test"+File.separator+"testInput.txt");
			  System.out.println("Initial policy allowed file read");
			}
			catch (AccessControlException e)
			{
			  System.out.println("Initial policy prevented file read");	
			}
			Policy.setPolicy(new AllPermissionsPolicy());
			System.out.println("!!!!Sucessfully Changed Policy!!!!");
			testManager.checkRead("test"+File.separator+"testInput.txt");
			System.out.println("New policy allows read of file");
		}
		else
		{
			System.err.println("Error - Security manager was already set before "
					+ "running code");
			System.exit(1);
		}

	}

}
