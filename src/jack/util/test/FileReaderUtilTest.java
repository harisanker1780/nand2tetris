package jack.util.test;

import java.io.File;
import java.io.IOException;

import jack.util.FileReaderUtil;

public class FileReaderUtilTest {

	public static void main(String[] args) throws IOException {
		File file = new File("D:\\personal\\project\\nand2tetris\\Main.jack");
		FileReaderUtil util = new FileReaderUtil(file);
		int i = util.read();
		while(i != -1) {
			char c = (char)i;
			System.out.print(c);
			i = util.read();
		}
	}
}
