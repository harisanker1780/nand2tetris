package jack.vmtranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jack.vmtranslator.constant.CommandType;
import jack.vmtranslator.exception.InavlidArithmeticCommandException;
import jack.vmtranslator.exception.InvalidSegmentException;

public class CodeWriter {
	
	private static final String LINE_SEPARATOR = System.lineSeparator();
	
	private final FileWriter writer;
	
	private int commandNumber = 0;
	
	private final String fileName;
	
	/**
	 * 
	 * @param outputFileName
	 * @throws IOException
	 */
	public CodeWriter(String outputFileName) throws IOException {
		this.fileName = outputFileName.substring(outputFileName.lastIndexOf("\\") + 1, outputFileName.length());
		
		File file = new File(outputFileName + ".asm");
		if(file.exists())
			file.delete();
		file.createNewFile();
		
		writer = new FileWriter(file);
	}
	
	/**
	 * 
	 * @param command
	 * @throws InavlidArithmeticCommandException
	 * @throws IOException
	 */
	public void writeArithmetic(String command) throws InavlidArithmeticCommandException, IOException {
		commandNumber++;
		switch(command) {
			case "add":
				writeAdd();
			break;
			case "sub":
				writeSubtract();
			break;
			case "neg":
				writeNeg();
			break;
			case "eq":
				writeEq();
			break;
			case "gt":
				writeGt();
			break;
			case "lt":
				writeLt();
			break;
			case "and":
				writeAnd();
			break;
			case "or":
				writeOr();
			break;
			case "not":
				writeNot();
			break;
			default:
				throw new InavlidArithmeticCommandException(command);
		}
	}
	
	/**
	 * 
	 * @param command
	 * @param segment
	 * @param index
	 * @throws IOException
	 * @throws InvalidSegmentException
	 */
	public void writePushPop(CommandType command, String segment, int index) throws IOException,
					InvalidSegmentException {
		commandNumber++;
		switch(command) {
			case C_PUSH:
				switch(segment) {
					case "constant":
						writePushConstant(index);
					break;
					case "local":
						writePushSegment(index, "LCL");
					break;
					case "argument":
						writePushSegment(index, "ARG");
					break;
					case "this":
						writePushSegment(index, "THIS");
					break;
					case "that":
						writePushSegment(index, "THAT");
					break;
					case "temp":
						writePushTemp(index);
					break;
					case "pointer":
						writePushPointer(index);
					break;
					case "static":
						writePushStatic(index);
					break;
					default:
						throw new InvalidSegmentException(segment);
				}
			break;
			case C_POP:
				switch(segment) {
					case "local":
						writePopSegment(index, "LCL");
					break;
					case "argument":
						writePopSegment(index, "ARG");
					break;
					case "this":
						writePopSegment(index, "THIS");
					break;
					case "that":
						writePopSegment(index, "THAT");
					break;
					case "temp":
						writePopTemp(index);
					break;
					case "pointer":
						writePopPointer(index);
					break;
					case "static":
						writePopStatic(index);
					break;
					default:
						throw new InvalidSegmentException(segment);
				}
			break;
		}
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		writer.close();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void writeSPAndMemorySegments() throws IOException {
		writeNewLine();
		writeAndNewLine("// Initialize Stack Poiner");
		writeAndNewLine("@R0");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = D");
		
		writeNewLine();
		writeAndNewLine("// Initialize LCL");
		writeAndNewLine("@R1");
		writeAndNewLine("D = M");
		writeAndNewLine("@LCL");
		writeAndNewLine("M = D");
		
		writeNewLine();
		writeAndNewLine("// Initialize ARG");
		writeAndNewLine("@R2");
		writeAndNewLine("D = M");
		writeAndNewLine("@ARG");
		writeAndNewLine("M = D");
		
		writeNewLine();
		writeAndNewLine("// Initialize THIS");
		writeAndNewLine("@R3");
		writeAndNewLine("D = M");
		writeAndNewLine("@THIS");
		writeAndNewLine("M = D");
		
		writeNewLine();
		writeAndNewLine("// Initialize THAT");
		writeAndNewLine("@R4");
		writeAndNewLine("D = M");
		writeAndNewLine("@THAT");
		writeAndNewLine("M = D");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public void writeEndLoop() throws IOException {
		writeNewLine();
		writeAndNewLine("// End");
		writeAndNewLine("(END)");
		writeAndNewLine("@END");
		writeAndNewLine("0;JMP");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeAdd() throws IOException {
		writeNewLine();
		writeAndNewLine("// add");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D + M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeSubtract() throws IOException {
		writeNewLine();
		writeAndNewLine("// subtract");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M - D");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeNeg() throws IOException {
		writeNewLine();
		writeAndNewLine("// neg");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("D = D - M");
		writeAndNewLine("D = D - M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeEq() throws IOException {
		writeNewLine();
		writeAndNewLine("// eq");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D - M");
		writeAndNewLine("@EQUAL"+commandNumber);
		writeAndNewLine("D;JEQ");
		writeAndNewLine("@NOTEQUAL"+commandNumber);
		writeAndNewLine("D;JNE");
		writeAndNewLine("(EQUAL"+commandNumber+")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(NOTEQUAL"+commandNumber+")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M  + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeGt() throws IOException {
		writeNewLine();
		writeAndNewLine("// gt");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D - M");
		writeAndNewLine("@GRATER" + commandNumber);
		writeAndNewLine("D;JGT");
		writeAndNewLine("@NOTGRATER" + commandNumber);
		writeAndNewLine("D;JLE");
		writeAndNewLine("(GRATER"+commandNumber+")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(NOTGRATER"+commandNumber+")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeLt() throws IOException {
		writeNewLine();
		writeAndNewLine("// lt");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D - M");
		writeAndNewLine("@LESSER" + commandNumber);
		writeAndNewLine("D;JLT");
		writeAndNewLine("@NOTLESSER" + commandNumber);
		writeAndNewLine("D;JGE");
		writeAndNewLine("(LESSER"+commandNumber+")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(NOTLESSER"+commandNumber+")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeAnd() throws IOException {
		writeNewLine();
		writeAndNewLine("// and");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D + M");
		writeAndNewLine("D = D - 1");
		writeAndNewLine("@ONE" + commandNumber);
		writeAndNewLine("D;JGT");
		writeAndNewLine("@ZERO" + commandNumber);
		writeAndNewLine("D; JLE");
		writeAndNewLine("(ONE" +commandNumber+ ")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(ZERO" +commandNumber+ ")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeOr() throws IOException {
		writeNewLine();
		writeAndNewLine("// or");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = D + M");
		writeAndNewLine("@ONE" + commandNumber);
		writeAndNewLine("D;JGT");
		writeAndNewLine("@ZERO" + commandNumber);
		writeAndNewLine("D;JLE");
		writeAndNewLine("(ONE"+commandNumber+ ")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(ZERO"+commandNumber+")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine(" M = M + 1");
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeNot() throws IOException {
		writeNewLine();
		writeAndNewLine("// not");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@ONE"+commandNumber);
		writeAndNewLine("D;JEQ");
		writeAndNewLine("@ZERO"+commandNumber);
		writeAndNewLine("D;JGT");
		writeAndNewLine("(ONE"+commandNumber+")");
		writeAndNewLine("D = 1");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(ZERO"+commandNumber+")");
		writeAndNewLine("D = 0");
		writeAndNewLine("@CONTINUE"+commandNumber+"");
		writeAndNewLine("0;JMP");
		writeAndNewLine("(CONTINUE"+commandNumber+")");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine(" M = M + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePushConstant(int index) throws IOException {
		writeNewLine();
		writeAndNewLine("// push constant " + index);
		writeAndNewLine("@"+index);
		writeAndNewLine("D = A");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M  + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @param segment
	 * @throws IOException
	 */
	private void writePushSegment(int index, String segment) throws IOException {
		writeNewLine();
		writeAndNewLine("// push "+ segment + " " + index);
		writeAndNewLine("@"+index);
		writeAndNewLine("D = A");
		writeAndNewLine("@"+segment);
		writeAndNewLine("D = D + M");
		writeAndNewLine("@addr");
		writeAndNewLine("M = D");
		writeAndNewLine("@addr");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M  + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @param segment
	 * @throws IOException
	 */
	private void writePopSegment(int index, String segment) throws IOException {
		writeNewLine();
		writeAndNewLine("// pop "+ segment + " " + index);
		writeAndNewLine("@"+index);
		writeAndNewLine("D = A");
		writeAndNewLine("@"+segment);
		writeAndNewLine("D = D + M");
		writeAndNewLine("@addr");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@addr");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePushTemp(int index) throws IOException {
		writeNewLine();
		writeAndNewLine("// push temp " + index);
		writeAndNewLine("@5");
		writeAndNewLine("D = A");
		writeAndNewLine("@"+index);
		writeAndNewLine("D = D + A");
		writeAndNewLine("@addr");
		writeAndNewLine("M = D");
		writeAndNewLine("@addr");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePopTemp(int index) throws IOException {
		writeNewLine();
		writeAndNewLine("// pop temp " + index);
		writeAndNewLine("@5");
		writeAndNewLine("D = A");
		writeAndNewLine("@"+index);
		writeAndNewLine("D = D + A");
		writeAndNewLine("@addr");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@addr");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePushPointer(int index) throws IOException {
		String segment = (index == 0) ? "THIS" : "THAT";
		writeNewLine();
		writeAndNewLine("// push pointer " + index);
		writeAndNewLine("@"+segment);
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePopPointer(int index) throws IOException {
		String segment = (index == 0) ? "THIS" : "THAT";
		writeNewLine();
		writeAndNewLine("// pop pointer " + index);
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@"+segment);
		writeAndNewLine("M = D");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePushStatic(int index) throws IOException {
		writeNewLine();
		writeAndNewLine("// push static " + index);
		writeAndNewLine("@16");
		writeAndNewLine("D = A");
		writeAndNewLine("@" + index);
		writeAndNewLine("D = D + A");
		writeAndNewLine("@" + fileName + "." + index);
		writeAndNewLine("M = D");
		writeAndNewLine("@" + fileName + "." + index);
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M + 1");
	}
	
	/**
	 * 
	 * @param index
	 * @throws IOException
	 */
	private void writePopStatic(int index) throws IOException {
		writeNewLine();
		writeAndNewLine("// pop static " + index);
		writeAndNewLine("@16");
		writeAndNewLine("D = A");
		writeAndNewLine("@" + index);
		writeAndNewLine("D = D + A");
		writeAndNewLine("@" + fileName + "." + index);
		writeAndNewLine("M = D");
		writeAndNewLine("@SP");
		writeAndNewLine("M = M - 1");
		writeAndNewLine("@SP");
		writeAndNewLine("A = M");
		writeAndNewLine("D = M");
		writeAndNewLine("@" + fileName + "." + index);
		writeAndNewLine("A = M");
		writeAndNewLine("M = D");
	}
	
	/**
	 * 
	 * @param text
	 * @throws IOException
	 */
	private void writeAndNewLine(String text) throws IOException {
		writer.write(text);
		writeNewLine();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void writeNewLine() throws IOException {
		writer.write(LINE_SEPARATOR);
	}
	
}
