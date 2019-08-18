package jack.syntaxanalyzer;

import java.io.File;
import java.io.IOException;
import jack.syntaxanalyzer.constant.LexicalElements;
import jack.syntaxanalyzer.constant.TokenType;
import jack.util.FileReaderUtil;

public class JackTokenizer {
	
	/**
	 * 
	 */
	private final FileReaderUtil reader;
	
	/**
	 * 
	 */
	private String currentToken = null; 
	
	/**
	 * 
	 */
	private TokenType currentTokenType = null;
	
	/**
	 * 
	 */
	private int character;
	
	/**
	 * 
	 * @param filePath
	 * @throws IOException 
	 */
	public JackTokenizer(File file) throws IOException {
		reader = new FileReaderUtil(file);
		character = reader.read();
		skipSpaceNewLineAndTab();
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasMoreTokens() {
		return character != -1;
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public String advance() throws Exception {
		char c = (char)character;
			
		if(c == '"') {
			currentToken = readStringConstant();
			currentTokenType = TokenType.STRINGVAL;
		}
		else if(Character.isDigit(c)) {
			currentToken = readIntegerConstant();
			currentTokenType = TokenType.INTVAL;
		}
		else if(isSymbol(c)) {
			currentToken = toHTMLCharacter(c);
			currentTokenType = TokenType.SYMBOL;
			character = reader.read();
		}
		else {
			currentToken = readKeywordOrIdentifier();
			currentTokenType = isKeyword(currentToken) ? TokenType.KEYWORD 
					: TokenType.IDENTIFIER;
		}
		
		skipSpaceNewLineAndTab();
		
		return currentToken;
	}
	
	/**
	 * 
	 * @return
	 */
	public TokenType tokenType() {
		return currentTokenType;
	}
	
	/**
	 * 
	 */
	private void skipSpaceNewLineAndTab() {
		char c = (char)character;
		while(c == ' ' || isNewLine(c) || c == '\t') {
			character = reader.read();
			if(character == -1) {
				return;
			}
			c = (char)character;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String toHTMLCharacter(char c) {
		if(c == '<')
			return "&lt;";
		else if(c == '>')
			return "&gt;";
		else if(c == '&')
			return "&amp;";
		else
			return Character.toString(c);
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private boolean isSymbol(char c) {
		for(char symbol : LexicalElements.SYMBOLS) {
			if(c == symbol) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private boolean isNewLine(char c) {
		return c == '\n' || c == '\r';
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	private boolean isKeyword(String str) {
		for(String keyword : LexicalElements.KEYWORDS) {
			if(str.equals(keyword)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 * @throws EndOfFileException 
	 */
	private String readStringConstant() throws Exception {
		character = reader.read();
		
		if(character == -1) {
			throw new Exception("EOF");
		}
		
		char c = (char)character;
		StringBuilder builder = new StringBuilder();
		while(c != '"') {
			// TODO Skip the escape double quotes
			
			builder.append(c);
			character = reader.read();
			if(character == -1) {
				throw new Exception("EOF");
			}
			c = (char)character;
		}
		
		character = reader.read();
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private String readIntegerConstant() throws Exception {
		char c = (char)character;
		StringBuilder builder = new StringBuilder();
		while(Character.isDigit(c)) {
			builder.append(c);
			character = reader.read();
			if(character == -1) {
				throw new Exception("EOF");
			}
			c = (char)character;
		}
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	private String readKeywordOrIdentifier() throws Exception {
		char c = (char)character;
		StringBuilder builder = new StringBuilder();
		while(Character.isDigit(c) || Character.isLetter(c) || c == '_') {
			builder.append(c);
			character = reader.read();
			if(character == -1) {
				throw new Exception("EOF");
			}
			c = (char)character;
		}
		
		return builder.toString();
	}
}
