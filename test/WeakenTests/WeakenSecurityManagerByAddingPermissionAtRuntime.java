package WeakenTests;
import java.io.File;
import java.security.AccessControlException;
import java.security.Policy;


public class WeakenSecurityManagerByAddingPermissionAtRuntime 
{
	public static void main(String args[])
	{
      ExtendedPermissionCollection epc = new ExtendedPermissionCollection();
      Policy.setPolicy(new DynamicallyWeakenedPolicyThroughPermissionAddition(epc));
      System.setSecurityManager(new SecurityManager());
      SecurityManager sm = System.getSecurityManager();
      try
      {
        sm.checkRead("test"+File.separator+"testInput.txt");
        System.out.println("Initial SecurityManager allows file read");
      } catch (AccessControlException e)
      {
    	System.out.println("Initial SecurityManager denies file read");  
      }
      epc.allowAllPermissions();
      System.out.println("!!!!!Added an all permissions to the permission list!!!!!");
      try
      {
        sm.checkRead("test"+File.separator+"testInput.txt");
        System.out.println("Final SecurityManager allows file read");
      } catch (AccessControlException e)
      {
    	System.out.println("Final SecurityManager denies file read");  
      }
	}
}
