package NullTests;
import java.lang.reflect.Field;

import sun.misc.Unsafe;


public class NullSecurityManagerUsingAddress {

	public static void main(String[] args) throws SecurityException {
		System.out.println(System.getProperty("java.runtime.version"));
		SecurityManager testManager = System.getSecurityManager();
		if (testManager == null)
		{
		  //final Unsafe unsafe = Unsafe.getUnsafe();//Doesn't work - protected to throw a security error
			final Unsafe unsafe = getUnsafe();
			if(unsafe == null)
			{
				System.err.println("Failed to get the unsafe class with reflection");
				System.exit(1);
			}
			else
			{
				 System.out.println("Java byte version: "+System.getProperty("os.arch"));
				 if("amd64".equals(System.getProperty("os.arch"))) //checking if 64 bit - probably need to check for others as well later
				 {
					 //System.setSecurityManager(new SecurityManager());
					 testManager = System.getSecurityManager();
					 SecurityManager smArray[] = new SecurityManager[1];
					 smArray[0] = testManager;
					 long baseOffset = unsafe.arrayBaseOffset(SecurityManager[].class);
					 long addressOfSecurityManager = unsafe.getLong(smArray, baseOffset);
					 //unsafe.setMemory(addressOfSecurityManager, 8, (byte) 0); //8 bytes for 64 bit
					 Field security = null;
					try {
						security = System.class.getDeclaredField("security");
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					long securityManagerOffset = 0;
					if (security == null)
					{
						System.out.println("Security manager was not found");
					}
					else
					{
						securityManagerOffset = unsafe.objectFieldOffset(security);
					}
					
					 unsafe.putAddress(addressOfSecurityManager+32, 0);
					 if (System.getSecurityManager()==null)						 
					 {
					   System.out.println("SecurityManager set to null");
					 }
					 else
					 {
						 System.out.println("SecurityManager still active");
					 }
				 }
				 else
				 {
					 System.err.println("Using other Java JVM bit version");
					 System.exit(1);
				 }
			}
		 
		  //long systemAddress =
		}
		else
		{
			System.err.println("Error - Security manager was already set before "
					+ "running code");
			System.exit(1);
		}

	}
	
	public static Unsafe getUnsafe()
	{
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return(Unsafe)f.get(null);
			
		}catch(Exception e)
		{
			
		}
		return null;
	}

}
