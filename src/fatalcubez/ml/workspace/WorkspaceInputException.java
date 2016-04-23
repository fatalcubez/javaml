package fatalcubez.ml.workspace;

public class WorkspaceInputException extends Exception{

	private static final long serialVersionUID = 1L;
	public WorkspaceInputException() { super(); };
	public WorkspaceInputException(String message) { super(message); };
	public WorkspaceInputException(String message, Throwable clause) { super(message, clause); };
	public WorkspaceInputException(Throwable clause) { super(clause); };
	
}
