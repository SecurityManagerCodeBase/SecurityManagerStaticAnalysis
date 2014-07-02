package Expiremental;

import java.util.Scanner;

public class InnerProceduralStaticTest {

  public static void main(String[] args) {
    System.out.println("starting");
    Scanner scanner = new Scanner(System.in);
    if (scanner.next().equals("yes")) {
      StaticSecurityManagerHolder.sm = new SecurityManager();
    } else {
      StaticSecurityManagerHolder.sm = null;
    }
    System.setSecurityManager(StaticSecurityManagerHolder.sm);
    System.out.println("ending");
  }
}
