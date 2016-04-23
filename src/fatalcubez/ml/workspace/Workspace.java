package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.Scanner;

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
	private void initialCheck(String input) throws WorkspaceInputException {
		// Get rid of spaces
		input = input.replace(" ", "");
		
		// Check to see if opening parenthesis count is same as closing
		int opening = input.length() - input.replace("(", "").length();
		int closing = input.length() - input.replace(")", "").length();
		if(opening != closing) throw new WorkspaceInputException("Number of '(' does not equal number of ')'.");
		
		// Check to see if starting/ending characters are invalid parenthesis
		if(input.charAt(0) == ')' || input.charAt(input.length() - 1) == '(') throw new WorkspaceInputException("Invalid parentheses placement (start/end).");
		
		// Check to make sure all parenthesis are followed by valid characters
		
		
	}
	
	private void evaluate(String input){
		
	}
	
	@Override
	public void run() {
		while(scanner.hasNext() && listening){
			
			String input = scanner.nextLine();
			try{
				initialCheck(input);
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
