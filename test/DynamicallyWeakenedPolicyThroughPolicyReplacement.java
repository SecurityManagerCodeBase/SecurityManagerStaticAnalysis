import java.io.FilePermission;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;


public class DynamicallyWeakenedPolicyThroughPolicyReplacement extends Policy{

	//change between two permission sets
	Permissions readPermissions;
	Permissions allPermissions; 
	boolean returnAllPermissions = false;
	
	{
		readPermissions = new Permissions();
		readPermissions.add(new FilePermission("test/testInput.txt","read"));
		allPermissions = new Permissions();
		allPermissions.add(new AllPermission());
	}
	
	public PermissionCollection getPermissions(CodeSource codesource) {
		if (returnAllPermissions) {
			return allPermissions;
		}
		return readPermissions;
	}

	
	public boolean implies(ProtectionDomain domain, Permission permission) {
		return getPermissions(domain.getCodeSource()).implies(permission);
	}

	public PermissionCollection getPermissions(ProtectionDomain domain) {
		return getPermissions(domain.getCodeSource());
	}

	public void refresh() {
		//Don't have to make the change in the refresh method but you can
		returnAllPermissions = !returnAllPermissions;
	}
	
}

