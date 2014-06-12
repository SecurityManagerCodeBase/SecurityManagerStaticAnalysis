/*
 * This class is provides a SecurityManager that can be weakened dynamically
 * to turn of file read checks
 */
public class WeakenedSecurityManager extends SecurityManager{

	private boolean checkingReads = true;
	
	public void changeReadCheck()
	{
		checkingReads = !checkingReads;
	}
	
	public void checkRead(String file) throws SecurityException
	{
		if (checkingReads)
		{
			super.checkRead(file);
		}
	}
}
