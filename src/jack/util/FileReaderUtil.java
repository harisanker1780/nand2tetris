package jack.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderUtil {
	
	private final List<Character> array;
	
	private int index = 0;
	
	private int length = 0;
	
	public FileReaderUtil(File file) throws IOException {
		
		array = new ArrayList<Character>();
		
		FileReader reader = new FileReader(file);
		
		boolean isLineComment = false;
		boolean isBlockComment = false;
		int i = reader.read();
		char prev = ' ';
		while(i != -1) {
			char current = (char) i;
			
			i = reader.read();
			
			if(isNewLine(current)) {
				isLineComment = false;
			}
			
			if(i != -1) {
				if(current == '/') {
					char next = (char) i;
					isLineComment = (isLineComment) ? isLineComment : next == '/';
					isBlockComment = (isBlockComment) ? isBlockComment : next == '*';
				}
			}
			
			if(!isLineComment && !isBlockComment) {
				array.add(current);
			}
			
			if(i != -1) {
				if(prev == '*' && current == '/' && isBlockComment) {
					isBlockComment = false;
				}
			}
			
			prev = current;
		}
		
		length = array.size();
		
		reader.close();
	}
	
	/**
	 * 
	 * @return
	 */
	public int read() {
		if(index < length) {
			Character c = array.get(index++);
			return (int)c;
		}
		return -1;
	}
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	private boolean isNewLine(char c) {
		return c == '\n' || c == '\r';
	}
}
