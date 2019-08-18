package jack.syntaxanalyzer;

import java.io.File;
import java.io.IOException;

import jack.constant.MemorySegment;
import jack.constant.VariableKind;
import jack.symboltable.SymbolTable;
import jack.syntaxanalyzer.constant.Keyword;
import jack.syntaxanalyzer.constant.TokenType;
import jack.syntaxanalyzer.exception.CompilerEngineException;

public class CompilationEngine {
	
	/** */
	private String currentToken;
	
	private JackTokenizer tokenizer;
	
	private VMWriter vmWriter;
	
	private SymbolTable symbolTable;
	
	private int labelIndex;
	
	private String className;
	
	private String subroutineName;
	
	private int nArgumentVariables;
	
	private int nFieldVariables;
	
	private int nStaticVariables;
	
	/**
	 * 
	 * @param input
	 * @param output
	 * @throws Exception
	 */
	public CompilationEngine(File input, File output) throws Exception {
		tokenizer = new JackTokenizer(input);
		symbolTable = new SymbolTable();
		vmWriter = new VMWriter(output);
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void startCompile() throws Exception {
		currentToken = tokenizer.advance();
		compileClass();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClass() throws Exception {
		
		eat("class");
		
		// class name
		className = currentToken;
		advance();
		
		eat("{");
		
		while (currentToken.equals(Keyword.STATIC) || currentToken.equals(Keyword.FIELD)) {
			compileClassVarDec();
		}
		
		while (currentToken.equals(Keyword.CONSTRUCTOR) || currentToken.equals(Keyword.FUNCTION)
				|| currentToken.equals(Keyword.METHOD)) {
			compileClassSubroutineDec();
		}
		
		eat("}");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClassVarDec() throws Exception {
		nFieldVariables = 0;
		nStaticVariables = 0;
		
		VariableKind varKind;
		if(currentToken.equals(Keyword.STATIC)) {
			eat(Keyword.STATIC);
			varKind = VariableKind.STATIC;
			nStaticVariables++;
		}
		else {
			eat(Keyword.FIELD);
			varKind = VariableKind.FIELD;
			nFieldVariables++;
		}
		
		// type
		String varType = currentToken;
		//writeVarType();
		
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
			
			if(varKind == VariableKind.STATIC) {
				nStaticVariables++;
			}
			else {
				nFieldVariables++;
			}
			
			advance();
		}
		
		eat(";");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileClassSubroutineDec() throws Exception {
		symbolTable.startSubroutine();
		
		String type = currentToken.equals(Keyword.CONSTRUCTOR) ? Keyword.CONSTRUCTOR : currentToken.equals(Keyword.FUNCTION) 
				? Keyword.FUNCTION : Keyword.METHOD;	
		eat(type);
		
		// subroutineName
		advance();
		subroutineName = currentToken;
		advance();
		
		eat("(");
		compileParameterList();
		eat(")");
		
		writeSubroutineName(type);
		
		compileSubroutineBody();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileParameterList() throws Exception {
		
		nArgumentVariables = 0;
		
		if(!currentToken.equals(")")) {
			
			nArgumentVariables++;
			
			VariableKind varKind = VariableKind.ARGUMENT;
			String varType = currentToken;
			//writeVarType();
			
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
				//writeVarType();
				advance();
				varName = currentToken;
				symbolTable.define(varName, varType, varKind);
				writeIdentifier(varName, false, false, true);
				
				advance();
				
				nArgumentVariables++;
			}
		}
	}
	
	private void writeSubroutineName(String subroutineType) {
		if(subroutineType.equals(Keyword.FUNCTION)) {
			vmWriter.writeFunction(className + "." + subroutineName, nArgumentVariables);
		}
		else if(subroutineType.equals(Keyword.CONSTRUCTOR)) {
			vmWriter.writePush(MemorySegment.CONST, nFieldVariables);
			vmWriter.writeCall("Memory.alloc", 1);
			vmWriter.writePop(MemorySegment.POINTER, 0);
		}
		else if(subroutineType.equals(Keyword.METHOD)) {
			vmWriter.writePush(MemorySegment.ARG, 0);
			vmWriter.writePop(MemorySegment.POINTER, 0);
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileSubroutineBody() throws Exception {
		eat("{");
		while(currentToken.equals(Keyword.VAR)) {
			compileVarDec();
		}
		
		compileStatements();
		eat("}");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileVarDec() throws Exception {
		eat(Keyword.VAR);
		
		VariableKind varKind = VariableKind.LOCAL;
		
		// type varName
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
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileStatements() throws Exception {
		while(currentToken.equals(Keyword.LET) || 
				currentToken.equals(Keyword.IF) ||
				currentToken.equals(Keyword.WHILE) ||
				currentToken.equals(Keyword.DO) ||
				currentToken.equals(Keyword.RETURN)) {
			if(currentToken.equals(Keyword.LET)) {
				compileLet();
			}
			else if(currentToken.equals(Keyword.IF)) {
				compileIf();
			}
			else if(currentToken.equals(Keyword.WHILE)) {
				compileWhile();
			}
			else if(currentToken.equals(Keyword.DO)) {
				compileDo();
			}
			else if(currentToken.equals(Keyword.RETURN)) {
				compileReturn();
			}
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileLet() throws Exception {
		eat(Keyword.LET);
		
		String token1 = currentToken;
		while(!token1.equals("=")) {
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
				writeIdentifier(token1, false, false, false);
			}
			
			token1 = currentToken;
		}
		
		eat("=");
		compileExpression();
		eat(";");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileIf() throws Exception {
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
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileWhile() throws Exception {
		eat("while");
		eat("(");
		compileExpression();
		eat(")");
		eat("{");
		compileStatements();
		eat("}");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileDo() throws Exception {
		eat("do");
		
		String token1 = currentToken;
		while(!token1.equals(";")) {
			advance();
			String token2 = currentToken;
			
			//writeIdentifierTwoStage(token1, token2);
			
			token1 = currentToken;
		}
		
		eat(";");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileReturn() throws Exception {
		eat("return");
		
		if(!currentToken.equals(";")) {
			compileExpression();
		}
		
		eat(";");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileExpression() throws Exception {
		compileTerm();
		
		while(isOperator(currentToken)) {
			writeCurrentToken();
			advance();
			
			compileTerm();
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileTerm() throws Exception {
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
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private void compileExpressionList() throws Exception {
		if(!currentToken.equals(")")) {
			compileExpression();
			
			//advance();
			while(currentToken.equals(",")) {
				writeCurrentToken();
				advance();
				compileExpression();
			}
		}
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
	/*private void writeIdentifierTwoStage(String firstToken, String secondToken) throws Exception {
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
	}*/
	
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
				break;
				case KEYWORD:
				break;
				case STRINGVAL:
				break;
				case SYMBOL:
				break;
				default:
					throw new Exception("Invalid Token Type");
			}
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
		}
		else if(canBeClass || canBeSubroutine) {
			String varKindStr = (canBeClass) ? "class" : (canBeSubroutine) ? "subroutine" : null;
		}
		else{ 
			throw new Exception("Identifier: "+ identifierName +" not found");
		}
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
	
	private String newLabel() {
		return "label-" + labelIndex++;
	}
}
