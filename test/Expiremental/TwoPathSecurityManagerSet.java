package Expiremental;

import java.util.Scanner;

public class TwoPathSecurityManagerSet {
	public static void main(String[] args) {
		SecurityManager testManager = System.getSecurityManager();
        if (testManager == null)
        {
        	Scanner	scanner = new Scanner(System.in);
        	SecurityManager sm;
        	if(scanner.next().equals("yes"))
        	{
        		sm = new SecurityManager();
        	}
        	else
        	{
        		sm = null;
        	}
        	System.setSecurityManager(sm);
        }
        else
        {
        	System.out.println("The security manager was set before"
        			+ "running the program");
        	System.exit(1);
        }
	}
}
