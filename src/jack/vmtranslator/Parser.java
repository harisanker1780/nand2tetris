package jack.vmtranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import jack.vmtranslator.constant.CommandType;
import jack.vmtranslator.exception.InvalidCommandException;

public class Parser {
	
	private static final String COMMAND_ARG_SEPARATOR = " ";
	
	private final Scanner sc;
	
	private boolean hasMoreCommands = true;
	private String nextCommandLine;
	
	private String command;
	private String arg1;
	private int arg2;
	
	/**
	 * 
	 * @param inputFileName
	 * @throws FileNotFoundException
	 */
	public Parser(String inputFileName) throws FileNotFoundException {
		sc = new  Scanner(new File(inputFileName));
		advance();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasMoreCommands() {
		return hasMoreCommands;
	}
	
	/**
	 * 
	 */
	public void advance() {
		nextCommandLine = getNextCommandline();
		if(nextCommandLine != null)
			advanceUtil(nextCommandLine);
		else
			hasMoreCommands = false;
	}
	
	/**
	 * 
	 * @return
	 * @throws InvalidCommandException
	 */
	public CommandType commandType() throws InvalidCommandException {
		switch(command) {
			case "add":
			case "sub":
			case "neg":
			case "eq":
			case "gt":
			case "lt":
			case "and":
			case "or":
			case "not":
				return CommandType.C_ARITHEMETIC;
			case "push":
				return CommandType.C_PUSH;
			case "pop":
				return CommandType.C_POP;
			default:
				throw new InvalidCommandException(command);
				
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String command() {
		return command;
	}
	
	/**
	 * 
	 * @return
	 */
	public String arg1() {
		return arg1;
	}
	
	/**
	 * 
	 * @return
	 */
	public int arg2() {
		return arg2;
	}
	
	/**
	 * 
	 * @param line
	 */
	private void advanceUtil(String line) {
		String tokens[] = line.split(COMMAND_ARG_SEPARATOR);
		if(tokens.length > 0) {
			command = tokens[0].trim();
		}
		
		if(tokens.length > 1) {
			arg1 = tokens[1].trim();
		}
		
		if(tokens.length > 2) {
			arg2 = Integer.parseInt(tokens[2]);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String getNextCommandline() {
		if(sc.hasNextLine()) {
			String line  = sc.nextLine();
			while(isCommentOrWhiteSpace(line)) {
				if(!sc.hasNextLine())
					break;
				line = sc.nextLine();
			}
			
			if(!isCommentOrWhiteSpace(line))
				return line;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	private static boolean isCommentOrWhiteSpace(String line) {
		
		if(line == null)
			return true;
		
		line = line.trim();
		
		if(line.length() == 0)
			return true;
		
		char ch = line.toCharArray()[0];
		return !(ch >= 'a' &&  ch <= 'z');
	}
}