package jack.symboltable;

import java.util.Hashtable;

import jack.constant.VariableKind;

/**
 * 
 * @author harisanker.thinkcore
 *
 */
public class SymbolTable {

	/**
	 * 
	 */
	private Hashtable<String, VariableProperty> classLevelTable;
	
	/**
	 * 
	 */
	private Hashtable<String, VariableProperty> subroutineLevelTable;
	
	/**
	 * 
	 */
	private int argumentCount = 0;
	private int fieldCount = 0;
	private int localCount = 0;
	private int staticCount = 0;
	
	/**
	 * 
	 */
	public SymbolTable() {
		classLevelTable = new Hashtable<String, VariableProperty>();
		subroutineLevelTable = new Hashtable<String, VariableProperty>();
	}
	
	/**
	 * 
	 */
	public void startSubroutine() {
		subroutineLevelTable.clear();
		argumentCount = 0;
		localCount = 0;
	}
	
	/**
	 * 
	 * @param name
	 * @param type
	 * @param kind
	 */
	public void define(String name, String type, VariableKind kind) {
		VariableProperty property = new VariableProperty();
		property.kind = kind;
		property.type = type;
		property.index = VarCount(kind);
		
		switch(kind) {
			case FIELD:
				classLevelTable.put(name, property);
				fieldCount++;
			case STATIC:
				classLevelTable.put(name, property);
				staticCount++;
			break;
			case ARGUMENT:
				subroutineLevelTable.put(name, property);
				argumentCount++;
			case LOCAL:
				subroutineLevelTable.put(name, property);
				localCount++;
			break;
		}
	}
	
	/**
	 * 
	 * @param kind
	 * @return
	 */
	public int VarCount(VariableKind kind) {
		switch(kind) {
			case FIELD:
				return fieldCount;
			case STATIC:
				return staticCount;
			case ARGUMENT:
				return argumentCount;
			case LOCAL:
				return localCount;
		}
		
		return 0;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public VariableKind KindOf(String name) {
		VariableProperty property = getVariableProperty(name);
		if(property != null) {
			return property.kind;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public String TypeOf(String name) {
		VariableProperty property = getVariableProperty(name);
		if(property != null) {
			return property.type;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public int IndexOf(String name) {
		VariableProperty property = getVariableProperty(name);
		if(property != null) {
			return property.index;
		}
		
		return -1;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private VariableProperty getVariableProperty(String name) {
		VariableProperty property = subroutineLevelTable.get(name);
		if(property == null) {
			property = classLevelTable.get(name);
		}
		return property;
	}
	
	/**
	 * 
	 * @author harisanker.thinkcore
	 *
	 */
	private class VariableProperty {
		
		public VariableKind kind; 
		
		public String type;
		
		public int index;
	}
}
