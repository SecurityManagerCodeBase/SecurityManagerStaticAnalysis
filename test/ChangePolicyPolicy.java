import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;


public class ChangePolicyPolicy extends Policy {
Permissions allPermissions; 
	
	{
		allPermissions = new Permissions();
		allPermissions.add(new SecurityPermission("setPolicy"));
	}
	
	public PermissionCollection getPermissions(CodeSource codesource) {
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
}
