// Andrei Parvu
// 341C3

package src;

import java.util.*;

import ro.pub.cs.lcpl.*;

public class Properties {
	public Program p;
	public LCPLClass curClass;
	public Map<String, LCPLClass> allClasses;
	public FormalParam self;
	public boolean useFolding;

	public Properties(Program p, LCPLClass curClass, Map<String, LCPLClass> allClasses,
		FormalParam self, boolean useFolding) {
		this.p = p;
		this.curClass = curClass;
		this.allClasses = allClasses;
		this.self = self;
		this.useFolding = useFolding;
	}

	public boolean canConvert(String from, String to) {
		// Putem converti Int la String si la Int
		if (from.compareTo("Int") == 0) {
			if (to.compareTo("String") == 0 || to.compareTo("Int") == 0) {
				return true;
			}
		}

		if (allClasses.get(to) == null) {
			return false;
		}

		// Putem converti void la orice clasa
		if (from.compareTo("void") == 0) {
			return true;
		}
		LCPLClass c = allClasses.get(from);

		if (c == null) {
			return false;
		}

		// Cautam pe ierarhia de parinti ai lui 'from' pentru a vedea daca il gasim pe 'to'
		do {
			if (from.compareTo(to) == 0) {
				return true;
			}

			if (c.getParent() == null) {
				return false;
			}

			from = c.getParent();
			c = c.getParentData();
		} while (true);
	}

	public static void classNotFound(String name, TreeNode t) throws LCPLException {
    throw new LCPLException("Class " + name + " not found.", t);
  }

  public static void cannotConvert(String type1, String type2, TreeNode t) throws LCPLException {
  	throw new LCPLException("Cannot convert a value of type " + type1 + " into " + type2, t);
  }	
}

