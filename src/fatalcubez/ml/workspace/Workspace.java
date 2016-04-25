package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Workspace implements Runnable{

	private WorkspaceFormatter formatter;
	private Scanner scanner;
	private boolean listening;
	private Thread thread;
	
	public Workspace(InputStream in){
		scanner = new Scanner(in);
		formatter = new WorkspaceFormatter();
		startListening();
	}

	public void startListening(){
		if(thread == null){
			listening = true;
			thread = new Thread(this);
			thread.start();
			System.out.print(">> ");
		}
	}
	
	public void stopListening(){
		listening = false;
	}
	
	/**
	 * Takes workspace input and does initial checks on it for immediate errors in the input.
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
		if(opening != closing) throw new WorkspaceInputException("Number of '(' does not equal number of ')'.");
		if(opening == 0 && closing == 0) return;
		
		// Check to see if starting/ending characters are invalid parenthesis
		if(input.charAt(0) == ')' || input.charAt(input.length() - 1) == '(') throw new WorkspaceInputException("Invalid parentheses placement (start/end).");
		
		// Check to make sure all parenthesis are followed by valid characters
		String characters = ".^*+-=)";
		Pattern pattern = Pattern.compile("\\)[^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()) throw new WorkspaceInputException("Invalid character placement after ')'.");
		
		String characterPattern = "\\([^a-zA-Z0-9(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if(matcher.find()) throw new WorkspaceInputException("Invalid character placement after '('.");
	}
	
	private void checkBrackets(String input) throws WorkspaceInputException{
		input = input.replace(" ", "");
		
		// Check to see if opening brackets count is same as closing
		int opening = input.length() - input.replace("[", "").length();
		int closing = input.length() - input.replace("]", "").length();
		if(opening != closing) throw new WorkspaceInputException("Number of '[' does not equal number of ']'.");
		if(opening == 0 && closing == 0) return;
		
		// Check to see if starting/ending characters are invalid brackets
		if(input.charAt(0) == ']' || input.charAt(input.length() - 1) == '[') throw new WorkspaceInputException("Invalid bracket placement (start/end).");
		
		// Check to make sure all brackets are followed by valid characters
		String characters = ".^*+-=)\\[\\],;";
		Pattern pattern = Pattern.compile("\\][^" + characters + "]");
		Matcher matcher = pattern.matcher(input);
		if(matcher.find()) throw new WorkspaceInputException("Invalid character placement after ']'.");
		
		String characterPattern = "\\[[^a-zA-Z0-9(\\[]";
		pattern = Pattern.compile(characterPattern);
		matcher = pattern.matcher(input);
		if(matcher.find()) throw new WorkspaceInputException("Invalid character placement after '['.");
	}
	
	private void checkStatement(String input) throws WorkspaceInputException{
		checkParentheses(input);
		checkBrackets(input);
	}
	
	private void evaluate(String input){
		
	}
	
	@Override
	public void run() {
		while(scanner.hasNext() && listening){
			
			String input = scanner.nextLine();
			try{
				String[] statements = input.replace(" ", "").split(";|,");
				for(int i = 0; i < statements.length; i++){
					if(statements[i].isEmpty()) continue;
					checkStatement(statements[i]);
				}
			}catch(WorkspaceInputException e){
				String formattedOutput = formatter.formatError(input, e);
				System.out.println(formattedOutput);
				System.out.print(">> ");
				continue;
			}
			evaluate(input);
			System.out.print(">> ");
		}
		scanner.close();
	}
	
}
