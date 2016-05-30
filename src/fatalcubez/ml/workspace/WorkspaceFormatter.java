package fatalcubez.ml.workspace;

import java.util.Arrays;

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
		if(value instanceof MatrixValue){
			MatrixValue mV = (MatrixValue)value;
			if(mV.getMatrix().getColumnDimension() > 25) return "\n" + tab + "Matrix too large to be displayed." + "\n";
			boolean allInt = true;
			formattedValue += "\n";
			int biggestNum = 1;
			// Loop through to see if all values are ints and get the biggest number
			for(int i = 0; i < mV.getMatrix().getRowDimension(); i++){
				for(int j = 0; j < mV.getMatrix().getColumnDimension(); j++){
					double doubleValue = mV.getMatrix().getEntry(i, j);
					int intValue = (int)doubleValue;
					if(allInt && doubleValue != Math.floor(doubleValue)) allInt = false;
					int size = ("" + intValue).length();
					if(intValue == 0 && doubleValue < 0) size++;
					if(size > biggestNum) biggestNum = size;
				}	
			}
			// Apply the proper formatting
			for(int i = 0; i < mV.getMatrix().getRowDimension(); i++){
				for(int j = 0; j < mV.getMatrix().getColumnDimension(); j++){
					double doubleValue = mV.getMatrix().getEntry(i, j);
					int intValue = (int)doubleValue;
					if(j == 0) formattedValue += tab;
					int size = ("" + intValue).length();
					if(intValue == 0 && doubleValue < 0) size++;
					int spaces = biggestNum - size;
					char[] charArray = new char[spaces];
					Arrays.fill(charArray, ' ');
					String str = new String(charArray);
					formattedValue += (str + String.format(allInt ? "%.0f" : "%.4f", doubleValue) + tab);
					if(j == mV.getMatrix().getColumnDimension() - 1) formattedValue += "\n";
				}
			}
			if(allInt){
				formattedValue = formattedValue.replace(".0000", "");
			}
			if(biggestNum > 1){
				
			}
			return "\n" + variable + " =\n" + formattedValue;
			
		}
		return "\n" + variable + " =\n\n" + tab + formattedValue + "\n";
	}
}
