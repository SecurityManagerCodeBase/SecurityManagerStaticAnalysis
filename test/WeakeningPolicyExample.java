//Based on an example from http://gemsres.com/story/oct05/140122/source.html
// main story - http://java.sys-con.com/node/140122?page=0,0

import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.PropertyPermission;

public class WeakeningPolicyExample extends Policy {
	private static CodeSource appCodeSource;
	private static PermissionCollection permissions;
	private static Permissions allPermissions;

	// assume that TestPolicy and the application are from the same code source
	// static
	{
		appCodeSource = WeakeningPolicyExample.class.getProtectionDomain()
				.getCodeSource();
		permissions = new MyPermissionCollection();
		allPermissions = new Permissions();
		allPermissions.add(new AllPermission());
	}

	public class MyPermissionCollection extends PermissionCollection {
		public void add(Permission permission) {
		}

		public boolean implies(Permission permission) {
			if (permission instanceof PropertyPermission) {
				return getAllowedPropertyPermissions().implies(permission);
			}
			// we allow all other permissions
			return true;
		}

		public Enumeration elements() {
			return new Enumeration() {
				public boolean hasMoreElements() {
					return false;
				}

				public Object nextElement() {
					return null;
				}
			};
		}

		// This method is supposed to return dynamic results
		private Permissions getAllowedPropertyPermissions() {
			Permissions perms = new Permissions();
			perms.add(new PropertyPermission("user.dir", "read"));
			perms.add(new PropertyPermission("user.home", "read"));
			return perms;
		}
	};

	public PermissionCollection getPermissions(CodeSource codesource) {
		if (appCodeSource.equals(codesource)) {
			return permissions;
		}
		return allPermissions;
	}

	public boolean implies(ProtectionDomain domain, Permission permission) {
		return getPermissions(domain.getCodeSource()).implies(permission);
	}

	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}

	public void refresh() {
	}

	public static void main(String[] args) throws Exception {
		/*ProtectionDomain pd = WeakeningPolicyExample.class.getProtectionDomain();
		CodeSource cs = pd.getCodeSource();
		URL url = cs.getLocation();
		System.out.println("URL example: "+url.toString());*/
		Policy.setPolicy(new WeakeningPolicyExample());
		System.setSecurityManager(new SecurityManager());
		System.out.println(System.getProperty("user.home"));
	}
}
