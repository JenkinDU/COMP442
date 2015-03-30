package comp442.semantic.symboltable.entries;

import comp442.semantic.SymbolTable;
import comp442.semantic.symboltable.entries.types.SymbolTableEntryType;

public abstract class SymbolTableEntry {

	public static enum Kind {
		Function,
		Class,
		Parameter,
		Variable,
	};
	
	private String name;
	private Kind kind;
	private SymbolTableEntryType type;
	private SymbolTable scope;
	
	public SymbolTableEntry(String name, Kind kind, SymbolTableEntryType type, SymbolTable scope){
		this.name  = name;
		this.kind  = kind;
		this.type  = type;
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public Kind getKind() {
		return kind;
	}

	public SymbolTableEntryType getType() {
		return type;
	}

	public SymbolTable getScope() {
		return scope;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public void setType(SymbolTableEntryType type) {
		this.type = type;
	}

	public void setScope(SymbolTable scope) {
		this.scope = scope;
	}
	
	@Override
	public String toString(){
		return getName() + " " + getKind() + " " + getType(); 
	}
}