package ro.pub.cs.lcpl;

/** Marker for a variable - formal parameter, attribute or local variable
 */
public interface Variable {
  public String getName();

  // wrappere pentru tipul de date folosit, combina setTypeData si setVariableData
  public Type takeTypeData();

  public void setTypeData(Type typeData);
}
