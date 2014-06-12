
public class SetSecurityManager {

	public static void main(String[] args) {
		//First check to make sure there is no current security manager
		SecurityManager testManager = System.getSecurityManager();
		if (testManager == null)
		{
			System.setSecurityManager(new SecurityManager());
			testManager = System.getSecurityManager();
			if (testManager==null)
			{
				System.out.println("Did not set the security manager");
				System.exit(1);
			}
			else
			{
				System.out.println("Successfully set the security manager");
			}	
		}
		else
		{
			System.out.println("Security Manager started with a default value.");
		    System.exit(1);
		}

	}

}
