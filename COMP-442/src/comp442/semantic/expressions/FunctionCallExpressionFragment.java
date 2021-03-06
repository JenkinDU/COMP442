package comp442.semantic.expressions;

import java.util.ArrayList;
import java.util.List;

import comp442.codegen.SpecialValues;
import comp442.error.CompilerError;
import comp442.semantic.symboltable.SymbolContext;
import comp442.semantic.symboltable.SymbolTable;
import comp442.semantic.symboltable.entries.FunctionEntry;
import comp442.semantic.symboltable.entries.MemberFunctionEntry;
import comp442.semantic.symboltable.entries.SymbolTableEntry;
import comp442.semantic.symboltable.entries.types.FunctionType;
import comp442.semantic.symboltable.entries.types.LateBindingType;
import comp442.semantic.symboltable.entries.types.SymbolTableEntryType;
import comp442.semantic.value.DynamicValue;
import comp442.semantic.value.FunctionCallValue;
import comp442.semantic.value.LateBindingDynamicValue;
import comp442.semantic.value.Value;

public class FunctionCallExpressionFragment extends TypedExpressionElement {

	private String id;
	
	private List<TypedExpressionElement> expressions;
	
	private SymbolTable surroundingScope;
	
	public FunctionCallExpressionFragment(String id){
		this.id = id;
		this.expressions = new ArrayList<TypedExpressionElement>();
		this.surroundingScope = SymbolContext.getCurrentScope();
	}
	
	@Override
	public void acceptSubElement(ExpressionElement e) throws CompilerError {
		
		if(e instanceof RelationExpressionFragment){
			expressions.add((RelationExpressionFragment) e);
		}else{
			super.acceptSubElement(e);			
		}		
	}
	
	@Override
	public Value getValue() throws CompilerError {
		return new LateBindingDynamicValue() {
			
			@Override
			public DynamicValue get() throws CompilerError {
				SymbolTableEntry entry = surroundingScope.find(id);
				SymbolTable outerScope = surroundingScope.getParent();
				if(entry instanceof MemberFunctionEntry){
					if(outerScope.exists(id)){
						expressions.add(0, new VariableExpressionFragment(SpecialValues.THIS_POINTER_NAME, surroundingScope));
						return new FunctionCallValue((FunctionEntry) entry, expressions);
					}else{
						throw new CompilerError("Can not find member function " + id);
					}
				}if(entry instanceof FunctionEntry){
					return new FunctionCallValue((FunctionEntry) entry, expressions);
				}else{
					throw new CompilerError("Could not find function " + id);
				}
			}
		};
	}

	@Override
	public SymbolTableEntryType getType() {
		return new LateBindingType(){
			@Override
			public SymbolTableEntryType get() throws CompilerError {
				SymbolTableEntry entry = surroundingScope.find(id);
				
				if(entry instanceof FunctionEntry){
					return ((FunctionType)((FunctionEntry) entry).getType()).getReturnType();
				}else{
					throw new CompilerError("Could not find function " + id);
				}

			}
		};
	}

	public String getId() {
		return id;
	}

	public List<TypedExpressionElement> getExpressions() {
		return expressions;
	}

}
