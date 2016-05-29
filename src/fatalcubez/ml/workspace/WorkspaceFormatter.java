package fatalcubez.ml.workspace;

public class WorkspaceFormatter {
	
	private static final String tab = "    ";

	public String formatError(String input, WorkspaceInputException e){
		return "\n" + tab + input + "\n" + tab + "Error: " + e.getMessage() + "\n"; 
	}
	
	public String formatAssignment(String variable, ExpressionValue value){
		//TODO: Fix spacing for matrices so that all columns line up
		String formattedValue = "";
		if(value instanceof ScalarValue){
			ScalarValue sV = (ScalarValue)value;
			if(sV.getScalar() == Math.floor(sV.getScalar())) formattedValue = "" + (int)sV.getScalar();
			else formattedValue = "" + String.format("%.4f",sV.getScalar());
		}
		if(value instanceof MatrixValue){
			MatrixValue mV = (MatrixValue)value;
			if(mV.getMatrix().getColumnDimension() > 25) return "\n" + tab + "Matrix too large to be displayed." + "\n";
			boolean allInt = true;
			formattedValue += "\n";
			for(int i = 0; i < mV.getMatrix().getRowDimension(); i++){
				for(int j = 0; j < mV.getMatrix().getColumnDimension(); j++){
					if(allInt && mV.getMatrix().getEntry(i, j) != Math.floor(mV.getMatrix().getEntry(i, j))) allInt = false;
					if(j == 0) formattedValue += tab;
					formattedValue += (String.format("%.4f", mV.getMatrix().getEntry(i, j)) + tab);
					if(j == mV.getMatrix().getColumnDimension() - 1) formattedValue += "\n";
				}
			}
			if(allInt){
				formattedValue = formattedValue.replace(".0000", "");
			}
			return "\n" + variable + " =\n" + formattedValue;
			
		}
		return "\n" + variable + " =\n\n" + tab + formattedValue + "\n";
	}
}
