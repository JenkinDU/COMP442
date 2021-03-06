package comp442.semantic.expressions.actions;

import comp442.error.CompilerError;
import comp442.lexical.token.Token;
import comp442.semantic.expressions.ExpressionAction;

public class FinishVariableAction extends ExpressionAction {

	@Override
	public void execute(Token precedingToken) throws CompilerError {

		try{
			context.finishTopElement();
		}catch(RuntimeException e){
			System.out.println("wooooooopsy " + e);
		}		
	}
}
