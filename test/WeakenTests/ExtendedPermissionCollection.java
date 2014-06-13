package WeakenTests;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.PropertyPermission;


public class ExtendedPermissionCollection extends PermissionCollection {
	
	Permissions perms = new Permissions();
	
	public void add(Permission permission) {
		//need to check for policy change in this method statically as well
	}

	public boolean implies(Permission permission) {
			return perms.implies(permission);
	}

	public void allowAllPermissions()
	{
		perms.add(new AllPermission());
	}
	
	/*  This method is supposed to return an enumeration of all of the elements
	 * in the collection.  Because I don't think we will ever use it, I'm 
	 * not implementing it and will do it later if this method becomes useful.
	 * (non-Javadoc)
	 * @see java.security.PermissionCollection#elements()
	 */
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

	
}


