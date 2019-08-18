package jack.vmtranslator.exception;

public class InavlidArithmeticCommandException extends InvalidCommandException {

	public InavlidArithmeticCommandException(String invalidCommand) {
		super(invalidCommand);
	}
	
	public String toString() {
		return "Command: " + command + " is not a valid arithmetic command"; 
	}
}
