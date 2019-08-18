package jack.syntaxanalyzer.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import jack.syntaxanalyzer.JackTokenizer;

public class JackTokenizerTest {

	public static void main(String[] args) throws Exception {
		File input = new File("D:\\personal\\project\\nand2tetris\\SquareGame.jack");
		JackTokenizer tokenizer = new JackTokenizer(input);
		File file = new File("D:\\personal\\project\\nand2tetris\\SquareGameT.xml");
		if(file.exists()) {
			file.delete();
		}
		
		file.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		bw.write("<tokens>");
		bw.newLine();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.advance();
			String line = "";
			switch(tokenizer.tokenType()) {
				case IDENTIFIER:
					line = "<identifier> " + token + " </identifier>";
				break;
				case INTVAL:
					line = "<integerConstant> " + token + " </integerConstant>";
				break;
				case KEYWORD:
					line = "<keyword> " + token + " </keyword>";
				break;
				case STRINGVAL:
					line = "<stringConstant> " + token + " </stringConstant>";
				break;
				case SYMBOL:
					line = "<symbol> " + token + " </symbol>";
				break;
				default:
					throw new Exception("Invalid Token Type");
			}
			
			bw.write(line);
			bw.newLine();
		}
		bw.write("</tokens>");
		bw.close();
		
		System.out.println("Completed Successfully");
	}
}
