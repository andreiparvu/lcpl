all: build

build:
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src/LCPLTreeBuilder.g
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src/LCPLTreeChecker.g
	javac -cp "lib/*" -d ./bin -sourcepath ./src ./src/LCPL*.java ./src/ro/pub/cs/lcpl/*.java

build-bonus: clean
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src_bonus/LCPLTreeBuilderBonus.g
	java -cp lib/antlrworks-1.4.jar org.antlr.Tool src_bonus/LCPLTreeCheckerBonus.g
	javac -cp "lib/*" -d ./bin -sourcepath ./src ./src_bonus/LCPL*.java ./src/ro/pub/cs/lcpl/*.java

clean:
	rm -rf ./bin/*
	rm -f ./src/LCPLTreeBuilder.tokens
	rm -f ./src/LCPLTreeBuilderLexer.java
	rm -f ./src/LCPLTreeBuilderParser.java
	rm -f ./src/LCPLTreeChecker.tokens
	rm -f ./src/LCPLTreeChecker.java
	rm -f ./src_bonus/LCPLTreeBuilderBonus.tokens
	rm -f ./src_bonus/LCPLTreeBuilderBonusLexer.java
	rm -f ./src_bonus/LCPLTreeBuilderBonusParser.java
	rm -f ./src_bonus/LCPLTreeCheckerBonus.tokens
	rm -f ./src_bonus/LCPLTreeCheckerBonus.java

test_vector: build-bonus
	java -cp "lib/*:./bin" LCPLParserBonus src_bonus/vector_test.lcpl vector_test.ast
