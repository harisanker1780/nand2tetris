package jack.vmtranslator;

import jack.vmtranslator.constant.CommandType;

public class VMTranslator {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "D:\\personal\\project\\nand2tetris\\javaworkspace\\NandToTetris\\src\\StaticTest.vm";
		
		System.out.println(fileName);
		try {
			Parser parser = new Parser(fileName);
			String outputFileName = getFileNameWithoutExtension(fileName);
			CodeWriter codeWriter = new CodeWriter(outputFileName);
			
			codeWriter.writeSPAndMemorySegments();
			
			while(parser.hasMoreCommands()) {
				CommandType commandType = parser.commandType();
				String command = parser.command();
				String segment = parser.arg1();
				int index = parser.arg2();
				
				if(commandType == CommandType.C_ARITHEMETIC) {
					codeWriter.writeArithmetic(command);
				}
				else if(commandType == CommandType.C_PUSH || commandType == CommandType.C_POP) {
					codeWriter.writePushPop(commandType, segment, index);
				}
				
				parser.advance();
			}
			
			codeWriter.writeEndLoop();
			codeWriter.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private static String getFileNameWithoutExtension(String fileName) {
		String response = fileName;
		int pos = fileName.lastIndexOf(".");
		if(pos > 0) {
			response = fileName.substring(0, pos);
		}
		return response;
	}
}