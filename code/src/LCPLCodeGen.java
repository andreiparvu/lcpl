// Andrei Parvu
// 341C3

import java.io.*;

import ro.pub.cs.lcpl.*;

import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.Constructor;

import src.Properties;

public class LCPLCodeGen {
  private static PrintStream fos;

  private static void genStandardCode() {
    // Structurile si functiile mostenite din runtime
    fos.println(";;; Standard Code\n" +
      "%struct.TObject = type { %struct.__lcpl_rtti* }\n" +
      "%struct.TString = type { %struct.__lcpl_rtti*, i32, i8* }\n" +
      "%struct.TIO = type { %struct.__lcpl_rtti* }\n" +
      "%struct.__lcpl_rtti = type { %struct.TString*, i32, %struct.__lcpl_rtti*, [0 x i8*] }\n" +
      "\n" +
      "@RObject = external global %struct.__lcpl_rtti\n" +
      "@RString = external global %struct.__lcpl_rtti\n" +
      "@RIO = external global %struct.__lcpl_rtti\n" +
      "\n" +
      "declare void @Object_init(%struct.TObject*)\n" +
      "declare void @M6_Object_abort(%struct.TObject*)\n" +
      "declare %struct.TString* @M6_Object_typeName(%struct.TObject*)\n" +
      "declare %struct.TObject* @M6_Object_copy(%struct.TObject*)\n" +
      "\n" +
      "declare void @IO_init(%struct.TIO*)\n" +
      "declare %struct.TString* @M2_IO_in(%struct.TIO*)\n" +
      "declare %struct.TIO* @M2_IO_out(%struct.TIO*, %struct.TString*)\n" +
      "\n" +
      "declare void @String_init(%struct.TString*)\n" +
      "declare i32 @M6_String_length(%struct.TString*)\n" +
      "declare i32 @M6_String_toInt(%struct.TString*)\n" +
      "declare %struct.TString* @M6_String_substring(%struct.TString*, i32, i32)\n" +
      "declare %struct.TString* @M6_String_concat(%struct.TString*, %struct.TString*)\n" +
      "declare i32 @M6_String_equal(%struct.TString*, %struct.TString*)\n" +
      "\n" +
      "declare i8* @__lcpl_new(%struct.__lcpl_rtti*)\n" +
      "declare void @__lcpl_checkNull(i8*)\n" +
      "declare i8* @__lcpl_cast(i8*, %struct.__lcpl_rtti*)\n" +
      "declare %struct.TString* @__lcpl_intToString(i32)\n"
    );
  }

  private static void emitStartup() {
    // Metoda de startup
    fos.println(";;; Startup function\n" +
      "define void @startup() {\n" +
      "\t%1 = call i8* @__lcpl_new(%struct.__lcpl_rtti* bitcast (%RMain* @RMain to %struct.__lcpl_rtti*))\n" +
      "\t%2 = bitcast i8* %1 to %struct.TMain*\n" +
      "\t%3 = bitcast %struct.TMain* %2 to i8*\n" +
      "\tcall void @__lcpl_checkNull(i8* %3)\n" +
      "\t%4 = getelementptr %struct.TMain* %2, i32 0, i32 0\n" +
      "\t%5 = load %struct.__lcpl_rtti** %4\n" +
      "\t%6 = getelementptr %struct.__lcpl_rtti* %5, i32 0, i32 3\n" +
      "\t%7 = bitcast [0 x i8*]* %6 to i8**\n" +
      "\t%8 = getelementptr i8** %7, i32 " + (Properties.main.methodTable.indexOf("main") + 1) + "\n" +
      "\t%9 = load i8** %8\n" +
      "\t%10 = bitcast i8* %9 to void (%struct.TMain*)*\n" +
      "\tcall void %10(%struct.TMain* %2)\n" +
      "\n\tret void\n" +
      "}\n"
    );
  }

  public static void main(String[] args) {
    if (args.length != 2)
    {
      System.err.println("Usage: LCPLCodeGen <filein.run> <fileout.ir>\n");
      System.exit(1);
    }
    try {
      Yaml yaml = new Yaml(new Constructor(Program.class));
      FileInputStream fis = new FileInputStream(args[0]);
      Program p = (Program) yaml.load(fis);
      fis.close();

      fos = new PrintStream(new FileOutputStream(args[1]));
      // Emitem codul standard, codul programului si metoda de startup
      genStandardCode();
      p.emitIR(fos);
      emitStartup();
      fos.close();
    } catch (IOException ex) {
      System.err.println("File error: " + ex.getMessage());
      System.err.println("===================================================");
    }

  }

}
