import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessControlException;
import java.security.Policy;

public class SetSecurityManagerThenWeakenSecurityManager {

	public static void main(String[] args) {
		// printCurrentDirectory(); //used to see where Eclipse was running the
		// code from
		SecurityManager testManager = System.getSecurityManager();
		System.out.println("Set security manager");
		if (testManager == null) {
			/*
			 * System.setSecurityManager(new SecurityManager()); Policy
			 * testPolicy = Policy.getPolicy();
			 * System.out.println("type of policy: "+testPolicy.getType());
			 * //This isn't finished.
			 */
			WeakenedSecurityManager weakSecManager = new WeakenedSecurityManager();
			System.setSecurityManager(weakSecManager);
			String file = "test"+File.separator+"testInput.txt";
			attemptToAccessFileAndThenCheckPermission(file);
			weakSecManager.changeReadCheck();
			System.out.println("!!!!!!!Sucessfully weakened security manager- allows file reads!!!!!!");
			attemptToAccessFileAndThenCheckPermission(file);
			testWritePermission("test/unimportantFile.txt");
		} else {
			System.out.println("Security manager was already set before "
					+ "running the code");
			System.exit(1);
		}
	}

	private static void attemptToAccessFileAndThenCheckPermission(String file) {
		try {

			attemptToReadFile(file);
		} catch (AccessControlException e) {
			System.out
					.println("Security Manager caught and denied the file read");
		}
		SecurityManager currentManager = System.getSecurityManager();
		try {
			currentManager.checkRead(file);
			System.out
					.println("Security Manager allowed the file read security check");
		} catch (AccessControlException e) {
			System.out
					.println("Security Manager denied the file read security check");
		}
	}

	private static void attemptToReadFile(String file) {
		BufferedReader testReader;
		try {
			System.out.println("Trying to read from file");
			testReader = new BufferedReader(new FileReader(new File(file)));
			System.out.println("Succcessfully opened file");
			String line;
			System.out.println("*******Printing File Contents********");
			while ((line = testReader.readLine()) != null) {
				System.out.println(line);
			}
			System.out.println("*******End of File Contents********");
			System.out.println("Security Manager allowed read from file");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Unable to open file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error when reading file");
		}

	}

	/*
	 * Prints the directory the code is currently executing from. Used to find
	 * the directory to make the relative path from. private static void
	 * printCurrentDirectory() { Path relativePathStartingAtCurrentDir =
	 * Paths.get(""); String pathToCurrentDir =
	 * relativePathStartingAtCurrentDir.toAbsolutePath().toString();
	 * System.out.println("Current directory is: "+pathToCurrentDir); }
	 */

	private static void testWritePermission(String file) {
		try {

			PrintWriter writer;
			try {
				writer = new PrintWriter(file, "UTF-8");
				writer.println("If you are reading this, you can delete this file.");
				writer.println("This file should not be created if the test executes correctly.");
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (AccessControlException e) {
            System.out.println("Security Manager caught a write attempt.\nThis shows it is weakened and not set to null.");
		}

	}
}
