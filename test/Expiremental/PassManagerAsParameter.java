package Expiremental;

public class PassManagerAsParameter {

  public static void main(String[] args) {
    System.out.println("Starting");
    SecurityManager steve = new SecurityManager();
    
    setSM(steve);
    System.out.println("finished");
  }
  
  private static void setSM(SecurityManager sm)
  {
    System.setSecurityManager(sm);
  } 

}
