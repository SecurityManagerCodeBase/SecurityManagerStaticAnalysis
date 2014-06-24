package Expiremental;

public class SaveSecurityManagerInVariables {
	public static void main(String[] args) {
		SecurityManager testManager = System.getSecurityManager();
        if (testManager == null)
        {
        	SecurityManager nolan = null;
        	SecurityManager tod = new SecurityManager();
        	System.setSecurityManager(nolan);
        	System.out.println("Set the first securityManager");
        	System.setSecurityManager(tod);
        	System.out.println("Set the second securityManager");	
        }
        else
        {
        	System.out.println("The security manager was set before"
        			+ "running the program");
        	System.exit(1);
        }
	}
}
