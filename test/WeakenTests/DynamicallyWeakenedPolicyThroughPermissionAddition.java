package WeakenTests;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;

public class DynamicallyWeakenedPolicyThroughPermissionAddition extends Policy {

	ExtendedPermissionCollection epc;

	public DynamicallyWeakenedPolicyThroughPermissionAddition() {
		epc = new ExtendedPermissionCollection();
	}

	public DynamicallyWeakenedPolicyThroughPermissionAddition(
			ExtendedPermissionCollection newEPC) {
		epc = newEPC;
	}

	public PermissionCollection getPermissions(CodeSource codesource) {
		return epc;
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
