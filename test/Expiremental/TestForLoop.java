package Expiremental;

import java.util.Random;

public class TestForLoop {

  public static void main(String args[]){
    SecurityManager sm = System.getSecurityManager();
    Random rand = new Random();
    int maxIter = rand.nextInt();
    for (int i = 0; i < maxIter; i++)
    {
      System.out.println("something");
    }
    System.setSecurityManager(sm);
    System.out.println("finished");
  }
}
