package ro.pub.cs.lcpl;

import java.io.PrintStream;

/** Marker for a variable - formal parameter, attribute or local variable
 */
public interface Variable {
  public String getName();

  public String load(PrintStream os, boolean isValue);
}
