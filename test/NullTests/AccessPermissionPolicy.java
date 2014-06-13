package NullTests;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.lang.reflect.ReflectPermission;

public class AccessPermissionPolicy extends Policy {

	Permissions allPermissions;

	{
		allPermissions = new Permissions();
		allPermissions.add(new RuntimePermission("accessDeclaredMembers"));
		allPermissions.add(new RuntimePermission("accessClassInPackage.sun.awt"));
		allPermissions.add(new ReflectPermission("suppressAccessChecks"));
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
