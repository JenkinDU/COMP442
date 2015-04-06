package comp442.semantic.symboltable.entries.types;

import java.util.Collections;
import java.util.List;

public class ArrayType implements SymbolTableEntryType {

	private final SymbolTableEntryType type;
	private final List<Integer> dimensions;
	
	public ArrayType(SymbolTableEntryType type, List<Integer> dimensions){
		this.type       = type;
		this.dimensions = dimensions;
	}

	public SymbolTableEntryType getType() {
		return type;
	}

	public List<Integer> getDimensions() {
		return Collections.unmodifiableList(dimensions);
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		for(Integer i : dimensions){
			sb.append('[');
			sb.append(i);
			sb.append(']');
		}
		return sb.toString();
	}

	public void pushDimension(int dim) {
		dimensions.add(dim);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ArrayType){
			ArrayType o = (ArrayType) other;
			if(getType().equals(o.getType())){
				if(dimensions.size() == o.dimensions.size()){
					for(int i = 0; i < dimensions.size(); ++i){
						if(dimensions.get(i) != o.dimensions.get(i)){
							return false;
						}
					}
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	@Override
	public int getSize() {
		// The size to store an array is equal to the size of the base type, times
		// the product of all of the dimensions!
		int size = getType().getSize();
		for(Integer i : getDimensions()){
			size *= i;
		}
		return size;
	}
	
}