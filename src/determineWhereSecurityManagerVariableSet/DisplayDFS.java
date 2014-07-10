package determineWhereSecurityManagerVariableSet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class DisplayDFS extends JApplet{
  
  class DFSDisplayNode
  {
    int level;
    String instruction;
    DFSDisplayNode parent;
    int xPos = 0;
    int yPos = 0;
    public DFSDisplayNode(int level, String instruction, DFSDisplayNode parent)
    {
      this.level = level;
      this.instruction = instruction;
      this.parent = parent;
    }
    
    public int getLevel(){
      return this.level;
    }
   
    public String getInstruction(){
	  return this.instruction;
    }
	
    public DFSDisplayNode getParent(){
      return this.parent;
    }
    
    public void setXPos(int xPos){
     this.xPos = xPos; 
    }
    
    public void setYPos(int yPos){
      this.yPos = yPos;
    }
    
    public int getXPos(){
      return this.xPos;
    }
    
    public int getYPos(){
      return this.yPos;
    }
  }
  
  
  ArrayList<DFSDisplayNode> nodes = new ArrayList<DFSDisplayNode>();
  int xPos = 5;
  
  public void init(){
    super.init();
    setBackground(Color.black);
    setForeground(Color.white);
  }
  
  
  public void paint(Graphics g)
  {
    super.paint(g);
    Graphics2D g2 = (Graphics2D) g;
    Dimension d = getSize();
    g2.setPaint(Color.red);
    int maxDepth = 0;
    for(int i = 0; i < nodes.size();i++)
    {
      if( nodes.get(i).getLevel() > maxDepth)
      {
	maxDepth = nodes.get(i).getLevel();
      }
    }
    int textYSeperation = d.height/(maxDepth+1);
    ArrayList<DFSDisplayNode> nodesAtDepth; 
    for(int currentDepth = 0; currentDepth < (maxDepth+1); currentDepth++){
      nodesAtDepth = new ArrayList<DFSDisplayNode>();
      for(int i = 0; i < nodes.size();i++){
       if(nodes.get(i).getLevel()==currentDepth){
	 nodesAtDepth.add(nodes.get(i));
       }
      }
      int textXSeperation = d.width/(nodesAtDepth.size()+1);
      for(int nodeAtDepthIndex = 0; nodeAtDepthIndex < nodesAtDepth.size(); nodeAtDepthIndex++){
	DFSDisplayNode dNode = nodesAtDepth.get(nodeAtDepthIndex);
	dNode.setXPos(textXSeperation*(nodeAtDepthIndex+1));
	//dNode.setYPos(d.height - textYSeperation*(currentDepth));
	dNode.setYPos(textYSeperation*(currentDepth));
	g2.drawChars(dNode.getInstruction().toCharArray(), 0, dNode.getInstruction().toCharArray().length,dNode.getXPos(), dNode.getYPos());
	if(dNode.getParent() != null){
	  g2.draw(new Line2D.Double(dNode.getXPos(), dNode.getYPos(), dNode.getParent().getXPos(), dNode.getParent().getYPos()));
	}
      }
    }
    String outputText = "sample instruction";
    
  }
  
  public DFSDisplayNode addNode(int level, String instruction, DFSDisplayNode parent)
  {
    DFSDisplayNode dNode = new DFSDisplayNode(level, instruction,parent);
    nodes.add(dNode);
    return dNode;
  }
  
  public void update()
  {
    System.out.println("updating");
    xPos = 50;
  }


  public static void main(String args[])
  {
    
    
    System.out.flush();
    JFrame f = new JFrame("DFS Display");
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
    });
    DisplayDFS applet = new DisplayDFS();
    DisplayDFS.DFSDisplayNode node = applet.addNode(1, "head instruction",null); 
    applet.addNode(2, "normal instruction",node);
    applet.addNode(2, "branch instruction",node);
    applet.init();
    f.add(applet);
    f.pack();
    f.setSize(new Dimension(1000,1000));
    f.setVisible(true);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    applet.update();
    applet.repaint();
  }
}
