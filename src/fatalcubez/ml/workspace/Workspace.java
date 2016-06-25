package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import fatalcubez.ml.workspace.functions.Function;
import fatalcubez.ml.workspace.functions.IFunction;
import fatalcubez.ml.workspace.operations.IOperation;
import fatalcubez.ml.workspace.operations.Operation;

public class Workspace implements Runnable {

	private WorkspaceFormatter formatter;
	private Scanner scanner;
	private boolean listening;
	private Thread thread;
	private HashMap<String, ExpressionValue> workspaceVariables;

	public Workspace(InputStream in) {
		scanner = new Scanner(in);
		formatter = new WorkspaceFormatter();
		workspaceVariables = new HashMap<String, ExpressionValue>();
		startListening();
	}

	public void startListening() {
		if (thread == null) {
			listening = true;
			thread = new Thread(this);
			thread.start();
			System.out.print(">> ");
		}
	}

	public void stopListening() {
		listening = false;
	}

	/**
	 * Takes workspace input and does initial checks on it for immediate errors in the input.
	 * 
	 * @param input
	 * @throws WorkspaceInputException
	 */
	private void checkParentheses(String input) throws WorkspaceInputException {
		// Check to see if opening parenthesis count is same as closing
		int opening = input.length() - input.replace("(", "").length();
		int closing = input.length() - input.replace(")", "").length();
		if (opening != closing) throw new WorkspaceInputException("Number of '(' does not equal number of ')'.");
		if (opening == 0 && closing == 0) return;

		// Check to see if starting/ending characters are invalid parenthesis
		if (input.charAt(0) == ')' || input.charAt(input.length() - 1) == '(') throw new WorkspaceInputException("Invalid parentheses placement (start/end).");

		// Check to make sure all parenthesis are followed by valid characters
		String characters = ".^*+-=)\\]'";
		Pattern pattern = Pattern.compile("\\)[^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after ')'.");

		String characterPattern = "\\([^a-zA-Z0-9\\+\\-(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after '('.");
	}

	private void checkBrackets(String input) throws WorkspaceInputException {
		// Check to see if opening brackets count is same as closing
		int opening = input.length() - input.replace("[", "").length();
		int closing = input.length() - input.replace("]", "").length();
		if (opening != closing) throw new WorkspaceInputException("Number of '[' does not equal number of ']'.");
		if (opening == 0 && closing == 0) return;

		// Check to see if starting/ending characters are invalid brackets
		if (input.charAt(0) == ']' || input.charAt(input.length() - 1) == '[') throw new WorkspaceInputException("Invalid bracket placement (start/end).");

		// Check to make sure all brackets are followed by valid characters
		String characters = ".^*+-=)\\[\\],;'\\s";
		Pattern pattern = Pattern.compile("\\][^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after ']'.");

		String characterPattern = "\\[[^a-zA-Z0-9(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after '['.");
	}

	private void checkAssignment(String input) throws WorkspaceInputException {
		if (!input.contains("=")) return;
		if (input.split("=").length > 2) throw new WorkspaceInputException("Too many assignment operators ('=').");
		String[] sides = input.split("=");
		String left = sides[0];
		String patternCharacters = "^[A-Za-z][A-Za-z0-9_]*$";
		Pattern pattern = Pattern.compile(patternCharacters);
		Matcher matcher = pattern.matcher(left);
		if (!matcher.find()) throw new WorkspaceInputException("The expression to the left of the equals sign is not a valid target for an assignment.");
		boolean isFunction = Function.getFunction(left) != null;
		if (isFunction) throw new WorkspaceInputException("Can't use function name in variable declaration.");
	}

	private void checkStatement(String input) throws WorkspaceInputException {
		checkParentheses(input);
		checkBrackets(input);
		checkAssignment(input);
	}

	/**
	 * Evaluates the expression passed in by a string.
	 * 
	 * @param input
	 *            - String to be evaluated
	 * @return a formatted string that can be used as output. String returned will be empty if display is set to false
	 */
	private String evaluate(String input, boolean display) throws WorkspaceInputException {
		String ret = "";

		// Get rid of spaces unless they are inside of '[' ']'
		int opening = 0;
		for (int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			switch (current) {
			case '[':
				opening++;
				break;
			case ']':
				opening--;
				break;
			case ' ':
				if (opening == 0) {
					if (i + 1 > input.length() - 1)
						input = input.substring(0, i);
					else {
						input = input.substring(0, i) + input.substring(i + 1, input.length());
						i--;
					}
				}
				break;
			}
		}

		// First check to see if there is an assignment operator
		if (input.indexOf('=') != -1) {
			String[] sides = input.split("=");
			String left = sides[0]; // left side is the variable name and must be saved
			String right = sides[1]; // right side is going to be evaluated
			ExpressionValue value = simplify(right);
			workspaceVariables.put(left, value);
			ret = formatter.formatAssignment(left, value);
		} else {
			ExpressionValue value = simplify(input);
			workspaceVariables.put("ans", value);
			ret = formatter.formatAssignment("ans", value);
		}
		return display ? ret : "";
	}

	private ExpressionValue simplify(String input) throws WorkspaceInputException {
		if((input.charAt(0) == '\'' || input.charAt(0) == '"') && (input.charAt(input.length() - 1) == '\'' || input.charAt(input.length() - 1) == '"')){
			return new StringValue(input.substring(1, input.length() - 1));
		}
		int opening = 0;

		// Looking for + or -
		for (int i = input.length() - 1; i >= 0; i--) {
			char current = input.charAt(i);
			if (current == '(' || current == '[') {
				opening++;
				continue;
			}
			if (current == ')' || current == ']') {
				opening--;
				continue;
			}
			if ((current == '+' || current == '-') && opening == 0) {
				boolean elementWise = false;
				if (i == input.length() - 1) throw new WorkspaceInputException("Operation missing second term.");
				if (i - 1 == 0 && input.charAt(i - 1) == '.') throw new WorkspaceInputException("Invalid use of dot operator.");
				if (i - 1 > 0 && input.charAt(i - 1) == '.') elementWise = true;
				ExpressionValue v1 = null;
				if (i == 0) {
					v1 = new ScalarValue(0.0d);
				} else if (input.charAt(i - 1) == '*' || input.charAt(i - 1) == '/' || input.charAt(i - 1) == '^') {
					if (i - 2 < 0) throw new WorkspaceInputException("Operation missing first term.");
					if (input.charAt(i - 2) == '.') elementWise = true;
					IOperation op = Operation.getOperation(Character.toString(input.charAt(i - 1))).getOperationInstance();
					if (elementWise) {
						return op.evaluate(simplify(input.substring(0, i - 2)), simplify(input.substring(i, input.length())), elementWise);
					} else {
						return op.evaluate(simplify(input.substring(0, i - 1)), simplify(input.substring(i, input.length())), elementWise);
					}
				} else {
					v1 = elementWise ? simplify(input.substring(0, i - 1)) : simplify(input.substring(0, i));
				}
				if (current == '+') {
					return Operation.ADD.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				} else {
					return Operation.SUBTRACT.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				}
			}
		}

		opening = 0;

		// Looking for * or /
		for (int i = input.length() - 1; i >= 0; i--) {
			char current = input.charAt(i);
			if (current == '(' || current == '[') {
				opening++;
				continue;
			}
			if (current == ')' || current == ']') {
				opening--;
				continue;
			}
			if ((current == '*' || current == '/') && opening == 0) {
				boolean elementWise = false;
				if (i == input.length() - 1) throw new WorkspaceInputException("Operation missing second term.");
				if (i - 1 == 0 && input.charAt(i - 1) == '.') throw new WorkspaceInputException("Invalid use of dot operator.");
				if (i - 1 > 0 && input.charAt(i - 1) == '.') elementWise = true;
				ExpressionValue v1 = null;
				if (i == 0) {
					throw new WorkspaceInputException("Invalid operation placement.");
				} else {
					v1 = elementWise ? simplify(input.substring(0, i - 1)) : simplify(input.substring(0, i));
				}
				if (current == '*') {
					return Operation.MULTIPLY.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				} else {
					return Operation.DIVIDE.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				}
			}
		}

		opening = 0;

		// Looking for ^
		for (int i = input.length() - 1; i >= 0; i--) {
			char current = input.charAt(i);
			if (current == '(' || current == '[') {
				opening++;
				continue;
			}
			if (current == ')' || current == ']') {
				opening--;
				continue;
			}
			if (current == '^' && opening == 0) {
				boolean elementWise = false;
				if (i == input.length() - 1) throw new WorkspaceInputException("Operation missing second term.");
				if (i - 1 == 0 && input.charAt(i - 1) == '.') throw new WorkspaceInputException("Invalid use of dot operator.");
				if (i - 1 > 0 && input.charAt(i - 1) == '.') elementWise = true;
				ExpressionValue v1 = null;
				if (i == 0) {
					throw new WorkspaceInputException("Invalid operation placement.");
				} else {
					v1 = elementWise ? simplify(input.substring(0, i - 1)) : simplify(input.substring(0, i));
				}
				return Operation.POWER.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
			}
		}

		// Transpose
		if (input.charAt(input.length() - 1) == '\'') {
			if (input.length() == 1) throw new WorkspaceInputException("Invalid use of transpose operation.");
			return MatOp.transpose(simplify(input.substring(0, input.length() - 1)));
		}

		// Check for function
		String characters = "^[a-zA-z]+\\(.*\\)$";
		Pattern pattern = Pattern.compile(characters);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String name = input.split("\\(")[0];
			IFunction function = Function.getFunction(name).getFunctionInstance();
			List<ExpressionValue> params = new ArrayList<ExpressionValue>();
			characters = "\\(.+\\)";
			pattern = Pattern.compile(characters);
			matcher = pattern.matcher(input);
			int begin = 0;
			if (matcher.find()){
				String p = (String)matcher.group().subSequence(1, matcher.group().length() - 1);
				opening = 0;
				for(int i = 0; i < p.length(); i++){
					char character = p.charAt(i);
					if(i == p.length() - 1){
						params.add(simplify(p.substring(begin)));
						break;
					}
					if(character == '(' || character == '['){
						opening++;
						continue;
					}
					if(character == ')' || character == ']'){
						opening--;
						continue;
					}
					if(p.charAt(i) == ',' && opening == 0){
						params.add(simplify(p.substring(begin, i)));
						begin = i + 1;
					}
				}
			}else{
				throw new WorkspaceInputException("No parameters for function " + name + ".");
			}
			return function.evaluate(params);
		}

		if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')') {
			return simplify(input.substring(1, input.length() - 1));
		}
		if (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') {
			return parseMatrix(input.substring(1, input.length() - 1));
		}
		return parseValue(input);
	}

	private MatrixValue parseMatrix(String input) throws WorkspaceInputException {
		List<String> rows = new ArrayList<String>();

		int opening = 0;
		int begin = 0;
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c == '[') {
				opening++;
				continue;
			}
			if (c == ']') {
				opening--;
				continue;
			}
			if (c == ';' && opening == 0) {
				String sub = input.substring(begin, i);
				if (!sub.isEmpty()) rows.add(sub);
				begin = i + 1;
			}
		}
		rows.add(input.substring(begin));

		if (rows.size() == 1) {
			// HORIZONTAL CONCATENATION
			String row = rows.get(0);
			List<String> elements = new ArrayList<String>();

			opening = 0;
			begin = 0;
			for (int i = 0; i < row.length(); i++) {
				char c = row.charAt(i);
				if (c == '[') {
					opening++;
					continue;
				}
				if (c == ']') {
					opening--;
					continue;
				}
				if (c == ',' || c == ' ' && opening == 0) {
					String sub = row.substring(begin, i);
					if (!sub.isEmpty()) elements.add(sub);
					begin = i + 1;
				}
			}
			elements.add(row.substring(begin));

			List<ExpressionValue> rowElements = new ArrayList<ExpressionValue>();
			int numCols = 0;
			int numRows = 0;
			for (int i = 0; i < elements.size(); i++) {
				ExpressionValue v = simplify(elements.get(i));
				if (v instanceof MatrixValue) {
					numCols += ((MatrixValue) v).getMatrix().getColumnDimension();
					numRows = ((MatrixValue) v).getMatrix().getRowDimension();
				} else {
					numCols++;
					numRows = 1;
				}
				rowElements.add(v);
			}
			RealMatrix matrix = MatrixUtils.createRealMatrix(numRows, numCols);
			int columnIndex = 0;
			for (ExpressionValue v : rowElements) {
				if (v instanceof MatrixValue) {
					MatrixValue mV = (MatrixValue) v;
					if (mV.getMatrix().getRowDimension() != matrix.getRowDimension()) throw new WorkspaceInputException("Horizontal concat dimension mismatch.");
					matrix.setSubMatrix(mV.getMatrix().getData(), 0, columnIndex);
					columnIndex += mV.getMatrix().getColumnDimension();
				} else {
					ScalarValue sV = (ScalarValue) v;
					if (matrix.getRowDimension() != 1) throw new WorkspaceInputException("Horizontal concat dimension mismatch.");
					matrix.setEntry(0, columnIndex, sV.getScalar());
					columnIndex++;
				}
			}
			return new MatrixValue(matrix);
		} else {
			// VERTICAL CONCATENATION
			List<MatrixValue> columnElements = new ArrayList<MatrixValue>();
			int numRows = 0;
			int numCols = 0;
			for (int i = 0; i < rows.size(); i++) {
				String row = rows.get(i);
				MatrixValue rowValue = parseMatrix(row);
				numCols = rowValue.getMatrix().getColumnDimension();
				numRows += rowValue.getMatrix().getRowDimension();
				columnElements.add(rowValue);
			}
			RealMatrix matrix = MatrixUtils.createRealMatrix(numRows, numCols);
			int rowIndex = 0;
			for (MatrixValue mV : columnElements) {
				if (mV.getMatrix().getColumnDimension() != matrix.getColumnDimension()) throw new WorkspaceInputException("Vertical concat dimension mismatch.");
				matrix.setSubMatrix(mV.getMatrix().getData(), rowIndex, 0);
				rowIndex += mV.getMatrix().getRowDimension();
			}
			return new MatrixValue(matrix);
		}
	}

	private String condenseStatment(String input) {
		String characters = "[+-]{2,}";
		Pattern pattern = Pattern.compile(characters);
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			int count = matcher.group().length() - matcher.group().replace("-", "").length();
			String replace = matcher.group().replace("+", "\\+");
			if (count % 2 == 1) {
				input = input.replaceFirst(replace, "-");
			} else {
				input = input.replaceFirst(replace, "+");
			}
		}
		characters = "[*\\/^\\(\\[,\\;]\\+";
		pattern = Pattern.compile(characters);
		matcher = pattern.matcher(input);
		while (matcher.find()) {
			input = input.replace(matcher.group(), matcher.group().substring(0, 1));
		}
		input = input.replaceAll("^\\+", "");
		return input;
	}

	private ExpressionValue parseValue(String input) throws WorkspaceInputException {
		if (workspaceVariables.containsKey(input)) return workspaceVariables.get(input);
		String patternCharacters = "[^0-9.-]";
		Pattern pattern = Pattern.compile(patternCharacters);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Can't parse statment " + input);
		double value = Double.parseDouble(input);
		return new ScalarValue(value);
	}

	/**
	 * Splits user input into statements based on semicolons and commas.
	 * 
	 * @param input
	 * @return a two object list where the first object in the list is an array of all the statements and the second object is a list of all the boolean values for whether or not each value should be displayed.
	 */
	private List<Object> getStatements(String input) {
		// Get rid of spaces
		int opening = 0;
		for (int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			switch (current) {
			case '[':
				opening++;
				break;
			case ']':
				opening--;
				break;
			case ' ':
				if (opening == 0) {
					if (i + 1 > input.length() - 1)
						input = input.substring(0, i);
					else {
						input = input.substring(0, i) + input.substring(i + 1, input.length());
						i--;
					}
				}
				break;
			}
		}
		
		opening = 0;
		List<String> statements = new ArrayList<String>();
		List<Boolean> displayList = new ArrayList<Boolean>();
		int begin = 0;
		int end = 0;
		for (int i = 0; i < input.length(); i++) {
			end = i;
			char currentChar = input.charAt(i);
			if (i == input.length() - 1 && currentChar != ';' && currentChar != ',') {
				statements.add(input.substring(begin, input.length()));
				displayList.add(true);
			}
			switch (currentChar) {
			case '[':
			case '(':
				opening++;
				break;
			case ']':
			case ')':
				opening--;
				break;
			case ';':
				if (opening == 0) {
					statements.add(input.substring(begin, end));
					displayList.add(false);
					begin = end + 1;
				}
				break;
			case ',':
				if (opening == 0) {
					statements.add(input.substring(begin, end));
					displayList.add(true);
					begin = end + 1;
				}
			}
		}
		List<Object> ret = new ArrayList<Object>();
		ret.add(statements.toArray(new String[0]));
		ret.add(displayList.toArray(new Boolean[0]));
		return ret;
	}

	@Override
	public void run() {
		while (scanner.hasNext() && listening) {
			String input = scanner.nextLine();
			try {
				List<Object> list = getStatements(input);
				String[] statements = (String[]) list.get(0);
				Boolean[] displayValues = (Boolean[]) list.get(1);
				for (int i = 0; i < statements.length; i++) {
					if (statements[i].isEmpty()) continue;
					checkStatement(statements[i]);
					statements[i] = condenseStatment(input);
					String formattedOutput = evaluate(statements[i], displayValues[i]);
					if (!formattedOutput.isEmpty()) System.out.println(formattedOutput);
				}
			} catch (WorkspaceInputException e) {
				String formattedOutput = formatter.formatError(input, e);
				System.out.println(formattedOutput);
				System.out.print(">> ");
				continue;
			}
			System.out.print(">> ");
		}
		scanner.close();
	}
}
