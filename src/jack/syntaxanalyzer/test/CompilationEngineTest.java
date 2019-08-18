package jack.syntaxanalyzer.test;

import java.io.File;

import jack.syntaxanalyzer.CompilationEngine1;

public class CompilationEngineTest {
	
	public static void main(String[] args) throws Exception {
		File input = new File("D:\\personal\\project\\nand2tetris\\SquareGame.jack");
		File output = new File("D:\\personal\\project\\nand2tetris\\SquareGame.xml");
		
		if(output.exists()) {
			output.delete();
		}
		
		output.createNewFile();
		
		CompilationEngine1 c = new CompilationEngine1(input, output);
		c.startCompile();
		
		System.out.println("Completed Successfully");
	}
}
