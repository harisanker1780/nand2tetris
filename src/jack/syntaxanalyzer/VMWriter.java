package jack.syntaxanalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import jack.constant.ArithmeticLogicalCommand;
import jack.constant.MemorySegment;

public class VMWriter {
	
	/**
	 * 
	 */
	private PrintWriter printWriter;
	
	/**
	 * 
	 */
	private static HashMap<ArithmeticLogicalCommand, String> commandStringMap = new HashMap<ArithmeticLogicalCommand, String>();
	
	/**
	 * 
	 */
	private static HashMap<MemorySegment, String> segmentStringMap = new HashMap<MemorySegment, String>();
	
	static {
		commandStringMap.put(ArithmeticLogicalCommand.ADD, "add");
		commandStringMap.put(ArithmeticLogicalCommand.SUB, "sub");
		commandStringMap.put(ArithmeticLogicalCommand.EQ, "eq");
		commandStringMap.put(ArithmeticLogicalCommand.GT, "gt");
		commandStringMap.put(ArithmeticLogicalCommand.LT, "lt");
		commandStringMap.put(ArithmeticLogicalCommand.NEG, "neg");
		commandStringMap.put(ArithmeticLogicalCommand.NOT, "not");
		commandStringMap.put(ArithmeticLogicalCommand.OR, "or");
		commandStringMap.put(ArithmeticLogicalCommand.SUB, "sub");
		
		segmentStringMap.put(MemorySegment.ARG, "argument");
		segmentStringMap.put(MemorySegment.CONST, "constant");
		segmentStringMap.put(MemorySegment.LOCAL, "local");
		segmentStringMap.put(MemorySegment.POINTER, "pointer");
		segmentStringMap.put(MemorySegment.STATIC, "static");
		segmentStringMap.put(MemorySegment.TEMP, "temp");
		segmentStringMap.put(MemorySegment.THAT, "that");
		segmentStringMap.put(MemorySegment.THIS, "this");
	}
	
	/**
	 * Create a new output .vm file and prepares it for writing
	 * 
	 * @param output
	 * @throws FileNotFoundException
	 */
	public VMWriter(File output) throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(output);
		printWriter = new PrintWriter(fos);
	}
	
	/**
	 * Writes a VM push command
	 * 
	 * @param segment
	 * @param index
	 */
	public void writePush(MemorySegment segment, int index) {
		write("push", segmentStringMap.get(segment), String.valueOf(index));
	}
	
	/**
	 * Writes a VM pop command
	 * 
	 * @param segment
	 * @param index
	 */
	public void writePop(MemorySegment segment, int index) {
		write("pop", segmentStringMap.get(segment), String.valueOf(index));
	}
	
	/**
	 * Writes a VM arithmetic-logical command
	 * 
	 * @param command
	 */
	public void writeArithmetic(ArithmeticLogicalCommand command) {
		write(commandStringMap.get(command), "", "");
	}
	
	/**
	 * Writes a VM label command
	 * 
	 * @param label
	 */
	public void writeLabel(String label) {
		write("label", label, ""	);
	}
	
	/**
	 * Writes a VM goto command
	 * 
	 * @param label
	 */
	public void writeGoto(String label) {
		write("goto", label, "");
	}

	/**
	 * Writes a VM if-goto command
	 * 
	 * @param label
	 */
	public void writeIf(String label) {
		write("if-goto", label, "");
	}
	
	/**
	 * Writes a VM call command
	 * 
	 * @param name
	 * @param nArgs
	 */
	public void writeCall(String name, int nArgs) {
		write("call", name, String.valueOf(nArgs));
	}
	
	/**
	 * Writes a VM function command
	 * 
	 * @param name
	 * @param nLocals
	 */
	public void writeFunction(String name, int nLocals) {
		write("function", name, String.valueOf(nLocals));
	}
	
	/**
	 * Writes a VM return command
	 */
	public void writeReturn() {
		write("return", "", "");
	}
	
	/**
	 * Closes the output file 
	 */
	public void close() {
		printWriter.close();
	}
	
	/**
	 * 
	 */
	private void write(String command, String arg1, String arg2) {
		printWriter.print(command + " " + arg1 + " " + arg2 + "\n");
	}
}
