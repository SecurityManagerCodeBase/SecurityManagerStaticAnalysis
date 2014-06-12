import java.security.Policy;


public class SetSecurityMangerThenWeakenSecurityManager {
	
	public static void main(String[] args)
	{
		SecurityManager testManager = System.getSecurityManager();
		if (testManager == null)
		{
			System.setSecurityManager(new SecurityManager());
			Policy testPolicy = Policy.getPolicy();
			System.out.println("type of policy: "+testPolicy.getType());
			//This isn't finished.
		}
		else
		{
			System.out.println("Security manager was already set before "
					+ "running the code");
			System.exit(1);
		}
	}

}
