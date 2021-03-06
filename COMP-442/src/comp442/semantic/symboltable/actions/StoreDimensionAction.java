package comp442.semantic.symboltable.actions;

import java.util.ArrayList;

import comp442.lexical.token.Token;
import comp442.semantic.symboltable.SymbolAction;
import comp442.semantic.symboltable.entries.types.ArrayType;

public class StoreDimensionAction extends SymbolAction {
	
	@Override
	public void execute(Token precedingToken) {

		ArrayType type;
		if( context.storedType instanceof ArrayType){
			type = (ArrayType) context.storedType;
		}else{
			type = new ArrayType(context.storedType, new ArrayList<Integer>());
			context.storedType = type;
		}
		
		type.pushDimension(Integer.parseInt(precedingToken.lexeme));
		
	}

}
