package fatalcubez.ml.workspace;

import java.io.InputStream;
import java.util.Scanner;

public class Workspace implements Runnable{

	private Thread workspaceThread;
	private WorkspaceFormatter formatter;
	private Scanner scanner;
	private boolean listening;
	
	public Workspace(InputStream in){
		scanner = new Scanner(in);
		formatter = new WorkspaceFormatter();
		workspaceThread = new Thread(this);
		startListening();
	}

	public void startListening(){
		listening = true;
	}
	
	public void stopListening(){
		listening = false;
	}
	
	private void evaluate(String input){
		
	}
	
	@Override
	public void run() {
		while(scanner.hasNext() && listening){
			evaluate(scanner.nextLine());
		}
		scanner.close();
	}
	
}
