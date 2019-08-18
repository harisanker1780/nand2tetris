package jack.vmtranslator.exception;

public class InvalidSegmentException extends VMTranslatorExcpetion {
	
	protected final String segment;
	
	public InvalidSegmentException(String segment) {
		this.segment = segment;
	}
	
	public String toString() {
		return "Segment: " + segment + " is not a valid segment"; 
	}
}
