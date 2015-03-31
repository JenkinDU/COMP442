package comp442.test.semantic;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import comp442.semantic.SymbolTable;
import comp442.semantic.action.SemanticContext;
import comp442.semantic.symboltable.entries.SymbolTableEntry;
import comp442.semantic.symboltable.entries.VariableEntry;
import comp442.semantic.symboltable.entries.types.PlainType;
import comp442.syntactical.parser.Parser;

@RunWith(Parameterized.class)
public class PlainVariableTest {

	private static enum Location {
		class_member,
		class_member_function_local,
		program_local,
		function_local
	}
	
	@Parameters(name="{0}")
	public static Collection<Object[]> data(){
		
		List<Object[]> values = new ArrayList<Object[]>();
		
		// name, code, expected, search-path
		for(String type : new String[]{"int", "float", "TestClass"}){
			for(Location location : Location.values()){
				String name = location.toString().replace("_", " ") + " " + type;
				
				String var  = type + " x;";
				
				String code 
						= "class TestClass{"
								+ (location == Location.class_member ? var : "")
								+ "int testMember(){"
									+ (location == Location.class_member_function_local ? var : "")
								+ "};"
						+ "};"
						+ "program {"
							+ (location == Location.program_local ? var : "")
						+ "};"
						+ "int test_func(){"
							+ (location == Location.function_local ? var : "")
						+ "};";
				
				
				VariableEntry expected = new VariableEntry("x", new PlainType(type));
				
				String[] searchPath;
				switch(location){
				case class_member:
					searchPath = new String[]{"TestClass", "x"};
					break;
				case class_member_function_local:
					searchPath = new String[]{"TestClass", "testMember", "x"};
					break;
				case function_local:
					searchPath = new String[]{"test_func", "x"};
					break;
				case program_local:
					searchPath = new String[]{"program", "x"};
					break;
				default:
					searchPath = new String[]{"default"};
					break;
				
				}
				
				values.add(new Object[]{name, code, expected, searchPath});
			}
		}
		return values;
	}
	
	String code;
	SymbolTableEntry expected;
	String searchPath[];
	public PlainVariableTest(String name, String code, VariableEntry expected, String[] searchPath) {
		this.code       = code;
		this.expected   = expected;
		this.searchPath = searchPath;
	}
	
	@org.junit.Test
	public void test() throws Exception {
		Parser p = new Parser(new ByteArrayInputStream(code.getBytes()));
		
		p.parse();

		
		SymbolTable scope = SemanticContext.getGlobalScope();
		
		SymbolTableEntry entry = null;
		for(String name : searchPath){
			entry = scope.find(name);
			scope = entry.getScope();
		}
		
		assertEquals(expected, entry);
	}

}
