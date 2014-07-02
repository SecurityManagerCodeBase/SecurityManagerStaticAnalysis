package Expiremental;
import java.io.IOException;
import java.util.Scanner;


public class SetSecurityManagerValueInExceptionHandling {
  public static void main(String args[])
  {
    SecurityManager jared = null;
    try{
      System.out.println("running test");
      Scanner scanner = new Scanner(System.in);
      if (scanner.next().equals("yes")) {
	throw new IOException();
        
      }
      
    }
    catch (IOException e)
    {
      jared = new SecurityManager();
      
    }
    System.setSecurityManager(jared);
  }

}
