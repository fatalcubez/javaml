package fatalcubez.ml.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import fatalcubez.ml.workspace.functions.Function;
import fatalcubez.ml.workspace.functions.IFunction;
import fatalcubez.ml.workspace.operations.IOperation;
import fatalcubez.ml.workspace.operations.Operation;

public class Matlab {

	private HashMap<String, ExpressionValue> variables;
	
	public Matlab(){
		variables = new HashMap<>();
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
	}

	private void checkBrackets(String input) throws WorkspaceInputException {
		// Check to see if opening brackets count is same as closing
		int opening = input.length() - input.replace("[", "").length();
		int closing = input.length() - input.replace("]", "").length();
		if (opening != closing) throw new WorkspaceInputException("Number of '[' does not equal number of ']'.");
		if (opening == 0 && closing == 0) return;

		// Check to see if starting/ending characters are invalid brackets
		if (input.charAt(0) == ']' || input.charAt(input.length() - 1) == '[') throw new WorkspaceInputException("Invalid bracket placement (start/end).");
	}

	private void checkAssignment(String input) throws WorkspaceInputException {
		if (!input.contains("=")) return;
		String patternCharacters = "(?<![<>~=])=(?!=)";
		Pattern pattern = Pattern.compile(patternCharacters);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String left = input.substring(0, matcher.start()).replace(" ", "");
			if (left.isEmpty()) throw new WorkspaceInputException("Invalid statement preceding assignment operator.");
			if (matcher.start() + 1 == input.length()) throw new WorkspaceInputException("Invalid statement following assignment operator.");
			patternCharacters = "^\\[[A-Za-z]\\w*(?:,[A-Za-z]\\w*)*\\]$|^[A-Za-z]\\w*$";
			pattern = Pattern.compile(patternCharacters);
			matcher = pattern.matcher(left);
			if (!matcher.find()) throw new WorkspaceInputException("The expression to the left of the equals sign is not a valid target for an assignment.");
			boolean isFunction = Function.getFunction(left) != null;
			if (isFunction) throw new WorkspaceInputException("Can't use function name in variable declaration.");
		}
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
	private void evaluateExpression(String input) throws WorkspaceInputException {
		// Get rid of spaces unless they are inside of '[' ']'
		input = removeSpaces(input);

		// First check to see if there is an assignment operator
		String patternCharacters = "(?<![<>~=])=(?!=)";
		Pattern pattern = Pattern.compile(patternCharacters);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			int index = matcher.start();
			String left = input.substring(0, index);
			String right = input.substring(index + 1, input.length());
			int numOutputs = left.length() - left.replace(",", "").length() + 1;
			if(numOutputs > 1){
				String[] vars = left.substring(1, left.length() - 1).split(",");
				for(int i = 0; i < vars.length; i++){
					if(Function.getFunction(vars[i]) != null) throw new WorkspaceInputException("Can't use function name in variable declaration.");
				}
				List<ExpressionValue> values = simplify(right, numOutputs);
				for(int i = 0; i < values.size(); i++){
					ExpressionValue v = values.get(i);
					String name = vars[i];
					variables.put(name, v);
				}
			}
			else{
				left = left.replace("[", "").replace("]", "");
				ExpressionValue value = simplify(right);
				variables.put(left, value);
			}
		} else {
			ExpressionValue value = simplify(input);
			variables.put("ans", value);
		}
	}

	private List<ExpressionValue> simplify(String input, int numOutputs) throws WorkspaceInputException {
		if (numOutputs <= 1) throw new WorkspaceInputException("Invalid use of multiple outputs.");
		if ((input.charAt(0) == '\'' || input.charAt(0) == '"') && (input.charAt(input.length() - 1) == '\'' || input.charAt(input.length() - 1) == '"')) {
			throw new WorkspaceInputException("Too many output arguments for String expression.");
		}
		if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')') {
			return simplify(input.substring(1, input.length() - 1), numOutputs);
		}
		String characters = "^[A-Za-z]\\w*\\(.*\\)$";
		Pattern pattern = Pattern.compile(characters);
		Matcher matcher = pattern.matcher(input);
		if (!matcher.find()) throw new WorkspaceInputException("Too many output arguments for operator.");
		String name = input.split("\\(")[0];
		Function func = Function.getFunction(name);
		IFunction function = func.getFunctionInstance();
		if(function == null) throw new WorkspaceInputException("Invalid function '" + name + "' for multiple outputs.");
		function = func.getFunctionInstance();
		List<ExpressionValue> params = new ArrayList<ExpressionValue>();
		characters = "\\(.+\\)";
		pattern = Pattern.compile(characters);
		matcher = pattern.matcher(input);
		int begin = 0;
		if (matcher.find()) {
			String p = (String) matcher.group().subSequence(1, matcher.group().length() - 1);
			int opening = 0;
			for (int i = 0; i < p.length(); i++) {
				char character = p.charAt(i);
				if ((p.charAt(i) == ',' && opening == 0) || i == p.length() - 1) {
					String str = i == p.length() - 1 ? p.substring(begin) : p.substring(begin, i);
					params.add(simplify(str));
					begin = i + 1;
					continue;
				}
				if (character == '(' || character == '[') {
					opening++;
					continue;
				}
				if (character == ')' || character == ']') {
					opening--;
					continue;
				}
			}
		} else {
			throw new WorkspaceInputException("No parameters for function " + name + ".");
		}
		return function.evaluate(params, numOutputs);
	}

	private ExpressionValue simplify(String input) throws WorkspaceInputException {
		if ((input.charAt(0) == '\'' || input.charAt(0) == '"') && (input.charAt(input.length() - 1) == '\'' || input.charAt(input.length() - 1) == '"') && input.length() > 1) {
			return new StringValue(input.substring(1, input.length() - 1));
		}

		int opening = 0;

		// Looking for | operator
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
			if (current == '|' && opening == 0) {
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
				return Operation.OR.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
			}
		}

		opening = 0;

		// Looking for & operator
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
			if (current == '&' && opening == 0) {
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
				return Operation.AND.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
			}
		}

		// Looking for ==, ~=, >, >=, <, <= operators
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
			if ((current == '<' || current == '>') && opening == 0) {
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
				if (current == '>') {
					return Operation.GREATER_THAN.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				} else {
					return Operation.LESS_THAN.getOperationInstance().evaluate(v1, simplify(input.substring(i + 1, input.length())), elementWise);
				}
			} else if (current == '=' && opening == 0) {
				boolean elementWise = false;
				if (i == input.length() - 1) throw new WorkspaceInputException("Operation missing second term.");
				if (i - 1 <= 0) throw new WorkspaceInputException("Invalid operation placement.");
				i--;
				if (i - 1 == 0 && input.charAt(i - 1) == '.') throw new WorkspaceInputException("Invalid use of dot operator.");
				if (i - 1 > 0 && input.charAt(i - 1) == '.') elementWise = true;
				ExpressionValue v1 = null;
				if (i == 0) {
					throw new WorkspaceInputException("Invalid operation placement.");
				} else {
					v1 = elementWise ? simplify(input.substring(0, i - 1)) : simplify(input.substring(0, i));
				}
				current = input.charAt(i);
				if (current == '=') {
					return Operation.EQUAL.getOperationInstance().evaluate(v1, simplify(input.substring(i + 2, input.length())), elementWise);
				} else if (current == '~') {
					return Operation.NOT_EQUAL.getOperationInstance().evaluate(v1, simplify(input.substring(i + 2, input.length())), elementWise);
				} else if (current == '<') {
					return Operation.LESS_THAN_EQUAL.getOperationInstance().evaluate(v1, simplify(input.substring(i + 2, input.length())), elementWise);
				} else if (current == '>') {
					return Operation.GREATER_THAN_EQUAL.getOperationInstance().evaluate(v1, simplify(input.substring(i + 2, input.length())), elementWise);
				} else {
					throw new WorkspaceInputException("Invalid operation character '" + current + "'.");
				}
			}
		}

		opening = 0;

		// Looking for :
		List<ExpressionValue> list = new ArrayList<ExpressionValue>();
		int begin = 0;
		for (int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			if (i == input.length() - 1 && list.size() > 0) {
				list.add(simplify(input.substring(begin)));
				continue;
			}
			if (current == '(' || current == '[') {
				opening++;
				continue;
			}
			if (current == ')' || current == ']') {
				opening--;
				continue;
			}
			// Split
			if (opening == 0 && current == ':') {
				if (input.substring(begin, i).isEmpty()) throw new WorkspaceInputException("Invalid use of ':' operator.");
				list.add(simplify(input.substring(begin, i)));
				begin = i + 1;
			}
		}
		if (!list.isEmpty()) {
			return MatOp.createVector(list);
		}

		opening = 0;

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

		// Negate
		if (input.charAt(0) == '~') {
			if (input.length() == 1) throw new WorkspaceInputException("Invalid use of negate operation.");
			return MatOp.negate(simplify(input.substring(1)));
		}

		// Transpose
		if (input.charAt(input.length() - 1) == '\'') {
			if (input.length() == 1) throw new WorkspaceInputException("Invalid use of transpose operation.");
			return MatOp.transpose(input.charAt(input.length() - 2) == '.' ? simplify(input.substring(0, input.length() - 2)) : simplify(input.substring(0, input.length() - 1)));
		}
		
		if (input.charAt(0) == '(' && input.charAt(input.length() - 1) == ')') {
			return simplify(input.substring(1, input.length() - 1));
		}

		// Check for function or indexing
		String characters = "^[A-Za-z]\\w*\\(.*\\)$";
		Pattern pattern = Pattern.compile(characters);
		Matcher matcher = pattern.matcher(input);
		// Either function -> ex. sum(...) or indexing -> ex. A(...)
		if (matcher.find()) {
			String name = input.split("\\(")[0];
			Function func = Function.getFunction(name);
			IFunction function = null;
			boolean isFunction = func != null;
			if (!isFunction && !variables.containsKey(name)) throw new WorkspaceInputException("Undefined function or variable '" + name + "'.");
			ExpressionValue value = null;
			if (!isFunction)
				value = variables.get(name);
			else
				function = func.getFunctionInstance();
			List<ExpressionValue> params = new ArrayList<ExpressionValue>();
			characters = "\\(.+\\)";
			pattern = Pattern.compile(characters);
			matcher = pattern.matcher(input);
			begin = 0;
			if (matcher.find()) {
				String p = (String) matcher.group().subSequence(1, matcher.group().length() - 1);
				opening = 0;
				for (int i = 0; i < p.length(); i++) {
					char character = p.charAt(i);
					if ((p.charAt(i) == ',' && opening == 0) || i == p.length() - 1) {
						// If its not a function, have to handle ':' and special
						// 'end' keyword
						String str = i == p.length() - 1 ? p.substring(begin) : p.substring(begin, i);
						if (!isFunction) {
							if (str.length() == 1 && str.charAt(0) == ':') {
								str = "(1:end)'";
							}
							if (value instanceof MatrixValue) {
								// Linear indexing
								if (params.size() == 0 && i == p.length() - 1) {
									str = str.replace("end", "" + value.getMaxIndex());
								}
								// 2D indexing rows
								else if (params.size() == 0) {
									str = str.replace("end", "" + ((MatrixValue) value).getRows());
								}
								// 2D indexing cols
								else {
									str = str.replace("end", "" + ((MatrixValue) value).getCols());
								}
							} else {
								str = str.replace("end", "" + value.getMaxIndex());
							}

						}
						params.add(simplify(str));
						begin = i + 1;
						continue;
					}
					if (character == '(' || character == '[') {
						opening++;
						continue;
					}
					if (character == ')' || character == ']') {
						opening--;
						continue;
					}
				}
			} else {
				if (isFunction) throw new WorkspaceInputException("No parameters for function " + name + ".");
			}
			return isFunction ? function.evaluate(params, 1).get(0) : MatOp.index(value, params);
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
		if (variables.containsKey(input)) return variables.get(input);
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
	private List<String> getStatements(String input) {
		// Get rid of spaces
		input = removeSpaces(input);

		int opening = 0;
		List<String> statements = new ArrayList<String>();
		int begin = 0;
		int end = 0;
		for (int i = 0; i < input.length(); i++) {
			end = i;
			char currentChar = input.charAt(i);
			if (i == input.length() - 1 && currentChar != ';' && currentChar != ',') {
				statements.add(input.substring(begin, input.length()));
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
			case ',':
				if (opening == 0) {
					statements.add(input.substring(begin, end));
					begin = end + 1;
				}
			}
		}
		return statements;
	}
	
	/**
	 * Evaluates a Matlab expression given through a String argument.
	 * Updates variables HashMap with the parsed data.
	 *
	 * @param input
	 * @throws WorkspaceInputException
	 */
	public void evaluate(String input) throws WorkspaceInputException{
		List<String> statements = getStatements(input);
		for(String statement : statements){
			if(statement.isEmpty()) continue;
			String temp = statement;
			checkStatement(temp);
			temp = condenseStatment(temp);
			evaluateExpression(temp);
		}
	}
	
	private String removeSpaces(String input){
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
		return input;
	}
	
	public HashMap<String, ExpressionValue> getVariables(){
		return variables;
	}
}