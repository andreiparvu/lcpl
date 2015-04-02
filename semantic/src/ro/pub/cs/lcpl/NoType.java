package ro.pub.cs.lcpl;

/** type for functions or expressions that do not return a value */ 
public class NoType implements Type {
  @Override
  public String getName() {
    return "(none)";
  }
}
