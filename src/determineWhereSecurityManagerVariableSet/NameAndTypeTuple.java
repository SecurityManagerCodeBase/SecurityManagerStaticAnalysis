package determineWhereSecurityManagerVariableSet;

import org.apache.bcel.generic.Type;



public class NameAndTypeTuple {
  String name;
  Type type;
  
  public NameAndTypeTuple(String newName, Type newType)
  {
    name = newName;
    type = newType;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Type getType()
  {
    return type;
  }
}
