package jack.vmtranslator.exception;

public class InvalidCommandException extends VMTranslatorExcpetion {
	
	protected final String command;
	
	public InvalidCommandException(String command) {
		this.command = command;
	}
	
	public String toString() {
		return "Command: " + command + " is not a valid command"; 
	}
}
