package comp442.syntactical.parser;

import static comp442.syntactical.data.Symbol.END_MARKER;
import static comp442.syntactical.data.Symbol.EPSILON;
import static comp442.syntactical.data.Symbol.prog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Set;

import comp442.lexical.Scanner;
import comp442.lexical.token.Token;
import comp442.semantic.action.SemanticContext;
import comp442.syntactical.data.First;
import comp442.syntactical.data.Follow;
import comp442.syntactical.data.Grammar;
import comp442.syntactical.data.Symbol;

public class Parser {

	private Scanner scanner;
	private Token token;
	private Token previousToken;
	private Symbol symbol;
	
	private int nErrors;
	
	private PrintStream error;
	private PrintStream output;
	private PrintStream derivation;
	
	public Parser(File input) throws FileNotFoundException{
		this.scanner = new Scanner(new FileInputStream(input));
		
		String baseName = input.getPath();
		baseName = baseName.substring(0, baseName.lastIndexOf('.'));
		
		error      = new PrintStream(new File(baseName + ".error"));
		output     = new PrintStream(new File(baseName + ".output"));
		derivation = new PrintStream(new File(baseName + ".derivation"));
		
		nErrors = 0;
		
		SemanticContext.reset();
		
	}

		
	public void parse(){
		ParseTree tree = new ParseTree(prog);
		nextToken();
		parse(tree);
		
		tree.printSelf(derivation);
		tree.printParsedCode(output);
		
		
		System.out.println("-------------------------------------");
		System.out.println(SemanticContext.printableString());
		
		error.close();
		output.close();
		derivation.close();
	}
	
	private void nextToken(){
		previousToken = token;
		token         = scanner.getNext();
		symbol        = Symbol.fromToken(token);	
	}
	
	private boolean parse(ParseTree tree){
		
		skipErrors(tree);
		
		if(tree.symbol.isTerminal()){
			if(tree.symbol == symbol){
				
				tree.setToken(token);
				nextToken();
				return true;
			}else{
				if(symbol == END_MARKER){
					error.println("ERROR: unexpected end of input, expected " + tree.symbol);					
				}
				
				return false;
			}
		}else{
			boolean nullable = false;
			Symbol[][] productions = Grammar.productions.get(tree.symbol);
			for(Symbol[] production : productions){
				if(First.get(production).contains(symbol)
				|| (
						First.get(production).contains(EPSILON)
					&&  Follow.get(production).contains(symbol)
				)){
					// This rule matches !
					for(Symbol child : production){
						if(child.isSemanticAction()){
							child.action.execute(previousToken);
						}else{
							ParseTree childTree = new ParseTree(child);
							tree.addChild(childTree);
							parse(childTree);
						}
					}
					return true;
				}
				// We assume our grammar doesn't include productions with multiple
				// EPSILONs in a row, because that would make no sense ... :P
				if(production[0] == EPSILON){
					nullable = true;
				}
				
			}
			
			if(nullable){
				tree.addChild(new ParseTree(EPSILON));
				return true;
			}else{
				error.println("ERROR: no rule matches! Current symbol is " + tree.symbol + " looking for " + symbol);
				
				return false;
			}
		}		
	}

	public int getNErrors(){
		return nErrors;
	}
	
	private void skipErrors(ParseTree tree) {
		
		Set<Symbol> first = First.get(tree.symbol);
		
		// If this symbol is nullable, we don't care
		if (first.contains(EPSILON)) return;
		
		// Skip any token that cannot follow the current token
		while( ! first.contains(symbol) && token != null){
			error.println("ERROR: unexpected token '" + symbol + "' on line " + token.lineno + ", expecting any of " + first);			
			++nErrors;
			nextToken();
		}
		
	}
	
}
