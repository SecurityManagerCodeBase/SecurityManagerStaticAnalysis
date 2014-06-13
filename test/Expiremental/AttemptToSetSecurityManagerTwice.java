package Expiremental;

public class AttemptToSetSecurityManagerTwice {
	public static void main(String[] args) {
		SecurityManager testManager = System.getSecurityManager();
        if (testManager == null)
        {
        	System.setSecurityManager(new SecurityManager());
        	System.out.println("Set the first securityManager");
        	System.setSecurityManager(new SecurityManager());
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
