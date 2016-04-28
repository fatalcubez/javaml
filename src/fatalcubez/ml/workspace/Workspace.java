package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fatalcubez.ml.workspace.functions.Function;

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
	 * Takes workspace input and does initial checks on it for immediate errors
	 * in the input.
	 * 
	 * @param input
	 * @throws WorkspaceInputException
	 */
	private void checkParentheses(String input) throws WorkspaceInputException {
		// Get rid of spaces
		input = input.replace(" ", "");

		// Check to see if opening parenthesis count is same as closing
		int opening = input.length() - input.replace("(", "").length();
		int closing = input.length() - input.replace(")", "").length();
		if (opening != closing) throw new WorkspaceInputException("Number of '(' does not equal number of ')'.");
		if (opening == 0 && closing == 0) return;

		// Check to see if starting/ending characters are invalid parenthesis
		if (input.charAt(0) == ')' || input.charAt(input.length() - 1) == '(') throw new WorkspaceInputException("Invalid parentheses placement (start/end).");

		// Check to make sure all parenthesis are followed by valid characters
		String characters = ".^*+-=)";
		Pattern pattern = Pattern.compile("\\)[^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after ')'.");

		String characterPattern = "\\([^a-zA-Z0-9(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after '('.");
	}

	private void checkBrackets(String input) throws WorkspaceInputException {
		input = input.replace(" ", "");

		// Check to see if opening brackets count is same as closing
		int opening = input.length() - input.replace("[", "").length();
		int closing = input.length() - input.replace("]", "").length();
		if (opening != closing) throw new WorkspaceInputException("Number of '[' does not equal number of ']'.");
		if (opening == 0 && closing == 0) return;

		// Check to see if starting/ending characters are invalid brackets
		if (input.charAt(0) == ']' || input.charAt(input.length() - 1) == '[') throw new WorkspaceInputException("Invalid bracket placement (start/end).");

		// Check to make sure all brackets are followed by valid characters
		String characters = ".^*+-=)\\[\\],;";
		Pattern pattern = Pattern.compile("\\][^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after ']'.");

		String characterPattern = "\\[[^a-zA-Z0-9(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if (matcher.find()) throw new WorkspaceInputException("Invalid character placement after '['.");
	}

	private void checkAssignment(String input) throws WorkspaceInputException {
		input = input.replace(" ", "");
		if (!input.contains("=")) return;
		if (input.split("=").length > 2) throw new WorkspaceInputException("Too many assignment operators ('=').");
		String[] sides = input.split("=");
		String left = sides[0];
		String patternCharacters = "[^A-Za-z]";
		Pattern pattern = Pattern.compile(patternCharacters);
		Matcher matcher = pattern.matcher(left);
		if (matcher.find()) throw new WorkspaceInputException("The expression to the left of the equals sign is not a valid target for an assignment.");
		boolean isFunction = Function.getFunction(left) != null;
		if (isFunction) throw new WorkspaceInputException("Can't use function name in variable declaration.");
	}

	// private void checkFunctions(String input){
	// input = input.replace(" ", "");
	// String[] words = input.replaceAll("[^A-Za-Z]", " ").split(" ");
	//
	// for(int i = 0; i < words.length; i++){
	// String word = words[i];
	// if(word.length() == 0) continue;
	// for(Function func : Function.values()){
	//
	// }
	// }
	// }

	private void checkStatement(String input) throws WorkspaceInputException {
		checkParentheses(input);
		checkBrackets(input);
		checkAssignment(input);
		// checkFunctions(input);
	}

	/**
	 * Evaluates the expression passed in by a string.
	 * 
	 * @param input
	 *            - String to be evaluated
	 * @return a formatted string that can be used as output. String returned
	 *         will be empty if display is set to false
	 */
	private String evaluate(String input, boolean display) throws WorkspaceInputException {
		String ret = "";
		input = input.replace(" ", "");

		// First check to see if there is an assignment operator
		if (input.indexOf('=') != -1) {
			String[] sides = input.split("=");
			String left = sides[0]; // left side is the variable name and must
									// be saved
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

	private ExpressionValue simplify(String input) {
		
	}

	/**
	 * Splits user input into statements based on semicolons and commas.
	 * 
	 * @param input
	 * @return a two object list where the first object in the list is an array
	 *         of all the statements and the second object is a list of all the
	 *         boolean values for whether or not each value should be displayed.
	 */
	private List<Object> getStatements(String input) {
		input = input.replace(" ", "");
		int opening = 0;
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
				opening++;
				break;
			case ']':
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
