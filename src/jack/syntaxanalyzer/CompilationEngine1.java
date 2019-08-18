package jack.syntaxanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jack.constant.VariableKind;
import jack.symboltable.SymbolTable;
import jack.syntaxanalyzer.constant.TokenType;
import jack.syntaxanalyzer.exception.CompilerEngineException;

public class CompilationEngine1 {
	
	/** */
	private static final String CLASS = "class";
	
	private static final String CLASS_VAR_DEC = "classVarDec";
	
	private static final String SUBROUTINE_DEC = "subroutineDec";
	
	private static final String PARAMETER_LIST = "parameterList";
	
	private static final String SUBROUTINE_BODY = "subroutineBody";
	
	private static final String VAR_DEC = "varDec";
	
	private static final String STATEMENTS = "statements";
	
	private static final String LET_STATEMENT = "letStatement";
	
	private static final String IF_STATEMENT = "ifStatement";
	
	private static final String WHILE_STATEMENT = "whileStatement";
	
	private static final String DO_STATEMENT = "doStatement";
	
	private static final String RETURN_STATEMENT = "returnStatement";
	
	private static final String EXPRESSION = "expression";
	
	private static final String TERM = "term";
	
	private static final String EXPRESSION_LIST = "expressionList";
	
	private static final String IDENTIFIER = "identifier";
	
	private static final String INTEGER_CONSTANT = "integerConstant";
	
	private static final String KEYWORD = "keyword";
	
	private static final String STRING_CONSTANT = "stringConstant";
	
	private static final String SYMBOL = "symbol";
	
	private String currentToken;
	
	private JackTokenizer tokenizer;
	
	private BufferedWriter bw;
	
	private static int space = 0;
	
	private SymbolTable symbolTable;
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	public CompilationEngine1(File input, File output) throws Exception {
		FileOutputStream fos = new FileOutputStream(output);
		bw = new BufferedWriter(new OutputStreamWriter(fos));
		tokenizer = new JackTokenizer(input);
		symbolTable = new SymbolTable();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void startCompile() throws Exception {
		currentToken = tokenizer.advance();
		compileClass();
		
		bw.close();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClass() throws Exception {
		writeStartTag(CLASS);
		
		eat("class");
		
		// class name
		writeClassName(currentToken);
		advance();
		
		eat("{");
		
		while (currentToken.equals("static") || currentToken.equals("field")) {
			compileClassVarDec();
		}
		
		while (currentToken.equals("constructor") || currentToken.equals("function")
				|| currentToken.equals("method")) {
			compileClassSubroutineDec();
		}
		
		eat("}");
		
		writeEndTag(CLASS);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClassVarDec() throws Exception {
		writeStartTag(CLASS_VAR_DEC);
		
		VariableKind varKind;
		if(currentToken.equals("static")) {
			eat("static");
			varKind = VariableKind.STATIC;
		}
		else {
			eat("field");
			varKind = VariableKind.FIELD;
		}
		
		// type
		String varType = currentToken;
		writeVarType();
		
		// varName
		advance();
		String varName = currentToken;
		symbolTable.define(varName, varType, varKind);
		
		writeIdentifier(varName, false, false, true);
		
		advance();
		while(currentToken.equals(",")) {
			writeCurrentToken();
			
			// varName
			advance();
			varName = currentToken;
			symbolTable.define(varName, varType, varKind);
			writeIdentifier(varName, false, false, true);
			
			advance();
		}
		
		eat(";");
		
		writeEndTag(CLASS_VAR_DEC);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClassSubroutineDec() throws Exception {
		symbolTable.startSubroutine();
		
		writeStartTag(SUBROUTINE_DEC);
		
		if(currentToken.equals("constructor")) {
			eat("constructor");
		}
		else if(currentToken.equals("function")) {
			eat("function");
		}
		else {
			eat("method");
		}
		
		// ('void' | type)
		writeVarType();
		
		// subroutineName
		advance();
		writeSubroutineName(currentToken);
		
		advance();
		
		eat("(");
		compileParameterList();
		eat(")");
		
		compileSubroutineBody();
		
		writeEndTag(SUBROUTINE_DEC);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileParameterList() throws Exception {
		writeStartTag(PARAMETER_LIST);
		
		if(!currentToken.equals(")")) {
			
			VariableKind varKind = VariableKind.ARGUMENT;
			String varType = currentToken;
			writeVarType();
			
			// (type varName)
			advance();
			String varName = currentToken;
			symbolTable.define(varName, varType, varKind);
			writeIdentifier(varName, false, false, true);
			
			advance();
			while(currentToken.equals(",")) {
				writeCurrentToken();
				
				// (type varName)
				advance();
				varType = currentToken;
				writeVarType();
				advance();
				varName = currentToken;
				symbolTable.define(varName, varType, varKind);
				writeIdentifier(varName, false, false, true);
				
				advance();
			}
		}
		
		writeEndTag(PARAMETER_LIST);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileSubroutineBody() throws Exception {
		writeStartTag(SUBROUTINE_BODY);
		
		eat("{");
		while(currentToken.equals("var")) {
			compileVarDec();
		}
		
		compileStatements();
		eat("}");
		
		writeEndTag(SUBROUTINE_BODY);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileVarDec() throws Exception {
		writeStartTag(VAR_DEC);
		
		eat("var");
		
		VariableKind varKind = VariableKind.LOCAL;
		
		// type varName
		writeVarType();
		String varType = currentToken;
		advance();
		String varName = currentToken;
		symbolTable.define(varName, varType, varKind);
		writeIdentifier(varName, false, false, true);
		
		advance();
		while(currentToken.equals(",")) {
			writeCurrentToken();
			
			// varName
			advance();
			varName = currentToken;
			symbolTable.define(varName, varType, varKind);
			writeIdentifier(varName, false, false, true);
			
			advance();
		}
		
		eat(";");
		
		writeEndTag(VAR_DEC);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileStatements() throws Exception {
		writeStartTag(STATEMENTS);
		
		while(currentToken.equals("let") || 
				currentToken.equals("if") ||
				currentToken.equals("while") ||
				currentToken.equals("do") ||
				currentToken.equals("return")) {
			if(currentToken.equals("let")) {
				compileLet();
			}
			else if(currentToken.equals("if")) {
				compileIf();
			}
			else if(currentToken.equals("while")) {
				compileWhile();
			}
			else if(currentToken.equals("do")) {
				compileDo();
			}
			else if(currentToken.equals("return")) {
				compileReturn();
			}
		}
		writeEndTag(STATEMENTS);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileLet() throws Exception {
		writeStartTag(LET_STATEMENT);
		
		eat("let");
		
		String token1 = currentToken;
		while(!token1.equals("=")) {
			advance();
			String token2 = currentToken;
			
			writeIdentifierTwoStage(token1, token2);
			
			token1 = currentToken;
		}
		
		eat("=");
		compileExpression();
		eat(";");
		
		writeEndTag(LET_STATEMENT);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileIf() throws Exception {
		writeStartTag(IF_STATEMENT);
		
		eat("if");
		eat("(");
		compileExpression();
		eat(")");
		
		eat("{");
		compileStatements();
		eat("}");
		
		if(currentToken.equals("else")) {
			eat("else");
			eat("{");
			compileStatements();
			eat("}");
		}
		
		writeEndTag(IF_STATEMENT);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileWhile() throws Exception {
		writeStartTag(WHILE_STATEMENT);
		
		eat("while");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
		
		writeEndTag(WHILE_STATEMENT);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileDo() throws Exception {
		writeStartTag(DO_STATEMENT);
		
		eat("do");
		
		String token1 = currentToken;
		while(!token1.equals(";")) {
			advance();
			String token2 = currentToken;
			
			writeIdentifierTwoStage(token1, token2);
			
			token1 = currentToken;
		}
		
		eat(";");
		
		writeEndTag(DO_STATEMENT);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileReturn() throws Exception {
		writeStartTag(RETURN_STATEMENT);
		
		eat("return");
		
		if(!currentToken.equals(";")) {
			compileExpression();
		}
		
		eat(";");
		
		writeEndTag(RETURN_STATEMENT);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileExpression() throws Exception {
		writeStartTag(EXPRESSION);
		
		compileTerm();
		
		while(isOperator(currentToken)) {
			writeCurrentToken();
			advance();
			
			compileTerm();
		}
		
		writeEndTag(EXPRESSION);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileTerm() throws Exception {
		writeStartTag(TERM);
		
		// Unary OP
		if(currentToken.equals("-") || currentToken.equals("~")) {
			writeCurrentToken();
			advance();
			compileTerm();
		}
		
		// Expression
		else if(currentToken.equals("(")) {
			eat("(");
			compileExpression();
			eat(")");
		}
		else {
			
			String token1 = currentToken;
			while(!token1.equals(";") && !isOperator(token1) && !token1.equals(")") && !token1.equals(",")) {
				TokenType type = tokenizer.tokenType();
				
				advance();
				String token2 = currentToken;
				
				if(token2.equals(".")) {
					writeIdentifier(token1, true, false, false);
					eat(".");
				}
				else if(token2.equals("(")) {
					writeIdentifier(token1, false, true, false);
					eat("(");
					compileExpressionList();
					eat(")");
				}
				else if(token2.equals("[")) {
					writeIdentifier(token1, true, false, false);
					eat("[");
					compileExpressionList();
					eat("]");
				}
				else {
					writeToken(token1, type);
				}
				
				token1 = currentToken;
			}
		}
		
		writeEndTag(TERM);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileExpressionList() throws Exception {
		writeStartTag(EXPRESSION_LIST);
		
		if(!currentToken.equals(")")) {
			compileExpression();
			
			//advance();
			while(currentToken.equals(",")) {
				writeCurrentToken();
				advance();
				compileExpression();
			}
		}
		
		writeEndTag(EXPRESSION_LIST);
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	private boolean isOperator(String token) {
		return token.equals("+") || token.equals("-") 
			|| token.equals("*") || token.equals("/")
			|| token.equals("&amp;") || token.equals("|")
			|| token.equals("&lt;") || token.equals("&gt;")
			|| token.equals("=");
	}
	
	/**
	 * 
	 * @param token
	 * @throws Exception
	 */
	private void eat(String token) throws Exception {
		if(!token.equals(currentToken)) {
			throw new CompilerEngineException("Invalid token: " + currentToken + ", expected: " + token);
		}
		else {
			writeCurrentToken();
			advance();
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void advance() throws Exception {
		currentToken = tokenizer.advance();
	}
	
	/**
	 * 
	 * @param firstToken
	 * @param secondToken
	 * @throws Exception
	 */
	private void writeIdentifierTwoStage(String firstToken, String secondToken) throws Exception {
		if(secondToken.equals(".")) {
			writeIdentifier(firstToken, true, false, false);
			eat(".");
		}
		else if(secondToken.equals("(")) {
			writeIdentifier(firstToken, false, true, false);
			eat("(");
			compileExpressionList();
			eat(")");
		}
		else if(secondToken.equals("[")) {
			writeIdentifier(firstToken, true, false, false);
			eat("[");
			compileExpressionList();
			eat("]");
		}
		else {
			writeIdentifier(firstToken, false, false, false);
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void writeCurrentToken() throws Exception {
		writeToken(currentToken, tokenizer.tokenType());
	}
	
	private void writeToken(String token, TokenType type) throws Exception {
		if(type == TokenType.IDENTIFIER) {
			writeIdentifier(token, false, false, false);
		}
		else {
			String line = "";
			switch(type) {
				case INTVAL:
					line = "<" + INTEGER_CONSTANT + "> " + token + " </" + INTEGER_CONSTANT + ">";
				break;
				case KEYWORD:
					line = "<" + KEYWORD + "> " + token + " </" + KEYWORD + ">";
				break;
				case STRINGVAL:
					line = "<" + STRING_CONSTANT + "> " + token + " </" + STRING_CONSTANT + ">";
				break;
				case SYMBOL:
					line = "<" + SYMBOL + "> " + token + " </" + SYMBOL + ">";
				break;
				default:
					throw new Exception("Invalid Token Type");
			}
			
			write(line);
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeIdentifier(String identifierName, boolean canBeClass, boolean canBeSubroutine, boolean isDefined) throws Exception {
		VariableKind varKind = symbolTable.KindOf(identifierName);
		if(varKind != null) {
			String varType = symbolTable.TypeOf(identifierName);
			int index = symbolTable.IndexOf(identifierName);
			String varKindStr = toVariableKindName(varKind);
			String use = isDefined ? "defined" : "used";
			
			writeStartTag(IDENTIFIER);
			write("<name>" + identifierName + "</name>");
			write("<kind>" + varKindStr + "</kind>");
			write("<type>" + varType + "</type>");
			write("<index>" + index + "</index>");
			write("<use> " + use + " </use>");
			writeEndTag(IDENTIFIER);
		}
		else if(canBeClass || canBeSubroutine) {
			String varKindStr = (canBeClass) ? "class" : (canBeSubroutine) ? "subroutine" : null;
			writeStartTag(IDENTIFIER);
			write("<name>" + identifierName + "</name>");
			write("<kind>" + varKindStr + "</kind>");
			writeEndTag(IDENTIFIER);
		}
		else{ 
			throw new Exception("Identifier: "+ identifierName +" not found");
		}
	}
	
	/**
	 * 
	 * @param className
	 * @throws IOException
	 */
	private void writeClassName(String className) throws IOException {
		writeStartTag("identifier");
		write("<name> " + className + " </name>");
		write("<kind> class </class>");
		writeEndTag("identifier");
	}
	
	/**
	 * 
	 * @param subroutineName
	 * @throws IOException
	 */
	private void writeSubroutineName(String subroutineName) throws IOException {
		writeStartTag("identifier");
		write("<name> " + subroutineName + " </name>");
		write("<kind> subroutine </class>");
		writeEndTag("identifier");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeVarType() throws Exception {
		TokenType tokenType = tokenizer.tokenType();
		if(tokenType == TokenType.IDENTIFIER) {
			writeStartTag("identifier");
			write("<name> " + currentToken + " </name>");
			write("<kind> class </class>");
			writeEndTag("identifier");
		} else if(tokenType == TokenType.KEYWORD) {
			write("<" + KEYWORD + "> " + currentToken + " </" + KEYWORD + ">");
		} else {
			throw new Exception("Invalid variable type");
		}
	}
	
	/**
	 * 
	 * @param tag
	 * @throws IOException
	 */
	private void writeStartTag(String tag) throws IOException {
		String element = "<" + tag + ">";
		write(element);
		space++;
	}
	
	/**
	 * 
	 * @param tag
	 * @throws IOException
	 */
	private void writeEndTag(String tag) throws IOException {
		space--;
		String element = "</" + tag + ">";
		write(element);
	}
	
	/**
	 * 
	 * @param text
	 * @throws IOException
	 */
	private void write(String text) throws IOException {
		String spaceStr = "";
		for(int i = 0; i < space; i++) {
			spaceStr += "  ";
		}
		
		bw.write(spaceStr + text); 
		bw.newLine();
		
		// System.out.println(spaceStr + text);
	}
	
	/**
	 * 
	 * @param kind
	 * @return
	 */
	private String toVariableKindName(VariableKind kind) {
		switch(kind) {
			case ARGUMENT:
				return "argument";
			case FIELD:
				return "field";
			case STATIC:
				return "static";
			case LOCAL:
				return "var";
			default:
				return null;
		}
	}
}
