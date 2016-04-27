package fatalcubez.ml.workspace;

public class WorkspaceFormatter {
	
	private static final String tab = "    ";

	public String formatError(String input, WorkspaceInputException e){
		return "\n" + tab + input + "\n" + tab + "Error: " + e.getMessage() + "\n"; 
	}
	
	public String formatAssignment(String variable, ExpressionValue value){
		
	}
}
