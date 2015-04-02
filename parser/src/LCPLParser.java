// Andrei Parvu
// 341C3

import ro.pub.cs.lcpl.*;
import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.*;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.yaml.snakeyaml.*;
import org.antlr.stringtemplate.*;

import java.util.regex.*;
import java.util.*;

public class LCPLParser {

  public static void main(String[] args) {
    CommonTree rootNode = null;

    if (args.length < 2)
    {
      System.err.println("Usage: LCPLParser <file.lcpl> <file.yaml> [<bonus>]\n");
      System.exit(1);
    }
    try {
      // Set up lexer and parser to read the file passed on the command line 
      FileInputStream fis = new FileInputStream(args[0]);
      ANTLRInputStream input = new ANTLRInputStream(fis);
      LCPLTreeBuilderLexer lexer = new LCPLTreeBuilderLexer(input);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      LCPLTreeBuilderParser parser = new LCPLTreeBuilderParser(tokenStream);
      // Parse the file and generate a tree
      LCPLTreeBuilderParser.program_return retVal = parser.program();
      fis.close();
      // Set up the tree grammar to parse the tree
      rootNode = (CommonTree)retVal.getTree();

      CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(rootNode);
      LCPLTreeChecker checker = new LCPLTreeChecker(nodeStream);
      // Generate Java objects
      Program prg = checker.program();
      // Serialize Java objects
      Yaml yaml = new Yaml();
      PrintStream fos = new PrintStream(new FileOutputStream(args[1]));
      fos.println(yaml.dump(prg));
      fos.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (RecognitionException ex) {
      ex.printStackTrace();
    }
  }
}
