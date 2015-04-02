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

public class LCPLParserBonus {

  final static String genFile = "genfile.lcpl";
  final static String vectFile = "src_bonus/Vect.lcpl";

  static String readFile(String path)
      throws IOException {

    // Citim fisierul intr-un string
    return (new Scanner(new File(path)).useDelimiter("\\Z").next());
  }

  public static void main(String[] args) {
    CommonTree rootNode = null;

    if (args.length < 2)
    {
      System.err.println("Usage: LCPLParserBonus <file.lcpl> <file.yaml>\n");
      System.exit(1);
    }
    try {
      HashSet<String> arrayTypes = new HashSet<String>();

      String originalFile = readFile(args[0]);

      // Cautam expresii de forma ID[]
      String pattern = "([a-z|A-Z]*)(\\[\\])";

      Pattern p = Pattern.compile(pattern);
      Matcher m = p.matcher(originalFile);

      while (m.find()) {
        arrayTypes.add(m.group(1));
      }

      String vectorFile = readFile(vectFile);

      Iterator<String> it = arrayTypes.iterator();

      for (; it.hasNext(); ) {
        String type = it.next();

        // Adaugam o noua clasa, VECTORNume_tip
        String newVector = vectorFile.replace("VECTOR", "VECTOR" + type).
          replace("Object", type);

        originalFile += newVector;
      }

      // Scriem noul fisier
      PrintWriter out = new PrintWriter(genFile);
      out.print(originalFile);

      out.close();

      FileInputStream fis = new FileInputStream(genFile);
      ANTLRInputStream input = new ANTLRInputStream(fis);
      LCPLTreeBuilderBonusLexer lexer = new LCPLTreeBuilderBonusLexer(input);
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      LCPLTreeBuilderBonusParser parser = new LCPLTreeBuilderBonusParser(tokenStream);
      // Parse the file and generate a tree
      LCPLTreeBuilderBonusParser.program_return retVal = parser.program();
      fis.close();
      rootNode = (CommonTree)retVal.getTree();
      CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(rootNode);
      LCPLTreeCheckerBonus checker = new LCPLTreeCheckerBonus(nodeStream);
      // Generate Java objects
      Program prg = checker.program();
      // Serialize Java objects
      Yaml yaml = new Yaml();
      PrintStream fos = new PrintStream(new FileOutputStream(args[1]));
      fos.println(yaml.dump(prg));
      fos.close();

      File f = new File(genFile);
      f.delete();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (RecognitionException ex) {
      ex.printStackTrace();
    }
  }
}
