package Expiremental;
/*This file is used to show the difference between having a the default security
 * manager set here and no security manager set in the FullPropertiesTest
 * 
 * Based on an example from Oracle.com
 * 
 * 
 */
public class FailingPermissionTest {

	public static void main(String[] args) {
		/* Test reading properties w & w/out security manager */

		String s;
        System.setSecurityManager(new SecurityManager());
		try {

			System.out.println("About to get os.name property value");

			s = System.getProperty("os.name", "not specified");
			System.out.println("  The name of your operating system is: " + s);

			System.out.println("About to get java.version property value");

			s = System.getProperty("java.version", "not specified");
			System.out.println("  The version of the JVM you are running is: "
					+ s);

			System.out.println("About to get user.home property value");

			s = System.getProperty("user.home", "not specified");
			System.out.println("  Your user home directory is: " + s);

			System.out.println("About to get java.home property value");

			s = System.getProperty("java.home", "not specified");
			System.out.println("  Your JRE installation directory is: " + s);

		} catch (Exception e) {
			System.err.println("Caught exception " + e.toString());
		}

	}

}
