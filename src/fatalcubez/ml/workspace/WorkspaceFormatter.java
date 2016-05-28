package fatalcubez.ml.workspace;

public class WorkspaceFormatter {
	
	private static final String tab = "    ";

	public String formatError(String input, WorkspaceInputException e){
		return "\n" + tab + input + "\n" + tab + "Error: " + e.getMessage() + "\n"; 
	}
	
	public String formatAssignment(String variable, ExpressionValue value){
		String formattedValue = "";
		if(value instanceof ScalarValue){
			ScalarValue sV = (ScalarValue)value;
			if(sV.getScalar() == Math.floor(sV.getScalar())) formattedValue = "" + (int)sV.getScalar();
			else formattedValue = "" + String.format("%.4f",sV.getScalar());
		}
		return "\n" + variable + " =\n\n" + tab + formattedValue + "\n";
	}
}
