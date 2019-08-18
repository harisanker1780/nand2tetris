package jack.syntaxanalyzer.constant;

public class LexicalElements {

	public static final String[] KEYWORDS = { 
			Keyword.CLASS,
			Keyword.CONSTRUCTOR,
			Keyword.FUNCTION,
			Keyword.METHOD,
			Keyword.FIELD,
			Keyword.STATIC,
			Keyword.VAR,
			Keyword.INT,
			Keyword.CHAR,
			Keyword.BOOLEAN,
			Keyword.VOID,
			Keyword.TRUE,
			Keyword.FALSE,
			Keyword.NULL,
			Keyword.THIS,
			Keyword.LET,
			Keyword.DO,
			Keyword.IF,
			Keyword.ELSE,
			Keyword.WHILE,
			Keyword.RETURN 
			};
	
	
	public static final char[] SYMBOLS = {'{', '}', '(', ')', '[', ']', '.',
			',', ';', '+', '-', '*', '/', '&', '|', '<', '>', '=', '~',  };
	
	public static final int INT_MIN = 0;
	
	public static final int INT_MAX = 32767;
}
